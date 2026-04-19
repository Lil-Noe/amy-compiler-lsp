package amyc
package analyzer

import amyc.utils._
import amyc.ast.SymbolicTreeModule._
import amyc.ast.Identifier

// The type checker for Amy
// Takes a symbolic program and rejects it if it does not follow the Amy typing rules.
object TypeChecker
    extends Pipeline[(Program, SymbolTable), (Program, SymbolTable)] {

  def run(ctx: Context)(v: (Program, SymbolTable)): (Program, SymbolTable) = {
    import ctx.reporter._

    val (program, table) = v

    case class Constraint(found: Type, expected: Type, pos: Position)

    // Represents a type variable.
    // It extends Type, but it is meant only for internal type checker use,
    //  since no Amy value can have such type.
    case class TypeVariable private (id: Int) extends Type
    object TypeVariable {
      private val c = new UniqueCounter[Unit]
      def fresh(): TypeVariable = TypeVariable(c.next(()))
    }

    // Generates typing constraints for an expression `e` with a given expected type.
    // The environment `env` contains all currently available bindings (you will have to
    //  extend these, e.g., to account for local variables).
    // Returns a list of constraints among types. These will later be solved via unification.
    def genConstraints(e: Expr, expected: Type)(implicit
        env: Map[Identifier, Type]
    ): List[Constraint] = {

      // This helper returns a list of a single constraint recording the type
      //  that we found (or generated) for the current expression `e`
      def topLevelConstraint(found: Type): List[Constraint] =
        List(Constraint(found, expected, e.startPosition))

      e match {
        // Literals
        case BooleanLiteral(_) => topLevelConstraint(BooleanType)
        case UnitLiteral() => topLevelConstraint(UnitType)
        case StringLiteral(_) => topLevelConstraint(StringType)
        case IntLiteral(_) => topLevelConstraint(IntType)


        // Variables
        case Variable(name) =>
          env.get(name) match {
            case Some(tpe) => topLevelConstraint(tpe)
            case None => ctx.reporter.fatal(s"Unknown variable : $name", e.startPosition)
          }


        // Arithmetic Operators
        case Plus(lhs, rhs) =>
          genConstraints(lhs, IntType) ++
          genConstraints(rhs, IntType) ++
          topLevelConstraint(IntType)
        case Minus(lhs, rhs) =>
          genConstraints(lhs, IntType) ++
          genConstraints(rhs, IntType) ++
          topLevelConstraint(IntType)
        case Times(lhs, rhs) =>
          genConstraints(lhs, IntType) ++
          genConstraints(rhs, IntType) ++
          topLevelConstraint(IntType)
        case Neg(e) =>
          genConstraints(e, IntType) ++
          topLevelConstraint(IntType)
        case Div(lhs, rhs) =>
          genConstraints(lhs, IntType) ++
          genConstraints(rhs, IntType) ++
          topLevelConstraint(IntType)
        case Mod(lhs, rhs) =>
          genConstraints(lhs, IntType) ++
          genConstraints(rhs, IntType) ++
          topLevelConstraint(IntType)


        // Logical Operators
        case And(lhs, rhs) =>
          genConstraints(lhs, BooleanType) ++
          genConstraints(rhs, BooleanType) ++
          topLevelConstraint(BooleanType)
        case Or(lhs, rhs) =>
          genConstraints(lhs, BooleanType) ++
          genConstraints(rhs, BooleanType) ++
          topLevelConstraint(BooleanType)
        case Not(e) =>
          genConstraints(e, BooleanType) ++
          topLevelConstraint(BooleanType)
        

        // Comparison Operators
        case LessThan(lhs, rhs) =>
          genConstraints(lhs, IntType) ++
          genConstraints(rhs, IntType) ++
          topLevelConstraint(BooleanType)
        case LessEquals(lhs, rhs) =>
          genConstraints(lhs, IntType) ++
          genConstraints(rhs, IntType) ++
          topLevelConstraint(BooleanType)
        case Equals(lhs, rhs) =>
          // Both operand must have the same (unspecified) type.
          val tv = TypeVariable.fresh()
          genConstraints(lhs, tv) ++
          genConstraints(rhs, tv) ++
          topLevelConstraint(BooleanType)


        // String Concatenation
        case Concat(lhs, rhs) =>
          genConstraints(lhs, StringType) ++
          genConstraints(rhs, StringType) ++
          topLevelConstraint(StringType)

        
        // Function and Objects
        case Call(qname, args) =>
          table.getFunction(qname) match {

            case Some(sig) =>
              if (args.length != sig.argTypes.length)
                ctx.reporter.fatal("Wrong number of arguments", e.startPosition)
              val argConstraints = args.zip(sig.argTypes).flatMap { 
                  case (argExpr, expectedType) => genConstraints(argExpr, expectedType)
                }
              argConstraints ++ topLevelConstraint(sig.retType)

            case None => 
              table.getConstructor(qname) match {
                case Some(constrSig) =>
                  if (args.length != constrSig.argTypes.length)
                    ctx.reporter.fatal("Wrong number of arguments", e.startPosition)
                  val argConstraints = args.zip(constrSig.argTypes).flatMap {
                    case (argExpr, expectedType) => genConstraints(argExpr, expectedType)
                  }
                  argConstraints ++ topLevelConstraint(constrSig.retType)

                case None => ctx.reporter.fatal("Unknown function or constructor", e.startPosition)
              }
          }


        // Control-Flow
        case Sequence(e1, e2) =>
          // First element can have any type, but must typecheck.
          val tv = TypeVariable.fresh()
          genConstraints(e1, tv) ++
          genConstraints(e2, expected)
        case Let(df, value, body) =>
          // Variable only exists in the body.
          genConstraints(value, df.tt.tpe) ++
          genConstraints(body, expected)(env + (df.name -> df.tt.tpe))
        case Ite(cond, thenn, elze) =>
          // Must return the same type as the caller.
          genConstraints(cond, BooleanType) ++
          genConstraints(thenn, expected) ++
          genConstraints(elze, expected)


        // Error Handling
        case Error(msg) =>
          // Useless to specify top level constraint, as any type is valid.
          genConstraints(msg, StringType) ++ topLevelConstraint(expected)


        // Pattern Matching
        case Match(scrut, cases) =>
          // Returns additional constraints from within the pattern with all bindings
          // from identifiers to types for names bound in the pattern.
          // (This is analogous to `transformPattern` in NameAnalyzer.)
          def patternBindings(
              pat: Pattern,
              expected: Type
          ): (List[Constraint], Map[Identifier, Type]) = {
            pat match
              case WildcardPattern()   => (Nil, Map())
              case IdPattern(name)     => (Nil, Map(name -> expected))
              case LiteralPattern(lit) => (genConstraints(lit, expected), Map())
              case CaseClassPattern(constr, args) =>
                val constrSig = table.getConstructor(constr).get

                val retType = constrSig.retType
                val argTypes = constrSig.argTypes
                val (argConstraints, argBindings) = args
                  .zip(argTypes)
                  .map { case (arg, tpe) => patternBindings(arg, tpe) }
                  .unzip

                (
                  List(
                    Constraint(retType, expected, e.startPosition)
                  ) ++ argConstraints.flatten,
                  argBindings.flatten.toMap
                )
          }

          def handleCase(
              cse: MatchCase,
              scrutExpected: Type
          ): List[Constraint] = {
            val (patConstraints, moreEnv) =
              patternBindings(cse.pat, scrutExpected)
            patConstraints ++ genConstraints(cse.expr, expected)(env ++ moreEnv)
          }

          val st = TypeVariable.fresh()
          genConstraints(scrut, st) ++
            cases.flatMap(cse => handleCase(cse, st))

        case _ =>
          ctx.reporter.fatal("Unrecognized token", e.startPosition)
      }
    }

    // Given a list of constraints `constraints`, replace every occurence of type variable
    //  with id `from` by type `to`.
    def subst_*(
        constraints: List[Constraint],
        from: Int,
        to: Type
    ): List[Constraint] = {
      constraints map { case Constraint(found, expected, pos) =>
        Constraint(subst(found, from, to), subst(expected, from, to), pos)
      }
    }

    // Do a single substitution.
    def subst(tpe: Type, from: Int, to: Type): Type = {
      tpe match {
        case TypeVariable(`from`) => to
        case other                => other
      }
    }

    // Solve the given set of typing constraints and report errors
    //  using `ctx.reporter.error` if they are not satisfiable.
    // We consider a set of constraints to be satisfiable exactly if they unify.
    def solveConstraints(constraints: List[Constraint]): Unit = {
      constraints match {
        case Nil                                      => ()
        case Constraint(found, expected, pos) :: more =>
          (found, expected) match {
            case (t1, t2) if t1 == t2 =>
              solveConstraints(more)
            case (TypeVariable(id), t) =>
              solveConstraints(subst_*(more, id, t))
            case (t, TypeVariable(id)) =>
              solveConstraints(subst_*(more, id, t))
            case _ =>
              ctx.reporter.error(
                s"Type mismatch: found $found, expected $expected", pos
              )
              solveConstraints(more)
          }
      }
    }

    // Putting it all together to type-check each module's functions and main expression.
    program.modules.foreach { mod =>
      mod.defs.collect { case FunDef(_, params, retType, body) =>
        val env = params.map { case ParamDef(name, tt) => name -> tt.tpe }.toMap
        solveConstraints(genConstraints(body, retType.tpe)(env))
      }

      val tv = TypeVariable.fresh()
      mod.optExpr.foreach(e => solveConstraints(genConstraints(e, tv)(Map())))
    }

    v

  }
}
