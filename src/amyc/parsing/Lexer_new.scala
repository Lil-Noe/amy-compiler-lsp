package amyc.parsing

import amyc.utils._
import java.io.File

import amyc.utils.Position

import com.ziplex.lexer.TokenValue
import com.ziplex.lexer.Sequence
import com.ziplex.lexer.seqFromList
import com.ziplex.lexer.emptySeq
import com.ziplex.lexer.TokenValueInjection
import com.ziplex.lexer.Rule
import com.ziplex.lexer.VerifiedRegex.Regex
import amyc.parsing.Tokens.ErrorToken
import scala.collection.mutable.ArrayBuffer


// The lexer for Amy.
object AmyLexer extends Pipeline[List[File], Iterator[Token]] {
  import amyc.utils.RegexUtils._
  import ZiplexTokens._
  
  // Keywords,
  def keywordRegex(): Regex[Char] = "abstract".r |
                                    "case".r |
                                    "class".r |
                                    "def".r |
                                    "else".r |
                                    "extends".r |
                                    "if".r |
                                    "then".r |
                                    "match".r |
                                    "object".r |
                                    "val".r |
                                    "error".r |
                                    "_".r |
                                    "end".r
  val keywordRule = Rule(regex = keywordRegex(), tag = "keyword", isSeparator = false, transformation = KeywordValueInjection.injection)

  // Primitive type names
  def primitivTypeRegex(): Regex[Char] = "Int".r | "Boolean".r | "String".r | "Unit".r

  val primitiveTypeRule = Rule(
    regex = primitiveTypeRegex(), 
    tag = "primType", 
    isSeparator = false, 
    transformation = PrimitiveTypeValueInjection.injection
    )
  
  // Boolean literals,
  val booleanLiteralRule = Rule(
    regex = "true".r | "false".r, 
    tag = "boolLit", 
    isSeparator = false, 
    transformation = BooleanLiteralValueInjection.injection
    )

  // Operators,
  val operatorRule = Rule(
    regex = "!-%/*+<".anyOf | "++".r | "<=".r | "==".r | "&&".r | "||".r,
    tag = "operator",
    isSeparator = false,
    transformation = OperatorValueInjection.injection
  )

  // Identifiers,
  val identifierRule = Rule(
    regex = azAZ ~ (azAZ | digits | '_'.r).*,
    tag = "identifier",
    isSeparator = false,
    transformation = IdentifierValueInjection.injection
  )

  // Integer literal,
  val integerLiteralRule = Rule(
    regex = digits.+,
    tag = "integer",
    isSeparator = false,
    transformation = IntegerValueInjection.injection
  )

  // String literal,
  val stringContent: Regex[Char] = allString.filter(c => c != '"' && c != '\n').anyOf
  val stringLiteralRule = Rule(
    regex = '"'.r ~ stringContent.* ~ '"'.r,
    tag = "string",
    isSeparator = false,
    transformation = StringLiteralValueInjection.injection
  )

  // Delimiters,
  val delimiterRule = Rule(
    regex = ".,:;(){}[]=".anyOf | "=>".r,
    tag = "delimiter",
    isSeparator = false,
    transformation = DelimiterValueInjection.injection
  )

  // Whitespaces,
  val whitespaceRule = Rule(
    regex = whiteSpaces.+,
    tag = "space",
    isSeparator = false,
    transformation = WhitespaceValueInjection.injection
  )

  // Single-line comments,
  val singleCommentContent: Regex[Char] = allString.filter(_ != '\n').anyOf
  val singleCommentRule = Rule(
    regex = "//".r ~ singleCommentContent.*,
    tag = "singleComment",
    isSeparator = false,
    transformation = CommentValueInjection.injection
  )

  // Multi-line comments,
  // Amy does not support nested multi-line comments (e.g. `/* foo /* bar */ */`)
  val notStar = allString.filter(_ != '*').anyOf
  val notStarOrSlash = allString.filter(c => c != '*' && c != '/').anyOf
  val multiCommentRegex = "/*".r ~ (notStar | ('*'.r.+ ~ notStarOrSlash)).* ~ opt('*'.r.+ ~ '/'.r)
  val multiCommentRule = Rule(
    regex = multiCommentRegex, 
    tag = "multiComment", 
    isSeparator = false, 
    transformation = CommentValueInjection.injection
    )


  val rules = stainless.collection.List(
      keywordRule,
      primitiveTypeRule,
      booleanLiteralRule,
      operatorRule,
      identifierRule,
      integerLiteralRule,
      stringLiteralRule,
      delimiterRule,
      whitespaceRule,
      singleCommentRule,
      multiCommentRule
  )
  /**
    * Converts a Ziplex token to an Amy token, filtering out whitespace and comments.
    * When the Ziplex token cannot be converted, returns an ErrorToken with the appropriate message.
    * 
    *
    * @param pt
    * @return
    */
  def toAmyToken(pt: (Position, com.ziplex.lexer.Token[Char])): Option[Token] =
    val (pos, token) = pt
    token.rule match
        case _ if token.rule == keywordRule => 
            token.value match
                case KeywordValue(value) => Some(Tokens.KeywordToken(value.mkString("")).setPos(pos))
        case _ if token.rule == primitiveTypeRule =>
            token.value match
                case PrimitiveTypeValue(value) => Some(Tokens.PrimTypeToken(value.mkString("")).setPos(pos))
        case _ if token.rule == booleanLiteralRule =>
            token.value match
                case BooleanLiteralValue.True  => Some(Tokens.BoolLitToken(true).setPos(pos))
                case BooleanLiteralValue.False => Some(Tokens.BoolLitToken(false).setPos(pos))
        case _ if token.rule == operatorRule =>
            token.value match
                case OperatorValue(name) => Some(Tokens.OperatorToken(name.mkString("")).setPos(pos))
        case _ if token.rule == identifierRule =>
            token.value match
                case IdentifierValue(name) => Some(Tokens.IdentifierToken(name.mkString("")).setPos(pos))
        case _ if token.rule == integerLiteralRule =>
            token.value match
                case IntegerValue(value) =>
                    val str = value.mkString("")
                    try {
                        Some(Tokens.IntLitToken(str.toInt).setPos(pos))
                    } catch {
                        case _: NumberFormatException =>
                            Some(Tokens.ErrorToken(str).setPos(pos))
                    }
        case _ if token.rule == stringLiteralRule =>
            token.value match
                case StringLiteralValue(value) => 
                    // remove surrounding quotes
                    val str = value.tail.init.mkString("")
                    Some(Tokens.StringLitToken(str).setPos(pos))
        case _ if token.rule == delimiterRule =>
            token.value match
                case DelimiterValue(value) => Some(Tokens.DelimiterToken(value.mkString("")).setPos(pos))
        case _ if token.rule == singleCommentRule => 
            None // Ignore single line comments
        case _ if token.rule == multiCommentRule =>
            token.value match
                case CommentValue(value) =>
                    val str = value.mkString("")
                    if (str.endsWith("*/")) None // Valid comment, ignore it
                    else Some(Tokens.ErrorToken(s"Unclosed multi-line comment: '$str'").setPos(pos))
        case _ if token.rule == whitespaceRule => 
            None // Ignore whitespace
        case _ =>
            None
    end match
  end toAmyToken

  override def run(ctx: amyc.utils.Context)(files: List[File]): Iterator[Token] = {
    import amyc.utils.ZipLexUtils.foreach

    var resTokens: ArrayBuffer[Token] = ArrayBuffer.empty
    val rules = AmyLexer.rules
    assert(ZipLexUtils.checkRulesValidity(rules))

    for file <- files do
      val source = scala.io.Source.fromFile(file)
      val input = try source.mkString.toStainless finally source.close()

      val (tokens, suffix) = ZipLexUtils.lex(rules, input)
      var (withPositions, nextPos) = ZipLexUtils.addPositions(tokens, file)
      val currentFileTokens = ArrayBuffer.empty[Token]
      withPositions.foreach(t => {
        AmyLexer.toAmyToken(t) match {
          case Some(token) => 
            currentFileTokens.append(token)
          case None => ()
        }
      })
      if !suffix.isEmpty then
        val errorPos = SourcePosition(file, nextPos.line, (input.size - suffix.size + 1).toInt)
        currentFileTokens.append(ErrorToken(s"Unrecognized token starting with: '${suffix.efficientList.mkString("").take(10)}'").setPos(errorPos))
        nextPos = ZipLexUtils.nextPosition(nextPos, suffix)
      end if
      currentFileTokens.append(Tokens.EOFToken().setPos(nextPos))
    
      resTokens ++= (currentFileTokens.map {
        case token@ErrorToken(msg) =>
            ctx.reporter.fatal("Unknown token at " + token.position + ": " + msg)
        case token => token
      })
    end for
    resTokens.toIterator
  }
}

/** Extracts all tokens from input and displays them */
object DisplayTokens extends Pipeline[Iterator[Token], Unit] {
  override def run(ctx: Context)(tokens: Iterator[Token]): Unit = {
    tokens.foreach(println(_))
  }
}


object ZiplexTokens {
  import stainless.collection.List

  case class IntegerValue(text: List[Char]) extends TokenValue
  case class IdentifierValue(value: List[Char]) extends TokenValue
  case class KeywordValue(value: List[Char]) extends TokenValue
  case class PrimitiveTypeValue(value: List[Char]) extends TokenValue
  enum BooleanLiteralValue extends TokenValue:
      case True
      case False
      case Broken(value: List[Char])
  case class OperatorValue(value: List[Char]) extends TokenValue
  case class StringLiteralValue(value: List[Char]) extends TokenValue
  case class DelimiterValue(value: List[Char]) extends TokenValue
  case class WhitespaceValue(value: List[Char]) extends TokenValue
  case class CommentValue(value: List[Char]) extends TokenValue

  case object IntegerValueInjection:
      def toValue(v: Sequence[Char]): TokenValue = 
          val list = v.efficientList
          IntegerValue(list)
      def toCharacters(t: TokenValue): Sequence[Char] = t match
              case IntegerValue(text) => seqFromList(text)
              case _ => emptySeq()
      
      val injection: TokenValueInjection[Char] = TokenValueInjection(toValue, toCharacters)
  end IntegerValueInjection

  case object IdentifierValueInjection:
      def toValue(v: Sequence[Char]): TokenValue = IdentifierValue(v.efficientList)
      def toCharacters(t: TokenValue): Sequence[Char] = t match
          case IdentifierValue(value) => seqFromList(value)
          case _ => emptySeq()
      
      val injection: TokenValueInjection[Char] = TokenValueInjection(toValue, toCharacters)
  end IdentifierValueInjection

  // forall v: Sequence[Char], toCharacters(toValue(l)) == l
  case object KeywordValueInjection:
      def toValue(c: Sequence[Char]): TokenValue = KeywordValue(c.efficientList)
      def toCharacters(t: TokenValue): Sequence[Char] = t match
          case KeywordValue(value) => seqFromList(value)
          case _ => emptySeq()
      val injection: TokenValueInjection[Char] = TokenValueInjection(toValue, toCharacters)
  end KeywordValueInjection

  case object PrimitiveTypeValueInjection:
      def toValue(v: Sequence[Char]): TokenValue = PrimitiveTypeValue(v.efficientList)
      def toCharacters(t: TokenValue): Sequence[Char] = t match
          case PrimitiveTypeValue(value) => seqFromList(value)
          case _ => emptySeq()

      val injection: TokenValueInjection[Char] = TokenValueInjection(toValue, toCharacters)
  end PrimitiveTypeValueInjection

  lazy val stringTrue: List[Char] = List('t', 'r', 'u', 'e')    
  lazy val stringFalse: List[Char] = List('f', 'a', 'l', 's', 'e')
  lazy val stringTrueConc: Sequence[Char] = seqFromList(stringTrue)
  lazy val stringFalseConc: Sequence[Char] = seqFromList(stringFalse)
  case object BooleanLiteralValueInjection:
      def toValue(v: Sequence[Char]): TokenValue = v.efficientList match
          case l if l == stringTrue => BooleanLiteralValue.True
          case l if l == stringFalse => BooleanLiteralValue.False
          case l => BooleanLiteralValue.Broken(l)
      def toCharacters(t: TokenValue): Sequence[Char] = t match
          case BooleanLiteralValue.True => seqFromList(stringTrue)
          case BooleanLiteralValue.False => seqFromList(stringFalse)
          case BooleanLiteralValue.Broken(value) => seqFromList(value)
          case _ => emptySeq()
      val injection: TokenValueInjection[Char] = TokenValueInjection(toValue, toCharacters)
  end BooleanLiteralValueInjection

  case object OperatorValueInjection:
    def toValue(v: Sequence[Char]): TokenValue = OperatorValue(v.efficientList)
      def toCharacters(t: TokenValue): Sequence[Char] = t match
          case OperatorValue(value) => seqFromList(value)
          case _ => emptySeq()
      val injection: TokenValueInjection[Char] = TokenValueInjection(toValue, toCharacters)
  end OperatorValueInjection

  case object StringLiteralValueInjection:
      def toValue(v: Sequence[Char]): TokenValue = StringLiteralValue(v.efficientList)
      def toCharacters(t: TokenValue): Sequence[Char] =
          t match
              case StringLiteralValue(value) => seqFromList(value)
              case _ => emptySeq()

      val injection: TokenValueInjection[Char] = TokenValueInjection(toValue, toCharacters)
  end StringLiteralValueInjection

  case object DelimiterValueInjection:
      def toValue(v: Sequence[Char]): TokenValue = DelimiterValue(v.efficientList)
      def toCharacters(t: TokenValue): Sequence[Char] = t match
          case DelimiterValue(value) => seqFromList(value)
          case _ => emptySeq()
      
      val injection: TokenValueInjection[Char] = TokenValueInjection(toValue, toCharacters)
  end DelimiterValueInjection

  case object WhitespaceValueInjection:
      def toValue(v: Sequence[Char]): TokenValue = WhitespaceValue(v.efficientList)
      def toCharacters(t: TokenValue): Sequence[Char] = 
          t match
              case WhitespaceValue(value) => seqFromList(value)
              case _ => emptySeq()
      val injection: TokenValueInjection[Char] = TokenValueInjection(toValue, toCharacters)
  end WhitespaceValueInjection

  case object CommentValueInjection:
      def toValue(v: Sequence[Char]): TokenValue = CommentValue(v.efficientList)
      def toCharacters(t: TokenValue): Sequence[Char] = 
          t match
              case CommentValue(value) => seqFromList(value)
              case _ => emptySeq()
      val injection: TokenValueInjection[Char] = TokenValueInjection(toValue, toCharacters)
  end CommentValueInjection
}