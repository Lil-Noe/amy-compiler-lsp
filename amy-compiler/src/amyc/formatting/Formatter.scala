package amyc.formatting

import amyc.utils._
import amyc.parsing._
import amyc.parsing.Tokens._
import amyc.formatting.TokenFormatter
import java.io.File
import amyc.parsing.AmyLexer.lexer
import silex._


object Formatter extends Pipeline[List[File], List[(File, String)]] {

  override def run(
      ctx: Context
  )(files: List[File]): List[(File, String)] = {
    val f = files.map { file =>
      // Create a source from the file
      val src = Source.fromFile(file.toString, SourcePositioner(file))

      // Tokenize the file, with no space
      val tokens = lexer.spawn(src).filter{
        case SpaceToken() => false
        case _ => true
      }

      // Format the token stream
      val content = TokenFormatter(tokens)

      // Return the file with its new content
      file -> content
    }
    f
  }
}
