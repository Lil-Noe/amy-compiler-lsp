package amyc.formatting

import amyc.parsing._
import amyc.parsing.Tokens._
import amyc.utils.AmycFatalError

object TokenFormatter {

  private val INDENT_SYMBOL: String = "  "
  private var INDENT_LEVEL: Int = 0
  private var atLineStart: Boolean = true
  private var lastToken: Option[Token] = None
  private var builder: StringBuilder = StringBuilder()

  // Add formatted text to the builder
  private def add_text(text: String): Unit = {
    if (atLineStart) {
      (0 until INDENT_LEVEL).foreach(_ => builder.append(INDENT_SYMBOL))
      atLineStart = false
    }
    builder.append(text)
  }

  // Skip a line
  private def add_newline: Unit = {
    builder.append("\n")
    atLineStart = true
  }

  // Add a space
  private def add_space: Unit = {
    builder.append(" ")
    atLineStart = false
  }

  def apply(tokens: Iterator[Token]): String = {
    // Iterate through the tokens and format them
    while (tokens.hasNext) {
      val token = tokens.next()
      handleToken(token)
      lastToken = Some(token)
    }

    // Return the formatted string
    builder.toString()
  }

  // Handle each token and format it accordingly
  private def handleToken(t: Token): Unit = t match {
    case CommentToken(c) =>
      if c.startsWith("//") then {
        lastToken match
          case Some(DelimiterToken(";")) =>
            ()
          case _ =>
            add_newline
        add_text(c)
        add_newline
      } else {
        add_text(c)
      }

    case DelimiterToken(",") =>
      add_text(",")
      add_space

    case DelimiterToken("{") =>
      add_newline
      add_text("{")
      INDENT_LEVEL += 1
      add_newline

    case DelimiterToken("}") =>
      INDENT_LEVEL -= 1
      lastToken match
        case Some(DelimiterToken("}")) => ()
        case _                         => add_newline
      add_text("}")
      add_newline

    case DelimiterToken(";") =>
      add_text(";")
      add_newline

    case DelimiterToken("=") =>
      add_space
      add_text("=")
      add_space

    case DelimiterToken(":") =>
      add_text(":")
      add_space

    case DelimiterToken("(") =>
      add_text("(")

    case DelimiterToken(")") =>
      add_text(")")

    case DelimiterToken(d) =>
      add_text(d)

    case OperatorToken(op) =>
      add_space
      add_text(op)
      add_space

    case KeywordToken("def") =>
      add_newline
      add_text("def")
      add_space

    case KeywordToken("object") =>
      add_text("object")
      add_space

    case KeywordToken("end") =>
      INDENT_LEVEL -= 1
      add_newline
      add_text("end")
      add_space

    case KeywordToken("error") =>
      add_text("error")

    case KeywordToken(s) =>
      add_text(s)
      add_space

    case IdentifierToken(s) =>
      lastToken match {
        case Some(KeywordToken("object")) =>
          add_text(s)
          add_newline
          INDENT_LEVEL += 1
        case Some(DelimiterToken("}")) =>
          add_newline
          add_text(s)
        case Some(KeywordToken("end")) =>
          add_text(s)
          add_newline
        case _ => add_text(s)
      }

    case PrimTypeToken(s) =>
      add_text(s)

    case IntLitToken(s) =>
      add_text(s.toString)

    case BoolLitToken(s) =>
      add_text(s.toString)

    case StringLitToken(s) =>
      add_text("\"" + s + "\"")

    case ErrorToken(e) =>
      throw new AmycFatalError(s"Unexpected error: $e")

    case EOFToken() => 
      // Always add a newline at the end of the file
      if (!atLineStart) add_newline

    case t => 
      throw new AmycFatalError(s"Unsupported token: $t")
  }

}
