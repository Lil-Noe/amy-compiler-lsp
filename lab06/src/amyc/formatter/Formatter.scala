package amyc.formatter

import amyc.utils._
import amyc.parsing._
import amyc.parsing.Tokens._
import scala.collection.mutable.ListBuffer
import java.nio.file._
import java.nio.charset.StandardCharsets

object Formatter extends Pipeline[Iterator[Token], List[(String, String)]] {
  private val INDENT_SYMBOL: String = "  "

  private def add_newline: Unit = {
    currentBuilder.append("\n")
    atLineStart = true
  }

  private def add_space: Unit = {
    currentBuilder.append(" ")
    atLineStart = false
  }

  private var INDENT_LEVEL: Int = 0
  private var atLineStart: Boolean = true

  private var lastToken: Option[Token] = None

  private var currentBuilder: StringBuilder = new StringBuilder
  private val files = ListBuffer.empty[(String, StringBuilder)]

  private def add_text(text: String): Unit = {
    if (atLineStart) {
      (0 until INDENT_LEVEL).foreach(_ => currentBuilder.append(INDENT_SYMBOL))
      atLineStart = false
    }
    currentBuilder.append(text)
  }

  def apply(tokens: Iterator[Token]): List[(String, String)] = {
    INDENT_LEVEL = 0
    atLineStart = true
    lastToken = None
    files.clear()
    currentBuilder = new StringBuilder

    while (tokens.hasNext) {
      val token = tokens.next()
      handleToken(token)
      lastToken = Some(token)
    }

    if (!atLineStart) add_newline

    files.map { case (name, sb) => (name, sb.toString()) }.toList
  }

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

    case FileToken(s) =>
      lastToken = None
      currentBuilder = new StringBuilder
      files += ((s, currentBuilder))

    case ErrorToken(e) =>
      add_text(s"Couldn't format: $e")

    case _ => ()
  }

  override def run(
      ctx: Context
  )(tokens: Iterator[Token]): List[(String, String)] = {
    Formatter(tokens)
  }
}
