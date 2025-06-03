error id: file://<WORKSPACE>/src/amyc/lsp/TextDocumentService.scala:`<none>`.
file://<WORKSPACE>/src/amyc/lsp/TextDocumentService.scala
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -amyc/utils/Range.
	 -amyc/utils/Range#
	 -amyc/utils/Range().
	 -amyc/parsing/Range.
	 -amyc/parsing/Range#
	 -amyc/parsing/Range().
	 -amyc/analyzer/Range.
	 -amyc/analyzer/Range#
	 -amyc/analyzer/Range().
	 -amyc/ast/SymbolicTreeModule.Range.
	 -amyc/ast/SymbolicTreeModule.Range#
	 -amyc/ast/SymbolicTreeModule.Range().
	 -Range.
	 -Range#
	 -Range().
	 -scala/Predef.Range.
	 -scala/Predef.Range#
	 -scala/Predef.Range().
offset: 566
uri: file://<WORKSPACE>/src/amyc/lsp/TextDocumentService.scala
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
import org.eclipse.lsp4j.Ra@@nge as LSP4JRange
import java.{util => ju}
import ju.Collections
import ju.Optional
import java.net.URI
import java.nio.file.{Files, Path, Paths}
import amyc.utils.* 
import amyc.parsing.*
import java.io.File
import amyc.analyzer.*
import amyc.ast.SymbolicTreeModule.*
import amyc.ast.Identifier
import scala.compiletime.ops.boolean


class AmyTextDocumentService extends TextDocumentService {

  override def didSave(params: DidSaveTextDocumentParams): Unit = {
  }

  override def didClose(params: DidCloseTextDocumentParams): Unit = {
  }

  override def didChange(params: DidChangeTextDocumentParams): Unit = {
  }

  override def didOpen(params: DidOpenTextDocumentParams): Unit = {
  } 

  /**
   * This function is defined to find the position of a givent identifier
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
  * This function is defined to recursively find the identifier pointed by positionSource in a pattern
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
    * This function is defined to find the identifier pointed by positionSource 
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
          if (positionMatch(positionSource, e.startPosition, e.endPosition)) Some(name)
          else None
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
        case Sequence(e1,e2) => { lookForIdentifier(e1, positionSource) match {
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
                  case _ => lookForIdentifier(elze, positionSource)
            }}
          }}



        // Local variable
        case Let(df, value, body) => {
          if (positionMatch(positionSource, e.startPosition, e.endPosition)) Some(df.name)
          else { lookForIdentifier(value, positionSource) match {
                case Some(name) => Some(name)
                case _ => lookForIdentifier(body, positionSource)
          }}
        }



        // Type or function invocation
        case Call(qname, args) => {
          if (positionMatch(positionSource, e.startPosition, e.endPosition)) Some(qname)
          else { 
            // Look into each argument
            args.foreach { arg => {
              lookForIdentifier(arg, positionSource) match { case Some(id) => id case _ => /* do nothing */}
            }}
            None
          }
        }

        // Match pattern
        case Match(scrut, cases) => { 
          lookForIdentifier(scrut, positionSource) match {
            case Some(name) => Some(name)
            case _ => {
              // Look into each MatchCase (the pattern first, then the expression)
              cases.foreach { matchCase =>
                lookIntoPattern(matchCase.pat, positionSource) match { case Some(id) => id case _ => /* do nothing */}
                lookForIdentifier(matchCase.expr, positionSource) match { case Some(id) => id case _ => /* do nothing */}
              }
              None
            }
          }
          
        }
    }
  }






  override def definition(params: DefinitionParams): 
    CompletableFuture[Either[ju.List[? <: Location], ju.List[? <: LocationLink]]] = 
  { 
      // Get Position given by the client
      val textDoc = params.getTextDocument
      val uri = textDoc.getUri
      val clientPosition = params.getPosition

      // Get line in file and position in this line
      val line = params.getPosition().getLine()
      val character = params.getPosition().getCharacter()

      // Change position from LSP4J to Amy following zero-indexing of LSP4J protocol
      val identifierPosition = new SourcePosition(new File(uri), line + 1, character + 1)
      var definitionPosition : Position = NoPosition
    
      // Get path of file
      val path = Paths.get(URI.create(uri))

      val ctx = new Context(new Reporter, List(path.toString()))
      val files = ctx.files.map(new File(_))
      val pipeline = AmyLexer.andThen(Parser.andThen(NameAnalyzer))

      // Keep track of the corresponding identifier
      var identifier : Option[Identifier] = None

      if (files.isEmpty) {
        // Return future with empty files
        return CompletableFuture.completedFuture(Either.forLeft(Collections.emptyList()))
      }

      val (program, table) = pipeline.run(ctx)(files)

      // Go through modules to find identifier then definition
      program.modules.foreach { mod => {


        // Search identifier first in definitions
        mod.defs.foreach { definition => {
            if (!identifier.isDefined) {
              definition match {
                case FunDef(_,_,_,body) => identifier = lookForIdentifier(body, identifierPosition) 
                case _                  => /* do nothing */
              }
            }
        }}

        // If not found, look in the expressions
        if (!identifier.isDefined) {
          mod.optExpr.foreach { e => {
            if (!identifier.isDefined) {
              identifier = lookForIdentifier(e, identifierPosition)
            }
          }}
        }





        // When corresponding identifier found, get its position
        identifier match {
          case None => {}
          case Some(name) => {
            
            // Search first in definitions
            mod.defs.foreach { definition => {

              if (definition.name == name) definitionPosition = definition.startPosition
              else {
                if (definitionPosition != NoPosition) {
                  definition match {
                    case FunDef(_,_,_,body) => definitionPosition = lookForPosition(body, name) 
                    case _                  => /* do nothing */
                  }
                }
              }
            }}

            // If not found, look in the expressions
            if (definitionPosition != NoPosition) {
              mod.optExpr.foreach { e => {
                  if (definitionPosition != NoPosition) {
                    definitionPosition = lookForPosition(e, name)
                  }
              }}
            }
          } 
        }
      }}


      // Change position from Amy to LSP4J following zero-indexing of LSP4J protocol
      // We set the start and end of position to same value
      val range = new LSP4JRange(
        new LSP4JPosition(definitionPosition.line - 1, definitionPosition.col - 1), 
        new LSP4JPosition(definitionPosition.line - 1, definitionPosition.col - 1)
      )

      val definitionFileURI = definitionPosition.file.toPath().toUri.toString()

      val location = Optional.of(new Location(definitionFileURI, range))
      val locationList = Collections.singletonList(location.get())
      CompletableFuture.completedFuture(Either.forLeft(Collections.singletonList(location.get())))
  }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.