file:///C:/Users/lilia/Documents/EPFL/BA6/CLP/CS-320_Amy_LSP/lab06/src/amyc/lsp/AmyTextDocumentService.scala
### java.lang.AssertionError: assertion failed

occurred in the presentation compiler.

presentation compiler configuration:


action parameters:
uri: file:///C:/Users/lilia/Documents/EPFL/BA6/CLP/CS-320_Amy_LSP/lab06/src/amyc/lsp/AmyTextDocumentService.scala
text:
```scala
package amyc.lsp

import org.eclipse.lsp4j.services.TextDocumentService
import org.eclipse.lsp4j.DidOpenTextDocumentParams
import org.eclipse.lsp4j.DidChangeTextDocumentParams
import org.eclipse.lsp4j.DidCloseTextDocumentParams
import org.eclipse.lsp4j.DidSaveTextDocumentParams
import org.eclipse.lsp4j.jsonrpc.messages.Either
import java.util.concurrent.CompletableFuture
import org.eclipse.lsp4j.DefinitionParams
import org.eclipse.lsp4j.Location
import org.eclipse.lsp4j.LocationLink
import org.eclipse.lsp4j.Position as LSP4JPosition
import org.eclipse.lsp4j.Range as LSP4JRange
import java.util.Collections
import java.util.Optional
import java.util.List as UtilList
import java.net.URI
import java.nio.file.{Files, Path, Paths}
import amyc.utils.Position as AmyPosition
import amyc.utils.* 
import amyc.parsing.*
import java.io.File
import amyc.analyzer.*
import amyc.ast.SymbolicTreeModule.*
import amyc.ast.Identifier
import scala.compiletime.ops.boolean

/**
  * Many methods of this class are not used in this project.
  * When it happens we only inform the client the request has been received.
  *
  * @param server
  */
class AmyTextDocumentService(server: AmyLanguageServer) extends TextDocumentService {

  override def didSave(params: DidSaveTextDocumentParams): Unit = {}
    //server.sendMsgToClient("didSave TextDocument")
  override def didClose(params: DidCloseTextDocumentParams): Unit = {}
    //server.sendMsgToClient("didClose TextDocument")
  override def didChange(params: DidChangeTextDocumentParams): Unit = {}
    //server.sendMsgToClient("didChange TextDocument")
  override def didOpen(params: DidOpenTextDocumentParams): Unit = {}
    //server.sendMsgToClient("didOpen TextDocument")




  /**
   * This function is defined to find the position of a given identifier
   * 
   * @param expr
   * @param positionSource
   * @return
   */
  def lookForPosition(e: Expr, identifier: Identifier): Position = {
    e match {
        // Literals or Error
        case BooleanLiteral(_) => NoPosition
        case UnitLiteral()     => NoPosition 
        case StringLiteral(_)  => NoPosition
        case IntLiteral(_)     => NoPosition
        case Error(_)          => NoPosition

        // Variables
        case Variable(name) => NoPosition


        /* Binary Operators */

        // Usual integer operators
        case Plus(lhs, rhs)  => { lookForPosition(lhs, identifier) match {
                case NoPosition => lookForPosition(rhs, identifier)
                case pos => pos
        }} 
        case Minus(lhs, rhs) => { lookForPosition(lhs, identifier) match {
                case NoPosition => lookForPosition(rhs, identifier)
                case pos => pos
        }} 
        case Times(lhs, rhs) => { lookForPosition(lhs, identifier) match {
                case NoPosition => lookForPosition(rhs, identifier)
                case pos => pos
        }} 
        case Div(lhs, rhs)   => { lookForPosition(lhs, identifier) match {
                case NoPosition => lookForPosition(rhs, identifier)
                case pos => pos
        }} 
        case Mod(lhs, rhs)   => { lookForPosition(lhs, identifier) match {
                case NoPosition => lookForPosition(rhs, identifier)
                case pos => pos
        }} 
        
        // Usual arithmetic comparison operators
        case LessThan(lhs, rhs)   => { lookForPosition(lhs, identifier) match {
                case NoPosition => lookForPosition(rhs, identifier)
                case pos => pos
        }} 
        case LessEquals(lhs, rhs) => { lookForPosition(lhs, identifier) match {
                case NoPosition => lookForPosition(rhs, identifier)
                case pos => pos
        }} 
        
        // Boolean disjunction and conjunction
        case And(lhs, rhs) => { lookForPosition(lhs, identifier) match {
                case NoPosition => lookForPosition(rhs, identifier)
                case pos => pos
        }} 
        case Or(lhs, rhs)  => { lookForPosition(lhs, identifier) match {
                case NoPosition => lookForPosition(rhs, identifier)
                case pos => pos
        }} 
        
        // String concatenation operator
        case Concat(lhs, rhs) => { lookForPosition(lhs, identifier) match {
                case NoPosition => lookForPosition(rhs, identifier)
                case pos => pos
        }} 
        // Equality operator
        case Equals(lhs, rhs) => { lookForPosition(lhs, identifier) match {
                case NoPosition => lookForPosition(rhs, identifier)
                case pos => pos
        }} 
        // Sequence operator
        case Sequence(e1,e2) => { lookForPosition(e1, identifier) match {
                case NoPosition => lookForPosition(e2, identifier)
                case pos => pos
        }} 


        // Integer or Boolean negation
        case Neg(e) => lookForPosition(e, identifier)
        case Not(e) => lookForPosition(e, identifier)


        // If-then-else
        case Ite(cond, thenn, elze) => { 
          lookForPosition(cond, identifier) match {
                case NoPosition =>  {
                  lookForPosition(thenn, identifier) match {
                    case NoPosition => lookForPosition(elze, identifier)
                    case pos => pos
                  }
                } 
                case pos => pos
          }}


        // Local variable
        case Let(df, value, body) => {
          if (df.name == identifier) e.startPosition
          else { lookForPosition(value, identifier) match {
                case NoPosition => lookForPosition(body, identifier)
                case pos => pos
          }}
        }



        // Type or function invocation
        case Call(qname, args) => {
          // Look into each argument
          args.foreach { arg => {
            lookForPosition(arg, identifier) match { 
              case NoPosition => /* do nothing */ 
              case pos => pos
            }
          }}
          NoPosition
        }

        // Match pattern
        case Match(scrut, cases) => { 
          lookForPosition(scrut, identifier) match {
            case NoPosition => {
              // Look into each MatchCase (the expression)
              cases.foreach { matchCase =>
                lookForPosition(matchCase.expr, identifier) match { 
                  case NoPosition => /* do nothing */
                  case pos => pos 
                }
              }
              NoPosition
            }
            case pos => pos
          }
          
        }
    }
  }





  /**
   * This function is defined to compare a position between a start and an end in a file 
   */
  def positionMatch(pos: Position, startPos: Position, endPos: Position): Boolean = {
    startPos.file == pos.file && pos.file == endPos.file &&
    startPos.line <= pos.line && pos.line <= endPos.line &&
    startPos.col  <= pos.col  && pos.col  <= endPos.col
  }


  /**
  * This function is defined to recursively find the identifier pointed by positionSource 
  * in a matchCase pattern
  *
  * @param pattern
  * @param positionSource
  * @return
  */
  def lookIntoPattern(pattern: Pattern, positionSource: Position): Option[Identifier] = {
    pattern match {
      case WildcardPattern()              => None
      case IdPattern(name)                => {
        if (positionMatch(positionSource, pattern.startPosition, pattern.endPosition)) Some(name)
        else None
      }
      case LiteralPattern(_)              => None
      case CaseClassPattern(constr, args) => {
        if (positionMatch(positionSource, pattern.startPosition, pattern.endPosition)) Some(constr)
        else {
          // Look into each pattern
          args.foreach { arg => {
            lookIntoPattern(arg, positionSource) match { case Some(id) => id case _ => /* do nothing */}
          }}
          None
        }
      }
    }
  } 


  /**
    * This function is defined to recursively find the identifier pointed by positionSource.
    * 
    * The algorithm is the following :
    * - If the Expr cannot contain an identifier (i.e. literals), return None 
    * - If the Expr contains sub-expressions, go recursive
    * - If the Expr contains a direct identifier (Variable/Let/Call)
    *   check if it corresponds, else go recursive (or return None for Variable)
    *
    * @param e
    * @param positionSource
    * @return
    */
  def lookForIdentifier(e: Expr, positionSource: Position): Option[Identifier] = {
    e match {
        // Literals or Error
        case BooleanLiteral(_) => None
        case UnitLiteral()     => None 
        case StringLiteral(_)  => None
        case IntLiteral(_)     => None
        case Error(_)          => None
        

        // Variables
        case Variable(name) => {
          if (positionMatch(positionSource, e.startPosition, e.endPosition)) {
            server.sendMsgToClient(s"Identifier : $name")
            Some(name)
          } else {
            server.sendMsgToClient(s"Identifier perdu : $name")
            None
          }
        }


        /* Binary Operators */

        // Usual integer operators
        case Plus(lhs, rhs)  => { lookForIdentifier(lhs, positionSource) match {
                case Some(name) => Some(name)
                case _ => lookForIdentifier(rhs, positionSource)
        }} 
        case Minus(lhs, rhs) => { lookForIdentifier(lhs, positionSource) match {
                case Some(name) => Some(name)
                case _ => lookForIdentifier(rhs, positionSource)
        }} 
        case Times(lhs, rhs) => { lookForIdentifier(lhs, positionSource) match {
                case Some(name) => Some(name)
                case _ => lookForIdentifier(rhs, positionSource)
        }} 
        case Div(lhs, rhs)   => { lookForIdentifier(lhs, positionSource) match {
                case Some(name) => Some(name)
                case _ => lookForIdentifier(rhs, positionSource)
        }} 
        case Mod(lhs, rhs)   => { lookForIdentifier(lhs, positionSource) match {
                case Some(name) => Some(name)
                case _ => lookForIdentifier(rhs, positionSource)
        }} 
        
        // Usual arithmetic comparison operators
        case LessThan(lhs, rhs)   => { lookForIdentifier(lhs, positionSource) match {
                case Some(name) => Some(name)
                case _ => lookForIdentifier(rhs, positionSource)
        }} 
        case LessEquals(lhs, rhs) => { lookForIdentifier(lhs, positionSource) match {
                case Some(name) => Some(name)
                case _ => lookForIdentifier(rhs, positionSource)
        }} 
        
        // Boolean disjunction and conjunction
        case And(lhs, rhs) => { lookForIdentifier(lhs, positionSource) match {
                case Some(name) => Some(name)
                case _ => lookForIdentifier(rhs, positionSource)
        }} 
        case Or(lhs, rhs)  => { lookForIdentifier(lhs, positionSource) match {
                case Some(name) => Some(name)
                case _ => lookForIdentifier(rhs, positionSource)
        }} 
        
        // String concatenation operator
        case Concat(lhs, rhs) => { lookForIdentifier(lhs, positionSource) match {
                case Some(name) => Some(name)
                case _ => lookForIdentifier(rhs, positionSource)
        }} 
        // Equality operator
        case Equals(lhs, rhs) => { lookForIdentifier(lhs, positionSource) match {
                case Some(name) => Some(name)
                case _ => lookForIdentifier(rhs, positionSource)
        }} 
        // Sequence operator
        case Sequence(e1,e2) => { 
          lookForIdentifier(e1, positionSource) match {
                case Some(name) => Some(name)
                case _ => lookForIdentifier(e2, positionSource)
        }}




        // Integer or Boolean negation
        case Neg(e) => lookForIdentifier(e, positionSource)
        case Not(e) => lookForIdentifier(e, positionSource)


        // If-then-else
        case Ite(cond, thenn, elze) => { 
          lookForIdentifier(cond, positionSource) match {
                case Some(name) => Some(name)
                case _ => { 
            lookForIdentifier(thenn, positionSource) match {
                  case Some(name) => Some(name)
                  case _ => {
                    lookForIdentifier(elze, positionSource)
                  }
            }}
          }}



        // Local variable
        case Let(df, value, body) => {
          if (positionMatch(positionSource, df.startPosition, df.endPosition)) Some(df.name)
          else { 
            if (df.name.name == "div") server.sendMsgToClient(s"Div value")
            lookForIdentifier(value, positionSource) match {
                case Some(name) => Some(name)
                case _ => 
                  lookForIdentifier(body, positionSource)
          }}
        }



        // Type or function invocation
        case Call(qname, args) => {
          if (positionMatch(positionSource, e.startPosition, e.endPosition)) {
            server.sendMsgToClient("inside Call")
            Some(qname)
          }
          else { 
            // Look into each argument
            args.toStream.flatMap(arg => lookForIdentifier(arg, positionSource)).headOption
          }
        }

        // Match pattern
        case Match(scrut, cases) => { 
          lookForIdentifier(scrut, positionSource) match {
            case Some(name) => Some(name)
            case _ => {
              // Look into each MatchCase (the pattern first, then the expression)
              cases.toStream.flatMap(matchCase => 
                lookIntoPattern(matchCase.pat, positionSource)
                .orElse(lookForIdentifier(matchCase.expr, positionSource))).headOption
            }
          }
          
        }
    }
  }





  /**
    * This method implements the goto definition method on Server side.
    * 
    * It receives a position in a file from Client side, 
    * find which identifier is pointed and returns the position of the definition of this identifier.
    * 
    * If the position does not point to an identifier, this method does nothing.
    *
    * @param params
    * @return
    */
  override def definition(params: DefinitionParams): 
    CompletableFuture[Either[UtilList[? <: Location], UtilList[? <: LocationLink]]] = 
  { 

      // Get the file in which to look for identifier
      val textDoc = params.getTextDocument
      val uri = textDoc.getUri
      val path = Paths.get(URI.create(uri))
      //server.sendMsgToClient(s"ClientFile URI : $uri \n ClientFile Path : $path")


      // Find source folder to include all needed files
      var baseDir = path
      while (baseDir != null && baseDir.getFileName.toString != "test-folder") {
        baseDir = baseDir.getParent()
      }
      val libraryDir = baseDir.resolve("library")
      val filesToLookIn = Set(
        path, 
        libraryDir.resolve("List.amy"),
        libraryDir.resolve("Std.amy"),
        libraryDir.resolve("Option.amy"))
      //server.sendMsgToClient(s"File we look in : ${path.toString()}")


      // Compile the files up to NameAnalyser to have identifiers
      val ctx = new Context(new Reporter, filesToLookIn.toList.map(_.toString()))
      val files = ctx.files.map(new File(_))
      val pipeline = AmyLexer.andThen(Parser.andThen(NameAnalyzer))

      if (files.isEmpty) {
        server.sendMsgToClient("No input files")
      }
      files.find(!_.exists()).foreach { f =>
        server.sendMsgToClient(s"File not found: ${f.getName}")
      }


      val (program, table) = pipeline.run(ctx)(files)




      // Get position given by the client
      val clientPosition: LSP4JPosition = params.getPosition()
      val clientLine = clientPosition.getLine()
      val clientChar = clientPosition.getCharacter()


      // Translate position (from LSP4J to Amy format) 
      // following zero-indexing of LSP4J protocol
      val identifierPosition = new SourcePosition(
        path.toFile(), clientLine + 1, clientChar + 1)
      var definitionPosition : Position = NoPosition
      //server.sendMsgToClient(s"ClientFile Position : line $clientLine, char $clientChar")

    



      // Keep track of the wanted identifier
      var identifier : Option[Identifier] = None

      // Go through modules to :
      // 1. Find the identifier pointed by the position
      // 2. Find the position of the definition of this identifier
      program.modules.foreach { mod => {


        // Search identifier first in definitions
        mod.defs.foreach { definition => {
            if (!identifier.isDefined) {
              definition match {
                case FunDef(_,_,_,body) => {
                  if (definition.name.name == "intToString") {
                    identifier = lookForIdentifier(body, identifierPosition)
                  }
                }
                case _                  => /* do nothing for ClassDef (no Expr) */
              }
            }
        }}

        // If not found, look in the expressions
        if (!identifier.isDefined) {
          mod.optExpr.foreach { e => {
            server.sendMsgToClient(s"Look in expr")
            if (!identifier.isDefined) {
              identifier = lookForIdentifier(e, identifierPosition)
            }
          }}
        }
        server.sendMsgToClient(s"Quit looking id")




        // When corresponding identifier found, get its position
        identifier match {
          case None => {server.sendMsgToClient("Identifier not found")/* do nothing if the position did not point to an identifier */}
          case Some(name) => {
            server.sendMsgToClient("Identifier found youhou")
            
            // Search first in definitions
            mod.defs.foreach { definition => {

              // If the identifier is a Class or a function, we are done
              if (definition.name == name) definitionPosition = definition.startPosition
              else {
                definition match {
                  case FunDef(_,_,_,body) => definitionPosition = lookForPosition(body, name) 
                  case _                  => /* do nothing for ClassDef (no Expr) */
                }
              }
            }}

            // If not found, look in the expressions
            if (definitionPosition == NoPosition) {
              mod.optExpr.foreach { e => {
                  if (definitionPosition == NoPosition) {
                    definitionPosition = lookForPosition(e, name)
                  }
              }}
            }
          } 
        }
      }}

      server.sendMsgToClient(s"Def Position : line ${definitionPosition.line}, char ${definitionPosition.col}")
      if (definitionPosition.file == null) {
        server.sendMsgToClient(s"Def poistion : file not found")
        return CompletableFuture.supplyAsync(() =>Either.forLeft(Collections.emptyList()))
      }


      // Translate position (from Amy to LSP4J format) 
      // following zero-indexing of LSP4J protocol
      // (we set the start and end of position to same value)
      val range = new LSP4JRange(
        new LSP4JPosition(definitionPosition.line - 1, definitionPosition.col - 1), 
        new LSP4JPosition(definitionPosition.line - 1, definitionPosition.col - 1)
      )

      val definitionFileURI = definitionPosition.file.toURI().toString()

      server.sendMsgToClient(s"Found identifier : $identifier")
      server.sendMsgToClient(s"ServerFile URI : $definitionFileURI")

      val locationList = Collections.singletonList(
        new Location(definitionFileURI, range))

      CompletableFuture.supplyAsync(() => Either.forLeft(locationList))
  }
}
```



#### Error stacktrace:

```
scala.runtime.Scala3RunTime$.assertFailed(Scala3RunTime.scala:11)
	dotty.tools.dotc.core.TypeOps$.dominators$1(TypeOps.scala:245)
	dotty.tools.dotc.core.TypeOps$.approximateOr$1(TypeOps.scala:381)
	dotty.tools.dotc.core.TypeOps$.orDominator(TypeOps.scala:399)
	dotty.tools.dotc.core.Types$OrType.join(Types.scala:3684)
	dotty.tools.dotc.core.Types$OrType.widenUnionWithoutNull(Types.scala:3700)
	dotty.tools.dotc.core.Types$Type.widenUnion(Types.scala:1386)
	dotty.tools.dotc.core.ConstraintHandling.widenOr$1(ConstraintHandling.scala:663)
	dotty.tools.dotc.core.ConstraintHandling.widenInferred(ConstraintHandling.scala:684)
	dotty.tools.dotc.core.ConstraintHandling.widenInferred$(ConstraintHandling.scala:29)
	dotty.tools.dotc.core.TypeComparer.widenInferred(TypeComparer.scala:31)
	dotty.tools.dotc.core.ConstraintHandling.instanceType(ConstraintHandling.scala:725)
	dotty.tools.dotc.core.ConstraintHandling.instanceType$(ConstraintHandling.scala:29)
	dotty.tools.dotc.core.TypeComparer.instanceType(TypeComparer.scala:31)
	dotty.tools.dotc.core.TypeComparer$.instanceType(TypeComparer.scala:3277)
	dotty.tools.dotc.core.Types$TypeVar.typeToInstantiateWith(Types.scala:5054)
	dotty.tools.dotc.core.Types$TypeVar.instantiate(Types.scala:5064)
	dotty.tools.dotc.typer.Inferencing.tryInstantiate$1(Inferencing.scala:814)
	dotty.tools.dotc.typer.Inferencing.doInstantiate$1(Inferencing.scala:817)
	dotty.tools.dotc.typer.Inferencing.interpolateTypeVars(Inferencing.scala:820)
	dotty.tools.dotc.typer.Inferencing.interpolateTypeVars$(Inferencing.scala:629)
	dotty.tools.dotc.typer.Typer.interpolateTypeVars(Typer.scala:145)
	dotty.tools.dotc.typer.Typer.simplify(Typer.scala:3518)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3503)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3577)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3581)
	dotty.tools.dotc.typer.Typer.traverse$1(Typer.scala:3630)
	dotty.tools.dotc.typer.Typer.typedStats(Typer.scala:3649)
	dotty.tools.dotc.typer.Typer.typedBlockStats(Typer.scala:1399)
	dotty.tools.dotc.typer.Typer.typedBlock(Typer.scala:1403)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3423)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3500)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3577)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3581)
	dotty.tools.dotc.typer.Typer.typedExpr(Typer.scala:3692)
	dotty.tools.dotc.typer.Typer.caseRest$1(Typer.scala:2177)
	dotty.tools.dotc.typer.Typer.typedCase(Typer.scala:2193)
	dotty.tools.dotc.typer.Typer.typedCases$$anonfun$1(Typer.scala:2121)
	dotty.tools.dotc.core.Decorators$.loop$1(Decorators.scala:99)
	dotty.tools.dotc.core.Decorators$.mapconserve(Decorators.scala:115)
	dotty.tools.dotc.typer.Typer.typedCases(Typer.scala:2120)
	dotty.tools.dotc.typer.Typer.$anonfun$39(Typer.scala:2111)
	dotty.tools.dotc.typer.Applications.harmonic(Applications.scala:2559)
	dotty.tools.dotc.typer.Applications.harmonic$(Applications.scala:434)
	dotty.tools.dotc.typer.Typer.harmonic(Typer.scala:145)
	dotty.tools.dotc.typer.Typer.typedMatchFinish(Typer.scala:2111)
	dotty.tools.dotc.typer.Typer.typedMatch(Typer.scala:2040)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3430)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3500)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3577)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3581)
	dotty.tools.dotc.typer.Typer.typedExpr(Typer.scala:3692)
	dotty.tools.dotc.typer.Typer.typedBlock(Typer.scala:1406)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3423)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3500)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3577)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3581)
	dotty.tools.dotc.typer.Typer.typedExpr(Typer.scala:3692)
	dotty.tools.dotc.typer.Typer.$anonfun$64(Typer.scala:2834)
	dotty.tools.dotc.inlines.PrepareInlineable$.dropInlineIfError(PrepareInlineable.scala:256)
	dotty.tools.dotc.typer.Typer.typedDefDef(Typer.scala:2834)
	dotty.tools.dotc.typer.Typer.typedNamed$1(Typer.scala:3397)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3499)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3577)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3581)
	dotty.tools.dotc.typer.Typer.traverse$1(Typer.scala:3603)
	dotty.tools.dotc.typer.Typer.typedStats(Typer.scala:3649)
	dotty.tools.dotc.typer.Typer.typedClassDef(Typer.scala:3097)
	dotty.tools.dotc.typer.Typer.typedTypeOrClassDef$1(Typer.scala:3403)
	dotty.tools.dotc.typer.Typer.typedNamed$1(Typer.scala:3407)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3499)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3577)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3581)
	dotty.tools.dotc.typer.Typer.traverse$1(Typer.scala:3603)
	dotty.tools.dotc.typer.Typer.typedStats(Typer.scala:3649)
	dotty.tools.dotc.typer.Typer.typedPackageDef(Typer.scala:3230)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3449)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3500)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3577)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3581)
	dotty.tools.dotc.typer.Typer.typedExpr(Typer.scala:3692)
	dotty.tools.dotc.typer.TyperPhase.typeCheck$$anonfun$1(TyperPhase.scala:47)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:15)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:10)
	dotty.tools.dotc.core.Phases$Phase.monitor(Phases.scala:503)
	dotty.tools.dotc.typer.TyperPhase.typeCheck(TyperPhase.scala:53)
	dotty.tools.dotc.typer.TyperPhase.$anonfun$4(TyperPhase.scala:99)
	scala.collection.Iterator$$anon$6.hasNext(Iterator.scala:479)
	scala.collection.Iterator$$anon$9.hasNext(Iterator.scala:583)
	scala.collection.immutable.List.prependedAll(List.scala:152)
	scala.collection.immutable.List$.from(List.scala:685)
	scala.collection.immutable.List$.from(List.scala:682)
	scala.collection.IterableOps$WithFilter.map(Iterable.scala:900)
	dotty.tools.dotc.typer.TyperPhase.runOn(TyperPhase.scala:98)
	dotty.tools.dotc.Run.runPhases$1$$anonfun$1(Run.scala:343)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:15)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:10)
	scala.collection.ArrayOps$.foreach$extension(ArrayOps.scala:1323)
	dotty.tools.dotc.Run.runPhases$1(Run.scala:336)
	dotty.tools.dotc.Run.compileUnits$$anonfun$1(Run.scala:384)
	dotty.tools.dotc.Run.compileUnits$$anonfun$adapted$1(Run.scala:396)
	dotty.tools.dotc.util.Stats$.maybeMonitored(Stats.scala:69)
	dotty.tools.dotc.Run.compileUnits(Run.scala:396)
	dotty.tools.dotc.Run.compileSources(Run.scala:282)
	dotty.tools.dotc.interactive.InteractiveDriver.run(InteractiveDriver.scala:161)
	dotty.tools.pc.MetalsDriver.run(MetalsDriver.scala:47)
	dotty.tools.pc.WithCompilationUnit.<init>(WithCompilationUnit.scala:31)
	dotty.tools.pc.SimpleCollector.<init>(PcCollector.scala:351)
	dotty.tools.pc.PcSemanticTokensProvider$Collector$.<init>(PcSemanticTokensProvider.scala:63)
	dotty.tools.pc.PcSemanticTokensProvider.Collector$lzyINIT1(PcSemanticTokensProvider.scala:63)
	dotty.tools.pc.PcSemanticTokensProvider.Collector(PcSemanticTokensProvider.scala:63)
	dotty.tools.pc.PcSemanticTokensProvider.provide(PcSemanticTokensProvider.scala:88)
	dotty.tools.pc.ScalaPresentationCompiler.semanticTokens$$anonfun$1(ScalaPresentationCompiler.scala:116)
```
#### Short summary: 

java.lang.AssertionError: assertion failed