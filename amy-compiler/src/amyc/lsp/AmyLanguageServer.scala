package amyc.lsp

import java.util.concurrent.CompletableFuture
import org.eclipse.lsp4j.InitializeParams
import org.eclipse.lsp4j.InitializeResult
import org.eclipse.lsp4j.ServerCapabilities
import org.eclipse.lsp4j.services.WorkspaceService
import org.eclipse.lsp4j.services.TextDocumentService
import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.services.LanguageServer
import org.eclipse.lsp4j.services.LanguageClientAware
import org.eclipse.lsp4j.TextDocumentSyncKind
import org.eclipse.lsp4j.MessageParams
import org.eclipse.lsp4j.MessageType
import java.nio.file.{Path, Paths}
import java.net.URI

/**
  * This Server receives requests/notifications from a client 
  * to perform certain operations regarding Amy language.
  */
class AmyLanguageServer extends LanguageServer with LanguageClientAware {
    
    // The root path of the workspace, initialized when launching
    private var workspaceRoot: Option[Path] = None

    // Language client, initialized when launching
    private var languageClient: LanguageClient = null
    // Connect to a client, called when launching
    override def connect(client: LanguageClient): Unit = { languageClient = client }
    

    // Initialize the workspace and text document services
    private val workspaceService: WorkspaceService = new AmyWorkspaceService(this)
    private val textDocumentService: TextDocumentService = new AmyTextDocumentService(this)

    override def getWorkspaceService(): WorkspaceService = workspaceService
    override def getTextDocumentService(): TextDocumentService = textDocumentService


    // Exit the process
    override def exit(): Unit = System.exit(0)


    // Shutdown server (not exit to get response)
    override def shutdown(): CompletableFuture[Object] = CompletableFuture.completedFuture(null)


    // Utility method to send messages from Server on Client side
    // (used to debug and control process)
    def sendMsgToClient(message: String): Unit = {
        val params: MessageParams = new MessageParams(MessageType.Info, message)
        languageClient.showMessage(params)
    }
    

    // Initialize the process
    // (first request the server should receive)
    override def initialize(params: InitializeParams): CompletableFuture[InitializeResult] = {

        // Get the root path of the workspace
        val rootPath = Option(params.getRootUri)
        workspaceRoot = rootPath.map(uri => Paths.get(URI.create(uri)))

        // Create the capabilities of the server
        val capabilities = new ServerCapabilities()
        // Make sure text documents are synced, always sending full content of the document
        capabilities.setTextDocumentSync(TextDocumentSyncKind.Full)


        // Only service the LSP implements for now
        capabilities.setDefinitionProvider(true)


        // Create the InitializeResult with the capabilities
        val result = new InitializeResult(capabilities)
        // Return the CompletableFuture
        CompletableFuture.completedFuture(result)
    }

    // Get the workspace root path
    def getWorkspaceRoot: Option[Path] = workspaceRoot

}
