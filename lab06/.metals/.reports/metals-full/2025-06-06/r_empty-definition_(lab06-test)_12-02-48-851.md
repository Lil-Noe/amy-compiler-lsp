error id: file:///C:/Users/lilia/Documents/EPFL/BA6/CLP/CS-320_Amy_LSP/lab06/test/scala/amyc/test/LanguageServerTests.scala:`<none>`.
file:///C:/Users/lilia/Documents/EPFL/BA6/CLP/CS-320_Amy_LSP/lab06/test/scala/amyc/test/LanguageServerTests.scala
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 506
uri: file:///C:/Users/lilia/Documents/EPFL/BA6/CLP/CS-320_Amy_LSP/lab06/test/scala/amyc/test/LanguageServerTests.scala
text:
```scala
package amyc.test

import amyc.lsp.AmyLanguageServer
import org.eclipse.lsp4j.InitializeParams
import org.eclipse.lsp4j.InitializeResult
import org.eclipse.lsp4j.ServerCapabilities
import org.eclipse.lsp4j.MessageParams
import org.eclipse.lsp4j.MessageType
import org.eclipse.lsp4j.services.LanguageClient
import org.junit.Test
import org.junit.Assert._
import java.util.concurrent.CompletableFuture

class AmyLanguageServerTests {

  // Un client factice qui n'ouvre pas de flux réel : on i@@mplémente uniquement les méthodes nécessaires
  private val dummyClient: LanguageClient = new LanguageClient {
    override def showMessage(params: MessageParams): Unit = {// Ne fait rien, juste stub
    }
    override def logMessage(x$0: MessageParams): Unit = ???
    override def publishDiagnostics(x$0: org.eclipse.lsp4j.PublishDiagnosticsParams): Unit = ???
    override def showMessageRequest(x$0: org.eclipse.lsp4j.ShowMessageRequestParams):
    java.util.concurrent.CompletableFuture[org.eclipse.lsp4j.MessageActionItem] = ???
  def telemetryEvent(x$0: Object): Unit = ???
    
    // Les autres méthodes de LanguageClient ont des implémentations par défaut dans LSP4J
    // (Aucune surcharge supplémentaire n'est nécessaire si vous utilisez une version
    // de lsp4j où ces méthodes sont déclarées "default".)
  }

  @Test
  def testInitializeAndGetCapabilities(): Unit = {
    // 1. Instanciation du serveur
    val server = new AmyLanguageServer()

    // 2. Connexion au client factice
    server.connect(dummyClient)

    // 3. Appel à initialize()
    val initParams = new InitializeParams()
    val futureResult: CompletableFuture[InitializeResult] = server.initialize(initParams)
    // Bloque jusqu'à obtenir le résultat
    val initResult: InitializeResult = futureResult.get()

    // 4. Vérifications JUnit
    assertNotNull("Le résultat de initialize() ne doit pas être nul", initResult)

    val caps: ServerCapabilities = initResult.getCapabilities
    assertNotNull("Les capabilities ne doivent pas être nulles", caps)

    // On s'attend à ce que le serveur expose un DefinitionProvider (comme dans votre code)
    assertTrue(
      "Le capability 'definitionProvider' doit être à true",
      caps.getDefinitionProvider == java.lang.Boolean.TRUE
    )

    // Vérifier que les services sont bien non nuls
    assertNotNull("WorkspaceService ne doit pas être null", server.getWorkspaceService())
    assertNotNull("TextDocumentService ne doit pas être null", server.getTextDocumentService())
  }

  @Test
  def testShutdownCompletableFuture(): Unit = {
    val server = new AmyLanguageServer()
    // L'appel à shutdown() ne doit pas planter et doit renvoyer une CompletableFuture non nulle
    val shutdownFuture: CompletableFuture[Object] = server.shutdown()
    assertNotNull("Le futur retourné par shutdown() ne doit pas être nul", shutdownFuture)
    // On peut vérifier que get() ne lève pas d'exception
    val result = shutdownFuture.get()
    assertNull("Le résultat de shutdown() doit être `null`", result)
  }

}

```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.