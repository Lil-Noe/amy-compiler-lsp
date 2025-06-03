package amyc.lsp

import java.util.concurrent.CompletableFuture
import org.eclipse.lsp4j.services.LanguageServer
import org.eclipse.lsp4j.InitializeParams
import org.eclipse.lsp4j.InitializeResult
import org.eclipse.lsp4j.ServerCapabilities
import org.eclipse.lsp4j.services.WorkspaceService
import org.eclipse.lsp4j.services.TextDocumentService
import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.services.LanguageClientAware
import org.eclipse.lsp4j.TextDocumentSyncKind

class AmyLanguageServer extends LanguageServer with LanguageClientAware {

    // Initialize the workspace and text document services
    private val workspaceService: WorkspaceService = new AmyWorkspaceService()
    private val textDocumentService: TextDocumentService = new AmyTextDocumentService()
    
    // Initialize the language client
    private var languageClient: LanguageClient = null
    
    
    // Implement the LanguageClientAware and LanguageServer interfaces

    override def getWorkspaceService(): WorkspaceService = workspaceService

    override def getTextDocumentService(): TextDocumentService = textDocumentService

    override def initialize(params: InitializeParams): CompletableFuture[InitializeResult] = {

        // Create the capabilities of the server
        val capabilities = new ServerCapabilities()

        // Make sure text documents are synced, always sending full content of the document
        capabilities.setTextDocumentSync(TextDocumentSyncKind.Full)

        // Make sure the server provides goto definition support
        capabilities.setDefinitionProvider(true)

        // Create the InitializeResult with the capabilities
        val result = new InitializeResult(capabilities)

        // Return the CompletableFuture
        CompletableFuture.completedFuture(result)
    }
    
    override def connect(client: LanguageClient): Unit = this.languageClient = client

    override def exit(): Unit = System.exit(0)
    
    override def shutdown(): CompletableFuture[Object] = CompletableFuture.completedFuture(null)

}
