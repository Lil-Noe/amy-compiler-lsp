package amyc
package parsing

import scala.language.implicitConversions

import amyc.ast.NominalTreeModule._
import amyc.utils._
import Tokens._
import TokenKinds._

import scallion._

// The parser for Amy
object Parser extends Pipeline[Iterator[Token], Program] with Parsers {

  type Token = amyc.parsing.Token
  type Kind = amyc.parsing.TokenKind

  import Implicits._

  override def getKind(token: Token): TokenKind = TokenKind.of(token)

  val eof: Syntax[Token] = elem(EOFKind)
  def op(string: String): Syntax[Token] = elem(OperatorKind(string))
  def kw(string: String): Syntax[Token] = elem(KeywordKind(string))

  given delimiter: Conversion[String, Syntax[Token]] with {
    def apply(string: String): Syntax[Token] = elem(DelimiterKind(string))
  }

  // An entire program (the starting rule for any Amy file).
  lazy val program: Syntax[Program] = many1(many1(module) ~<~ eof).map(ms =>
    Program(ms.flatten.toList).setPos(ms.head.head)
  )

  // A module (i.e., a collection of definitions and an initializer expression)
  lazy val module: Syntax[ModuleDef] =
    (
      kw("object") ~ identifier ~ many(definition) ~ opt(expr) ~ kw("end") ~ identifier
    ).map { 
      case kw ~ id ~ defs ~ body ~ _ ~ id1 =>
        if id == id1 then ModuleDef(id, defs.toList, body).setPos(kw)
        else
          throw new AmycFatalError(
            "Begin and end module names do not match: " + id + " and " + id1
          )
    }

  // An identifier.
  val identifier: Syntax[String] = accept(IdentifierKind) {
    case IdentifierToken(name) => name
  }

  // An identifier along with its position.
  val identifierPos: Syntax[(String, Position, Position)] = accept(IdentifierKind) {
    case id @ IdentifierToken(name) => (name, id.startPosition, id.endPosition)
  }

  // A definition within a module.
  lazy val definition: Syntax[ClassOrFunDef] =
    abstractClassDefinition | caseClassDefinition | functionDefinition

  // An abstract class definition.
  lazy val abstractClassDefinition: Syntax[ClassOrFunDef] =
    (
      kw("abstract") ~ kw("class") ~ identifier
    ).map { 
      case kw ~ _ ~ id =>
        AbstractClassDef(id).setPos(kw)
    }

  // A case class definition.
  lazy val caseClassDefinition: Syntax[ClassOrFunDef] =
    (
      kw("case") ~ kw("class") ~ identifier 
      ~ "(" ~ parameters ~ ")" 
      ~ kw("extends") ~ identifier
    ).map {
      case kw ~ _ ~ id ~ _ ~ params ~ _ ~ _ ~ parent =>
        CaseClassDef(id, params.map(_.tt), parent).setPos(kw)
    }

  // A function definition
  lazy val functionDefinition: Syntax[ClassOrFunDef] =
    (
      kw("def") ~ identifierPos 
      ~ "(" ~ parameters ~ ")" 
      ~ ":" ~ typeTree ~ "=" 
      ~ "{" ~ expr ~ "}"
    ).map {
      case kw ~ (id, startPos, endPos) ~ _ ~ params ~ _ ~ _ ~ fnType ~ _ ~ _ ~ body ~ _ =>
        FunDef(id, params, fnType, body).setPos(startPos, endPos)
    }

  // A list of parameter definitions.
  lazy val parameters: Syntax[List[ParamDef]] =
    repsep(parameter, ",").map(_.toList)

  // A parameter definition, i.e., an identifier along with the expected type.
  lazy val parameter: Syntax[ParamDef] =
    (identifierPos ~ ":" ~ typeTree).map { 
      case (id, startPos, endPos) ~ _ ~ tt => ParamDef(id, tt).setPos(startPos, endPos)
    }

  // A type expression.
  lazy val typeTree: Syntax[TypeTree] = primitiveType | identifierType

  // A built-in type (such as `Int`).
  val primitiveType: Syntax[TypeTree] =
    (accept(PrimTypeKind) { case tk @ PrimTypeToken(name) =>
      TypeTree(name match {
        case "Unit"    => UnitType
        case "Boolean" => BooleanType
        case "Int"     => IntType
        case "String"  => StringType
        case _         => throw new java.lang.Error("Unexpected primitive type name: " + name)
      }).setPos(tk)
    } ~ opt("(" ~ literal ~ ")")).map {
      case (prim @ TypeTree(IntType)) ~ Some(_ ~ IntLiteral(32) ~ _) => prim
      case TypeTree(IntType) ~ Some(_ ~ IntLiteral(width) ~ _) =>
        throw new AmycFatalError(
          "Int type can only be used with a width of 32 bits, found : " + width
        )
      case TypeTree(IntType) ~ Some(_ ~ lit ~ _) =>
        throw new AmycFatalError(
          "Int type should have an integer width (only 32 bits is supported)"
        )
      case TypeTree(IntType) ~ None =>
        throw new AmycFatalError(
          "Int type should have a specific width (only 32 bits is supported)"
        )
      case prim ~ Some(_) =>
        throw new AmycFatalError("Only Int type can have a specific width")
      case prim ~ None => prim
    }

  // A user-defined type (such as `List`).
  lazy val identifierType: Syntax[TypeTree] =
    (identifierPos ~ opt("." ~ identifier)) map {
      case (id, startPos, endPos) ~ None => 
        TypeTree(ClassType(QualifiedName(None, id)))
        .setPos(startPos, endPos)
      case (mod, startPos, endPos) ~ Some(_ ~ id) =>
        TypeTree(ClassType(QualifiedName(Some(mod), id)))
        .setPos(startPos, endPos)
    }

  // An expression.
  // HINT: You can use `operators` to take care of associativity and precedence
  lazy val expr: Syntax[Expr] = recursive {
    letExpr | semiColExpr
  }

  // A val declaration expression.
  lazy val letExpr: Syntax[Expr] =
    (
      kw("val") ~ parameter ~ "=" ~ ifOrMatchExpr ~ ";" ~ expr
    ).map { case _ ~ nameAndType ~ _ ~ value ~ _ ~ body =>
      Let(nameAndType, value, body).setPos(nameAndType)
    }

  // A ; operator expression.
  lazy val semiColExpr: Syntax[Expr] =
    (ifOrMatchExpr ~ opt((";" ~ expr))) map {
      case exp ~ None           => exp.setPos(exp)
      case exp ~ Some(_ ~ body) => Sequence(exp, body).setPos(exp)
    }

  // Either an if or a binary expression, optionally matched on X times.
  lazy val ifOrMatchExpr: Syntax[Expr] =
    ((ifExpr | binaryExpr) ~ opt(
      many1(kw("match") ~ "{" ~ many1(matchCase) ~ "}")
    )) map {
      case exp ~ None => exp.setPos(exp)
      case exp ~ Some(
            matches
          ) => // Makes sure to compute each match expr in order
        matches.foldLeft(exp) { case (acc, (_ ~ _ ~ matchCases ~ _)) =>
          Match(acc, matchCases.toList).setPos(exp)
        }
    }

  // A case for a match expression.
  lazy val matchCase: Syntax[MatchCase] =
    (kw("case") ~ pattern ~ "=>" ~ expr) map {
      case kw ~ pat ~ _ ~ exp => MatchCase(pat, exp).setPos(kw)
    }

  // An 'if () then {} else {}' expression.
  lazy val ifExpr: Syntax[Expr] =
    (kw("if") ~ "(" ~ expr ~ ")" ~ "{" ~ expr ~ "}" ~ kw("else") ~ "{" ~ expr ~ "}").map { case kw ~ _ ~ cond ~ _ ~ _ ~ thenn ~ _ ~ _ ~ _ ~ elze ~ _ =>
      Ite(cond, thenn, elze).setPos(kw)
    }

  def acceptFromString(s: String) = accept(OperatorKind(s)) { case _ => s }

  val times: Syntax[String] = acceptFromString("*")
  val div: Syntax[String] = acceptFromString("/")
  val mod: Syntax[String] = acceptFromString("%")
  val plus: Syntax[String] = acceptFromString("+")
  val minus: Syntax[String] = acceptFromString("-")
  val concat: Syntax[String] = acceptFromString("++")
  val lessThan: Syntax[String] = acceptFromString("<")
  val lessEquals: Syntax[String] = acceptFromString("<=")
  val equals: Syntax[String] = acceptFromString("==")
  val and: Syntax[String] = acceptFromString("&&")
  val or: Syntax[String] = acceptFromString("||")

  // An expression that takes 2 operands.
  lazy val binaryExpr: Syntax[Expr] =
    operators(unaryExpr)(
      (times | div | mod).is(LeftAssociative),
      (plus | minus | concat).is(LeftAssociative),
      (lessThan | lessEquals).is(LeftAssociative),
      (equals).is(LeftAssociative),
      (and).is(LeftAssociative),
      (or).is(LeftAssociative)
    ) {
      case (lhs, "*", rhs)  => Times(lhs, rhs)
      case (lhs, "/", rhs)  => Div(lhs, rhs)
      case (lhs, "%", rhs)  => Mod(lhs, rhs)
      case (lhs, "+", rhs)  => Plus(lhs, rhs)
      case (lhs, "-", rhs)  => Minus(lhs, rhs)
      case (lhs, "++", rhs) => Concat(lhs, rhs)
      case (lhs, "<", rhs)  => LessThan(lhs, rhs)
      case (lhs, "<=", rhs) => LessEquals(lhs, rhs)
      case (lhs, "==", rhs) => Equals(lhs, rhs)
      case (lhs, "&&", rhs) => And(lhs, rhs)
      case (lhs, "||", rhs) => Or(lhs, rhs)
      case _ => throw new AmycFatalError("Unexpected binary operator in expression")
    }

  // An expression that takes 1 operand.
  lazy val unaryExpr: Syntax[Expr] =
    (opt((op("!") | op("-"))) ~ simpleExpr).map {
      case Some(opToken) ~ e if opToken == OperatorToken("!") => Not(e)
      case Some(opToken) ~ e if opToken == OperatorToken("-") => Neg(e)
      case None ~ e                                           => e
      case e =>
        throw new AmycFatalError("Unexpected unary operator: " + e)
    }

  // A literal expression.
  lazy val literal: Syntax[Literal[?]] =
    accept(LiteralKind) {
      case BoolLitToken(v)   => BooleanLiteral(v)
      case IntLitToken(v)    => IntLiteral(v)
      case StringLitToken(v) => StringLiteral(v)
      // Unit literal taken care of by parenExpr
    }

  // A pattern as part of a match case.
  lazy val pattern: Syntax[Pattern] = recursive {
    literalPattern | wildPattern | caseClassPattern | unitPattern
  }

  // A pattern composed of a literal.
  lazy val literalPattern: Syntax[Pattern] =
    literal.map(LiteralPattern(_))

  // The wildcard pattern.
  lazy val wildPattern: Syntax[Pattern] =
    kw("_").map { 
      case _ =>
        WildcardPattern()
    }

  // The Unit pattern, as it's not in the literal rule.
  lazy val unitPattern: Syntax[Pattern] =
    ( "(" ~ ")" ).map { 
      case _ ~ _ =>
        LiteralPattern(UnitLiteral())
    }

  // A pattern composed of a case class.
  lazy val caseClassPattern: Syntax[Pattern] =
    (identifierPos ~ opt("." ~ identifier) ~ opt(
      "(" ~ patterns ~ ")"
    )).map {
      // A simple variable pattern.
      case (id, startPos, endPos) ~ None ~ None => IdPattern(id).setPos(startPos, endPos)

      // A simple function pattern.
      case (fn, startPos, endPos) ~ None ~ Some(_ ~ patterns ~ _) =>
        CaseClassPattern(QualifiedName(None, fn), patterns).setPos(startPos, endPos)

      // Another module's function pattern.
      case (mod, startPos, endPos) ~ Some(_ ~ fn) ~ Some(_ ~ patterns ~ _) =>
        CaseClassPattern(QualifiedName(Some(mod), fn), patterns).setPos(startPos, endPos)

      case p => throw new AmycFatalError("Unexpected pattern: " + p)
    }

  // A repetition of patterns separated by commas.
  lazy val patterns: Syntax[List[Pattern]] = repsep(pattern, ",").map(_.toList)

  // HINT: It is useful to have a restricted set of expressions that don't include any more operators on the outer level.
  lazy val simpleExpr: Syntax[Expr] =
    literal.up[Expr] | variableOrCall | parenExpr | errorExpr

  // Either a variable or a function call.
  lazy val variableOrCall: Syntax[Expr] =
    (identifierPos ~ opt("." ~ identifierPos) 
    ~ opt("(" ~ args ~ ")"))
    .map {
      // A simple var.
      case (id, startPos, endPos) ~ None ~ None => Variable(id).setPos(startPos, endPos)

      // A simple function call.
      case (fn, startPos, endPos) ~ None ~ Some(_ ~ args ~ _) => Call(QualifiedName(None, fn), args).setPos(startPos, endPos)

      // Another module's function call.
      case (mod, modStartPos, _) ~ Some(_ ~ (fn, _, fnEndPos)) ~ Some(_ ~ args ~ _) =>
        Call(QualifiedName(Some(mod), fn), args).setPos(modStartPos, fnEndPos)

      case e => throw new AmycFatalError("Unexpected call: " + e)
    }

  // A function call arguments.
  lazy val args: Syntax[List[Expr]] = repsep(expr, ",").map(_.toList)

  // Either a parenthesised expression or the Unit literal - important for LL1.
  lazy val parenExpr =
    ( "(" ~ opt(expr) ~ ")" ) map {
      case _ ~ None ~ _      => UnitLiteral()
      case _ ~ Some(exp) ~ _ => exp
    }

  // The error expression.
  lazy val errorExpr: Syntax[Expr] =
    (kw("error") ~ "(" ~ expr ~ ")") map {
      case _ ~ _ ~ exp ~ _ => Error(exp)
    }

  // Ensures the grammar is in LL(1)
  lazy val checkLL1: Boolean = {
    if (program.isLL1) {
      true
    } else {
      // Set `showTrails` to true to make Scallion generate some counterexamples for you.
      // Depending on your grammar, this may be very slow.
      val showTrails = true
      debug(program, showTrails)
      false
    }
  }

  override def run(ctx: Context)(tokens: Iterator[Token]): Program = {
    import ctx.reporter._
    if (!checkLL1) {
      ctx.reporter.fatal("Program grammar is not LL1!")
    }

    val parser = Parser(program)

    parser(tokens) match {
      case Parsed(result, rest) => result
      case UnexpectedEnd(rest)  => fatal("Unexpected end of input.")
      case UnexpectedToken(token, rest) =>
        fatal(
          "Unexpected token: " + token + ", possible kinds: " + rest.first
            .map(_.toString)
            .mkString(", ")
        )
    }
  }
}
