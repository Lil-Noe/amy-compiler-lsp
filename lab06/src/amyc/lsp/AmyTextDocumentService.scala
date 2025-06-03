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

  override def didSave(params: DidSaveTextDocumentParams): Unit =
    server.sendMsgToClient("didSave TextDocument")
  override def didClose(params: DidCloseTextDocumentParams): Unit =
    server.sendMsgToClient("didClose TextDocument")
  override def didChange(params: DidChangeTextDocumentParams): Unit =
    server.sendMsgToClient("didChange TextDocument")
  override def didOpen(params: DidOpenTextDocumentParams): Unit =
    server.sendMsgToClient("didOpen TextDocument")




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
      server.sendMsgToClient(s"ClientFile URI : $uri \n ClientFile Path : $path")

      // Compile the file up to NameAnalyser to have identifiers
      val ctx = new Context(new Reporter, List(path.toString()))
      val files = ctx.files.map(new File(_))
      val pipeline = AmyLexer.andThen(Parser.andThen(NameAnalyzer))
      val (program, table) = pipeline.run(ctx)(files)




      // Get position given by the client
      val clientPosition: LSP4JPosition = params.getPosition()

      // Translate position (from LSP4J to Amy format) 
      // following zero-indexing of LSP4J protocol
      val identifierPosition = new SourcePosition(
        new File(uri), clientPosition.getLine() + 1, clientPosition.getCharacter() + 1)
      var definitionPosition : Position = NoPosition
    



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
                case FunDef(_,_,_,body) => identifier = lookForIdentifier(body, identifierPosition) 
                case _                  => /* do nothing for ClassDef (no Expr) */
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
          case None => {/* do nothing if the position did not point to an identifier */}
          case Some(name) => {
            
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


      // Translate position (from Amy to LSP4J format) 
      // following zero-indexing of LSP4J protocol
      // (we set the start and end of position to same value)
      val range = new LSP4JRange(
        new LSP4JPosition(definitionPosition.line - 1, definitionPosition.col - 1), 
        new LSP4JPosition(definitionPosition.line - 1, definitionPosition.col - 1)
      )

      val definitionFileURI = definitionPosition.file.toPath().toUri.toString()

      server.sendMsgToClient(s"Found identifier : $identifier")
      server.sendMsgToClient(s"ServerFile URI : $definitionFileURI")

      val location = Optional.of(new Location(definitionFileURI, range))
      val locationList = Collections.singletonList(location.get())
      CompletableFuture.completedFuture(Either.forLeft(Collections.singletonList(location.get())))
  }
}