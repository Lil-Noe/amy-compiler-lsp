// package amyc.test

// import amyc.lsp.AmyLanguageServer
// import org.eclipse.lsp4j.InitializeParams
// import org.eclipse.lsp4j.InitializeResult
// import org.eclipse.lsp4j.ServerCapabilities
// import org.eclipse.lsp4j.MessageParams
// import org.eclipse.lsp4j.MessageType
// import org.eclipse.lsp4j.PublishDiagnosticsParams
// import org.eclipse.lsp4j.services.LanguageClient
// import org.eclipse.lsp4j.ShowMessageRequestParams
// import org.eclipse.lsp4j.Position
// import org.eclipse.lsp4j.TextDocumentIdentifier
// import org.junit.Test
// import org.junit.Assert._
// import java.util.concurrent.CompletableFuture
// import org.eclipse.lsp4j.MessageActionItem
// import org.eclipse.lsp4j.DefinitionParams
// import java.nio.file.Paths
// import java.nio.file.Files
// import java.util.Collections

// class AmyLanguageServerTests {

//     // Useless client created only for the use of test
//     private val uselessClient: LanguageClient = new LanguageClient {
//         override def showMessage(params: MessageParams): Unit = 
//             {/* Stub implementation : do nothing */}
//         override def logMessage(x$0: MessageParams): Unit = 
//             {/* Stub implementation : do nothing */}
//         override def publishDiagnostics(x$0: PublishDiagnosticsParams): Unit = 
//             {/* Stub implementation : do nothing */}
//         override def showMessageRequest(x$0: ShowMessageRequestParams): CompletableFuture[MessageActionItem] = 
//             {/* Stub implementation : do nothing */ 
//             return CompletableFuture.supplyAsync(() => new MessageActionItem)
//             }
//         override def telemetryEvent(x$0: Object): Unit =  
//             {/* Stub implementation : do nothing */}
//     }

//     @Test def testInitLSPServer(): Unit = {
//         // Simulate what the Main function does : create a server and connect it to a client
//         val server = new AmyLanguageServer()
//         server.connect(uselessClient)

//         // Impersonate the client : 
//         // initialize the server and wait for the result
//         val serverInitParams = new InitializeParams()
//         val futureFromServer = server.initialize(serverInitParams)
//         val initResult = futureFromServer.get()


//         // Make sure the server did return a valid initialized result
//         assertNotNull("The server should have returned a valid result", initResult)

//         val serverCapabilities: ServerCapabilities = initResult.getCapabilities
//         assertNotNull("The server should have returned valid capabilities", serverCapabilities)
//         assertTrue("The server should implement go to definition feature", 
//                     serverCapabilities.getDefinitionProvider.getLeft()
//         )

//         assertNotNull("The server should have a valid Workspace Service", server.getWorkspaceService())
//         assertNotNull("The server should have a valid Text Document Service", server.getTextDocumentService())
//     }

//     @Test def testGoToDefinitionVal(): Unit = {
//         // This is the file we are going to use to test
//         val fileName = "HelloInt.amy"

//         // Get the root and navigate to wanted file from that
//         val projectRoot = Paths.get(System.getProperty("user.dir"))
//         val filePath   = projectRoot.resolve("test")
//                         .resolve("resources")
//                         .resolve("amy-files")
//                         .resolve(fileName)
//         assertTrue(s"Make sure the path does exist : $filePath", Files.exists(filePath))

//         // Create by hand the parameters we need to pass to definition
//         val fileURI: String = filePath.toAbsolutePath.toUri.toString
//         val posClient: Position = new Position(29, 31)
//         val fileClient: TextDocumentIdentifier = new TextDocumentIdentifier(fileURI)
//         val defParam: DefinitionParams = new DefinitionParams(fileClient, posClient)

//         val server = new AmyLanguageServer()

//         // Make sure the server has at least a valid text document service file
//         assertNotNull("The server should have a valid Text Document Service", server.getTextDocumentService())

//         // Apply the definition method
//         val future = server.getTextDocumentService().definition(defParam)
//         val fileReturned = future.get().getLeft().getFirst().getUri()
//         val positionReturned = future.get().getLeft().getFirst().getRange().getStart()
//         val lineReturned = positionReturned.getLine()
//         val charReturned = positionReturned.getCharacter()

//         assertEquals("The file returned is the right one", fileName, fileReturned.takeRight(fileName.length()))
//         assertEquals("The line returned is the right one", 2, lineReturned)
//         assertEquals("The column returned is the right one", 10, charReturned)
//     }



//     @Test def testGoToDefinitionInLibrary(): Unit = {
//         // This is the file we are going to use to test
//         val fileName = "Std.amy"
//         // Get the root and navigate to wanted file from that
//         val projectRoot = Paths.get(System.getProperty("user.dir"))
//         val filePath   = projectRoot.resolve("test")
//                         .resolve("resources")
//                         .resolve("amy-files")
//                         .resolve("library")
//                         .resolve(fileName)
//         assertTrue(s"Make sure the path does exist : $filePath", Files.exists(filePath))

//         // Create by hand the parameters we need to pass to definition
//         val fileURI: String = filePath.toAbsolutePath.toUri.toString
//         val posClient: Position = new Position(29, 12)
//         val fileClient: TextDocumentIdentifier = new TextDocumentIdentifier(fileURI)
//         val defParam: DefinitionParams = new DefinitionParams(fileClient, posClient)
//         val server = new AmyLanguageServer()

//         // Make sure the server has at least a valid text document service file
//         assertNotNull("The server should have a valid Text Document Service", server.getTextDocumentService())

//         // Apply the definition method
//         val future = server.getTextDocumentService().definition(defParam)
//         val fileReturned = future.get().getLeft().getFirst().getUri()
//         val positionReturned = future.get().getLeft().getFirst().getRange().getStart()
//         val lineReturned = positionReturned.getLine()
//         val charReturned = positionReturned.getCharacter()

//         assertEquals("The file returned is the right one", fileName, fileReturned.takeRight(fileName.length()))
//         assertEquals("The line returned is the right one", 28, lineReturned)
//         assertEquals("The column returned is the right one", 13, charReturned)
//     }



//     @Test def testGoToDefinitionFun(): Unit = {
//         // This is the file we are going to use to test
//         val fileName = "Hanoi.amy"
//         // Get the root and navigate to wanted file from that
//         val projectRoot = Paths.get(System.getProperty("user.dir"))
//         val filePath   = projectRoot.resolve("test")
//                         .resolve("resources")
//                         .resolve("amy-files")
//                         .resolve(fileName)
//         assertTrue(s"Make sure the path does exist : $filePath", Files.exists(filePath))

//         // Create by hand the parameters we need to pass to definition
//         val fileURI: String = filePath.toAbsolutePath.toUri.toString
//         val posClient: Position = new Position(14, 63)
//         val fileClient: TextDocumentIdentifier = new TextDocumentIdentifier(fileURI)
//         val defParam: DefinitionParams = new DefinitionParams(fileClient, posClient)
//         val server = new AmyLanguageServer()

//         // Make sure the server has at least a valid text document service file
//         assertNotNull("The server should have a valid Text Document Service", server.getTextDocumentService())

//         // Apply the definition method
//         val future = server.getTextDocumentService().definition(defParam)
//         val fileReturned = future.get().getLeft().getFirst().getUri()
//         val positionReturned = future.get().getLeft().getFirst().getRange().getStart()
//         val lineReturned = positionReturned.getLine()
//         val charReturned = positionReturned.getCharacter()

//         assertEquals("The file returned is the right one", fileName, fileReturned.takeRight(fileName.length()))
//         assertEquals("The line returned is the right one", 2, lineReturned)
//         assertEquals("The column returned is the right one", 11, charReturned)
//     }


//     @Test def testGoToDefinitionFail(): Unit = {
//         // This is the file we are going to use to test
//         val fileName = "Factorial.amy"
//         // Get the root and navigate to wanted file from that
//         val projectRoot = Paths.get(System.getProperty("user.dir"))
//         val filePath   = projectRoot.resolve("test")
//                         .resolve("resources")
//                         .resolve("amy-files")
//                         .resolve(fileName)
//         assertTrue(s"Make sure the path does exist : $filePath", Files.exists(filePath))

//         // Create by hand the parameters we need to pass to definition
//         val fileURI: String = filePath.toAbsolutePath.toUri.toString
//         val posClient: Position = new Position(8, 20)
//         val fileClient: TextDocumentIdentifier = new TextDocumentIdentifier(fileURI)
//         val defParam: DefinitionParams = new DefinitionParams(fileClient, posClient)
//         val server = new AmyLanguageServer()

//         // Make sure the server has at least a valid text document service file
//         assertNotNull("The server should have a valid Text Document Service", server.getTextDocumentService())

//         // Apply the definition method
//         val future = server.getTextDocumentService().definition(defParam)

//         assertEquals("The value returned contains an empty List", 
//                     Collections.emptyList(), future.get().getLeft()
//         )
//     }
// }
