package amyc.formatter

import amyc.utils._
import amyc.parsing._
import amyc.parsing.Tokens._
import scala.collection.mutable.ListBuffer
import java.nio.file._
import java.nio.charset.StandardCharsets

object Formatter extends Pipeline[Iterator[Token], List[(String, String)]] {
  private val INDENT_SYMBOL: String = "  "

  private def NEWLINE_SYMBOL: Unit = {
    currentBuilder.append("\n")
    atLineStart = true
  }

  private def SPACE_SYMBOL: Unit = {
    currentBuilder.append(" ")
    atLineStart = false
  }

  private var INDENT_LEVEL: Int = 0
  private var atLineStart: Boolean = true

  private var lastToken: Option[Token] = None

  private var currentBuilder: StringBuilder = new StringBuilder
  private val files = ListBuffer.empty[(String, StringBuilder)]

  private def PRINT_TOKEN(text: String): Unit = {
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

    if (!atLineStart) NEWLINE_SYMBOL

    files.map { case (name, sb) => (name, sb.toString()) }.toList
  }

  private def handleToken(t: Token): Unit = t match {
    case CommentToken(c) =>
      if c.startsWith("//") then {
        lastToken match
          case Some(DelimiterToken(";")) =>
            ()
          case _ =>
            NEWLINE_SYMBOL
        PRINT_TOKEN(c)
        NEWLINE_SYMBOL
      } else {
        PRINT_TOKEN(c)
      }

    case DelimiterToken(",") =>
      PRINT_TOKEN(",")
      SPACE_SYMBOL

    case DelimiterToken("{") =>
      NEWLINE_SYMBOL
      PRINT_TOKEN("{")
      INDENT_LEVEL += 1
      NEWLINE_SYMBOL

    case DelimiterToken("}") =>
      INDENT_LEVEL -= 1
      lastToken match
        case Some(DelimiterToken("}")) => ()
        case _                         => NEWLINE_SYMBOL
      PRINT_TOKEN("}")
      NEWLINE_SYMBOL

    case DelimiterToken(";") =>
      PRINT_TOKEN(";")
      NEWLINE_SYMBOL

    case DelimiterToken("=") =>
      SPACE_SYMBOL
      PRINT_TOKEN("=")
      SPACE_SYMBOL

    case DelimiterToken(":") =>
      PRINT_TOKEN(":")
      SPACE_SYMBOL

    case DelimiterToken("(") =>
      PRINT_TOKEN("(")

    case DelimiterToken(")") =>
      PRINT_TOKEN(")")

    case DelimiterToken(d) =>
      PRINT_TOKEN(d)

    case OperatorToken(op) =>
      SPACE_SYMBOL
      PRINT_TOKEN(op)
      SPACE_SYMBOL

    case KeywordToken("def") =>
      NEWLINE_SYMBOL
      PRINT_TOKEN("def")
      SPACE_SYMBOL

    case KeywordToken("object") =>
      PRINT_TOKEN("object")
      SPACE_SYMBOL

    case KeywordToken("end") =>
      INDENT_LEVEL -= 1
      NEWLINE_SYMBOL
      PRINT_TOKEN("end")
      SPACE_SYMBOL

    case KeywordToken("error") =>
      PRINT_TOKEN("error")

    case KeywordToken(s) =>
      PRINT_TOKEN(s)
      SPACE_SYMBOL

    case IdentifierToken(s) =>
      lastToken match {
        case Some(KeywordToken("object")) =>
          PRINT_TOKEN(s)
          NEWLINE_SYMBOL
          INDENT_LEVEL += 1
        case Some(DelimiterToken("}")) =>
          NEWLINE_SYMBOL
          PRINT_TOKEN(s)
        case Some(KeywordToken("end")) =>
          PRINT_TOKEN(s)
          NEWLINE_SYMBOL
        case _ => PRINT_TOKEN(s)
      }

    case PrimTypeToken(s) =>
      PRINT_TOKEN(s)

    case IntLitToken(s) =>
      PRINT_TOKEN(s.toString)

    case BoolLitToken(s) =>
      PRINT_TOKEN(s.toString)

    case StringLitToken(s) =>
      PRINT_TOKEN("\"" + s + "\"")

    case FileToken(s) =>
      lastToken = None
      currentBuilder = new StringBuilder
      files += ((s, currentBuilder))

    case ErrorToken(e) =>
      PRINT_TOKEN(s"Couldn't format: $e")

    case _ => ()
  }

  override def run(
      ctx: Context
  )(tokens: Iterator[Token]): List[(String, String)] = {
    val formattedFiles = Formatter(tokens)
    formattedFiles.foreach { case (fileName, content) =>
      ctx.reporter.info(s"Formatting $fileName")
      Files.write(
        Paths.get(fileName),
        content.getBytes(StandardCharsets.UTF_8)
      )
    }
    formattedFiles
  }
}
