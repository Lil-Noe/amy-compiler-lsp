package amyc
package interpreter

import utils._
import ast.SymbolicTreeModule._
import ast.Identifier
import analyzer.SymbolTable

// An interpreter for Amy programs, implemented in Scala
object Interpreter extends Pipeline[(Program, SymbolTable), Unit] {

  // A class that represents a value computed by interpreting an expression
  abstract class Value {
    def asInt: Int = this.asInstanceOf[IntValue].i
    def asBoolean: Boolean = this.asInstanceOf[BooleanValue].b
    def asString: String = this.asInstanceOf[StringValue].s

    override def toString: String = this match {
      case IntValue(i) => i.toString
      case BooleanValue(b) => b.toString
      case StringValue(s) => s
      case UnitValue => "()"
      case CaseClassValue(constructor, args) =>
        constructor.name + "(" + args.map(_.toString).mkString(", ") + ")"
    }
  }
  case class IntValue(i: Int) extends Value
  case class BooleanValue(b: Boolean) extends Value
  case class StringValue(s: String) extends Value
  case object UnitValue extends Value
  case class CaseClassValue(constructor: Identifier, args: List[Value]) extends Value

  def run(ctx: Context)(v: (Program, SymbolTable)): Unit = {
    val (program, table) = v

    // These built-in functions do not have an Amy implementation in the program,
    // instead their implementation is encoded in this map
    val builtIns: Map[(String, String), (List[Value]) => Value] = Map(
      ("Std", "printInt")    -> { args => println(args.head.asInt); UnitValue },
      ("Std", "printString") -> { args => println(args.head.asString); UnitValue },
      ("Std", "readString")  -> { args => StringValue(scala.io.StdIn.readLine()) },
      ("Std", "readInt")     -> { args =>
        val input = scala.io.StdIn.readLine()
        try {
          IntValue(input.toInt)
        } catch {
          case ne: NumberFormatException =>
            ctx.reporter.fatal(s"""Could not parse "$input" to Int""")
        }
      },
      ("Std", "intToString")   -> { args => StringValue(args.head.asInt.toString) },
      ("Std", "digitToString") -> { args => StringValue(args.head.asInt.toString) }
    )

    // Utility functions to interface with the symbol table.
    def isConstructor(name: Identifier) = table.getConstructor(name).isDefined
    def findFunctionOwner(functionName: Identifier) = table.getFunction(functionName).get.owner.name
    def findFunction(owner: String, name: String) = {
      program.modules.find(_.name.name == owner).get.defs.collectFirst {
        case fd@FunDef(fn, _, _, _) if fn.name == name => fd
      }.get
    }

    // Interprets a function, using evaluations for local variables contained in 'locals'
    def interpret(expr: Expr)(implicit locals: Map[Identifier, Value]): Value = {
      expr match {

        // Literals and Variables
        case Variable(name)    => locals(name)
        case IntLiteral(i)     => IntValue(i)
        case BooleanLiteral(b) => BooleanValue(b)
        case StringLiteral(s)  => StringValue(s)
        case UnitLiteral()     => UnitValue


        // Arithmetic Operators
        case Plus(lhs, rhs)  => IntValue(interpret(lhs).asInt + interpret(rhs).asInt)
        case Minus(lhs, rhs) => IntValue(interpret(lhs).asInt - interpret(rhs).asInt)
        case Times(lhs, rhs) => IntValue(interpret(lhs).asInt * interpret(rhs).asInt)
        case Neg(e)          => IntValue(-interpret(e).asInt)
        case Div(lhs, rhs)   =>
          val rhsValue = interpret(rhs).asInt
          if (rhsValue != 0) IntValue(interpret(lhs).asInt / rhsValue) else ctx.reporter.fatal("Division by zero")
        case Mod(lhs, rhs)   => 
          val rhsValue = interpret(rhs).asInt
          if (rhsValue != 0) IntValue(interpret(lhs).asInt % rhsValue) else ctx.reporter.fatal("Division by zero")
        

        // Logical Operators
        case And(lhs, rhs) => BooleanValue(interpret(lhs).asBoolean && interpret(rhs).asBoolean)
        case Or(lhs, rhs)  => BooleanValue(interpret(lhs).asBoolean || interpret(rhs).asBoolean)
        case Not(e)        => BooleanValue(!interpret(e).asBoolean)


        // Comparison Operators
        case LessThan(lhs, rhs)   => BooleanValue(interpret(lhs).asInt < interpret(rhs).asInt)
        case LessEquals(lhs, rhs) => BooleanValue(interpret(lhs).asInt <= interpret(rhs).asInt)
        case Equals(lhs, rhs)     => 
          BooleanValue((interpret(lhs), interpret(rhs)) match {
            // Compare by value for primitive types, by reference otherwise (Amy semantics)
            case (UnitValue, UnitValue)             => true
            case (IntValue(l), IntValue(r))         => l == r
            case (BooleanValue(l), BooleanValue(r)) => l == r
            case (l, r)                             => l eq r
          })
        

        // String Concatenation
        case Concat(lhs, rhs) => StringValue(interpret(lhs).asString + interpret(rhs).asString)


        // Function and Objects
        case Call(qname, args) => 
          if (isConstructor(qname)) CaseClassValue(qname, args map interpret)
          else {
            val (funcOwner, funcName) = (findFunctionOwner(qname), qname.name)
            builtIns.get((funcOwner, funcName)) match {
              case Some(func) => func(args map interpret)
              case None       => 
                val func = findFunction(funcOwner, funcName)
                interpret(func.body)(locals ++ (func.paramNames zip args.map(interpret)))
            }
          }
        

        // Control-Flow
        case Sequence(e1, e2)       => interpret(e1); interpret(e2)
        case Let(df, value, body)   => interpret(body)(locals + (df.name -> interpret(value)))
        case Ite(cond, thenn, elze) => if (interpret(cond).asBoolean) then interpret(thenn) else interpret(elze)


        // Error Handling
        case Error(msg) => ctx.reporter.fatal(s"Error: ${interpret(msg).asString}")
  

        // Pattern matching
        case Match(scrut, cases) =>
          val evS = interpret(scrut)

          // Checks if value matches a pattern, returns bound variables or None if it fails.
          def matchesPattern(v: Value, pat: Pattern): Option[List[(Identifier, Value)]] = {
            ((v, pat): @unchecked) match {
              case (_, WildcardPattern()) => Some(Nil) // Always matches
              case (_, IdPattern(name)) => Some((name, v) :: Nil)
              case (IntValue(i1), LiteralPattern(IntLiteral(i2))) if i1 == i2 => Some(Nil)
              case (BooleanValue(b1), LiteralPattern(BooleanLiteral(b2))) if b1 == b2 => Some(Nil)
              case (StringValue(_), LiteralPattern(StringLiteral(_))) => None // Never matches because comparing by reference (Amy semantics)
              case (UnitValue, LiteralPattern(UnitLiteral())) => Some(Nil) // Always matches
              // Match constructor name and ensure arguments pattern-match
              case (CaseClassValue(con1, realArgs), CaseClassPattern(con2, formalArgs)) if con1 == con2 =>
                val matchResults = (realArgs zip formalArgs).map((v, p) => matchesPattern(v, p))
                if (matchResults.contains(None)) None
                else Some(matchResults.flatten.flatten)
              case _ => None
            }
          }

          // Main "loop" of the implementation: Go through every case,
          // check if the pattern matches, and if so return the evaluation of the case expression
          cases
            .to(LazyList)
            .map { matchCase =>
              val MatchCase(pat, rhs) = matchCase
              (rhs, matchesPattern(evS, pat))
            }
            .find(_._2.isDefined) match {
            case Some((rhs, Some(moreLocals))) =>
              interpret(rhs)(locals ++ moreLocals)
            case _ =>
              // No case matched
              ctx.reporter.fatal(
                s"Match error: ${evS.toString}@${scrut.startPosition}"
              )
          }
        
      }
    }

    for {
      m <- program.modules
      e <- m.optExpr
    } {
      interpret(e)(Map())
    }
  }
}
