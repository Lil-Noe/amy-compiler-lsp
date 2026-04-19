package amyc

import ast._
import utils._
import parsing._
import analyzer._
import codegen._
import lsp._
import interpreter.Interpreter
import java.io.File

import org.eclipse.lsp4j.jsonrpc.Launcher
import org.eclipse.lsp4j.services.LanguageClient
import formatting.{FormatPrinter, Formatter}

object Main extends MainHelpers {
  private def parseArgs(args: Array[String]): Context = {
    var ctx = Context(new Reporter, Nil)
    args foreach {
      case "--printTokens" => ctx = ctx.copy(printTokens = true)
      case "--printTrees"  => ctx = ctx.copy(printTrees = true)
      case "--printNames"  => ctx = ctx.copy(printNames = true)
      case "--interpret"   => ctx = ctx.copy(interpret = true)
      case "--type-check"  => ctx = ctx.copy(typeCheck = true)
      case "--help"        => ctx = ctx.copy(help = true)
      case "--format"      => ctx = ctx.copy(format = true)
      case "--server_mode" => ctx = ctx.copy(serverMode = true)
      case file            => ctx = ctx.copy(files = ctx.files :+ file)
    }

    if (ctx.files.nonEmpty) {
      val stdLib = List("examples/library/Std.amy", "examples/library/Option.amy", "examples/library/List.amy")
      ctx = ctx.copy(files = (stdLib ++ ctx.files).distinct)
    }

    ctx
  }

  private def launchServer(): Unit = {
    // Create the server and launch it first
    val server = new AmyLanguageServer()
    val launcher = Launcher.createLauncher(
      server, classOf[LanguageClient], System.in, System.out)

    // Connect to the client side of the LSP
    val client = launcher.getRemoteProxy
    server.connect(client)

    // Start listening to communications from client
    launcher.startListening()
  }


  def main(args: Array[String]): Unit = {
    val ctx = parseArgs(args)

    if (ctx.serverMode) {
      launchServer() 
      return
    }

    if (ctx.help) {
      val helpMsg = {
        """Welcome to the Amy reference compiler, v.1.5
          |
          |Default behavior is to compile the program to WebAssembly and print the following files:
          |(1) the resulting code in WebAssembly text format (.wat),
          |(2) the resulting code in WebAssembly binary format (.wasm),
          |
          |Options:
          |  --format         Format the specified files
          |  --server_mode    Launch the language server
          |  --printTokens    Print lexer tokens (with positions) after lexing and exit
          |  --printTrees     Print trees after parsing and exit
          |  --printNames     Print trees with unique namas after name analyzer and exit
          |  --interpret      Interpret the program instead of compiling
          |  --type-check     Type-check the program and print trees
          |  --server_mode    Launch Amy LSP Server
          |  --help           Print this message
        """.stripMargin
      }
      println(helpMsg)
      sys.exit(0)
    }
    val pipeline = {
      if (ctx.format) Formatter.andThen(FormatPrinter)
      else {
      AmyLexer.andThen(
        if (ctx.printTokens) DisplayTokens
        else Parser.andThen(
          if (ctx.printTrees) treePrinterN("Trees after parsing")
          else NameAnalyzer.andThen(
            if (ctx.printNames) treePrinterS("Trees after name analysis")
            else TypeChecker.andThen(
              if (ctx.typeCheck) then treePrinterS("Trees after type checking")
              else (
                if (ctx.interpret) then Interpreter
                else CodeGen.andThen(CodePrinter))))))}}

    val files = ctx.files.map(new File(_))

    try {
      if (files.isEmpty) {
        ctx.reporter.fatal("No input files")
      }
      files.find(!_.exists()).foreach { f =>
        ctx.reporter.fatal(s"File not found: ${f.getName}")
      }
      pipeline.run(ctx)(files)
      ctx.reporter.terminateIfErrors()
    } catch {
      case AmycFatalError(_) =>
        sys.exit(1)
    }
  }
}

trait MainHelpers {
  import SymbolicTreeModule.{Program => SP}
  import NominalTreeModule.{Program => NP}

  def treePrinterS(title: String): Pipeline[(SP, SymbolTable), Unit] = {
    new Pipeline[(SP, SymbolTable), Unit] {
      def run(ctx: Context)(v: (SP, SymbolTable)) = {
        println(title)
        println(SymbolicPrinter(v._1)(true))
      }
    }
  }

  def treePrinterN(title: String): Pipeline[NP, Unit] = {
    new Pipeline[NP, Unit] {
      def run(ctx: Context)(v: NP) = {
        println(title)
        println(NominalPrinter(v))
      }
    }
  }
}
