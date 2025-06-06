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

  private def add_text(text: String): Unit = {
    if (atLineStart) {
      (0 until INDENT_LEVEL).foreach(_ => builder.append(INDENT_SYMBOL))
      atLineStart = false
    }
    builder.append(text)
  }

  private def add_newline: Unit = {
    builder.append("\n")
    atLineStart = true
  }

  private def add_space: Unit = {
    builder.append(" ")
    atLineStart = false
  }

  def apply(tokens: Iterator[Token]): String = {
    INDENT_LEVEL = 0
    atLineStart = true
    lastToken = None
    builder = new StringBuilder

    tokens.foreach { token =>
      handleToken(token)
      lastToken = Some(token)
    }

    if (!atLineStart) add_newline
    builder.toString()
  }

  private def handleToken(t: Token): Unit = t match {
    case CommentToken(c) =>
      if (!atLineStart) add_newline
      add_text(c)
      add_newline

    case DelimiterToken(d) => d match {
      case "," =>
        add_text(","); add_space

      case ":" =>
        add_text(":"); add_space

      case ";" =>
        add_text(";"); add_newline

      case "=" =>
        add_space; add_text("="); add_space

      case "{" =>
        lastToken match {
          case Some(KeywordToken("else")) =>
            add_newline; add_text("{"); INDENT_LEVEL += 1; add_newline
          case Some(KeywordToken("if")) | Some(KeywordToken("for")) | Some(DelimiterToken(")")) =>
            add_newline; add_text("{"); INDENT_LEVEL += 1; add_newline
          case Some(KeywordToken("match")) =>
            add_text("{"); INDENT_LEVEL += 1; add_newline
          case _ =>
            add_newline; add_text("{"); INDENT_LEVEL += 1; add_newline
        }

      case "}" =>
        INDENT_LEVEL = math.max(0, INDENT_LEVEL - 1)
        lastToken match {
          case Some(DelimiterToken("{")) =>
            add_text("}"); add_newline
          case Some(DelimiterToken("}")) =>
            add_text("}"); add_newline
          case _ =>
            add_newline; add_text("}"); add_newline
        }

      case "=>" => 
        add_space; add_text("=>"); add_space

      case other =>
        add_text(other)
    }

    case OperatorToken(op) => op match {
      case "&&" | "||" | "==" | "<=" | "<" | "+" | "*" | "/" | "%" | "++" =>
        add_space; add_text(op); add_space

      case "!" =>
        add_text("!")

      case "-" =>
        lastToken match {
          case Some(IdentifierToken(_)) | Some(DelimiterToken(")")) | Some(DelimiterToken("}")) =>
            add_space; add_text("-"); add_space
          case _ =>
            add_text("-");
        }

      case other =>
        add_text(other)
    }

    case KeywordToken(s) => s match {
      case "case" =>
        lastToken match {
          case Some(DelimiterToken("{")) =>
            add_text("case"); add_space
          case _ => 
            add_newline; add_text("case"); add_space
        }

      case "class" =>
        add_newline; add_text("class"); add_space

      case "def" =>
        add_newline; add_text("def"); add_space

      case "match" =>
        add_space; add_text("match"); add_space

      case "else" =>
        add_text("else")

      case "extends" =>
        add_space; add_text("extends"); add_space

      case "if" =>
        add_text("if"); add_space

      case "object" =>
        add_text("object"); add_space

      case "end" =>
        INDENT_LEVEL = 0
        add_newline; add_newline; add_text("end"); add_space

      case "val" =>
        lastToken match {
          case Some(IdentifierToken(_)) | Some(DelimiterToken(")")) | Some(DelimiterToken("}")) =>
            add_newline; add_text("val"); add_space
          case _ =>
            add_text("val"); add_space
        }

      case "abstract" =>
        add_text("abstract"); add_space

      case "error" =>
        add_text("error")

      case "_" =>
        add_text("_"); 

      case other =>
        add_text(other); add_space
    }

    case IdentifierToken(s) =>
      lastToken match {
        case Some(KeywordToken("object")) =>
          add_text(s); add_newline; INDENT_LEVEL += 1

        case Some(DelimiterToken("}")) =>
          add_newline; add_text(s)

        case Some(KeywordToken("end")) =>
          add_text(s); add_newline

        case Some(DelimiterToken(")")) =>
          add_newline; add_text(s)

        case Some(IdentifierToken(_)) =>
          add_newline; add_text(s)

        case _ =>
          add_text(s)
      }

    case PrimTypeToken(s) =>
      add_text(s)

    case IntLitToken(v) =>
      lastToken match {
        case Some(IdentifierToken(_)) | Some(DelimiterToken(")")) | Some(DelimiterToken("}")) =>
          add_newline; add_text(v.toString); add_space
        case _ =>
          add_text(v.toString)
      }

    case BoolLitToken(b) =>
      add_text(b.toString);

    case StringLitToken(s) =>
      add_text("\"" + s + "\"")

    case EOFToken() =>
      ()

    case ErrorToken(e) =>
      throw new AmycFatalError(s"Error: $e")

    case _ =>
      ()
  }
}
