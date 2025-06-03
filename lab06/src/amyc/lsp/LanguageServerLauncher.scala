package amyc.lsp

import org.eclipse.lsp4j.launch.LSPLauncher
import org.eclipse.lsp4j.jsonrpc.Launcher
import org.eclipse.lsp4j.services.LanguageClient

object AmyLanguageServerLauncher {
    def apply(args: Array[String]): Unit = {
        try {
            // Create an instance of the language server
            val server: AmyLanguageServer = new AmyLanguageServer()
            val in = System.in
            val out = System.out

            // Create a launcher for the server
            val launcher: Launcher[LanguageClient] = LSPLauncher.createServerLauncher(server, in, out)
            val client: LanguageClient = launcher.getRemoteProxy()

            // Start the server
            server.connect(client)
            launcher.startListening()

            // println("LSP server is running...")
        } catch {
            case e: Exception =>
                println("Error starting LSP server: " + e.getMessage)
                e.printStackTrace()
        }
    }
}
