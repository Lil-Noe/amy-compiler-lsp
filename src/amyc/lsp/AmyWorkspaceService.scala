package amyc.lsp

import org.eclipse.lsp4j.services.WorkspaceService
import org.eclipse.lsp4j.DidChangeConfigurationParams
import org.eclipse.lsp4j.DidChangeWatchedFilesParams

/**
  * This class is not used in this project.
  * We only inform the client the request has been received.
  *
  * @param server
  */
class AmyWorkspaceService(server: AmyLanguageServer) extends WorkspaceService {

  override def didChangeWatchedFiles(params: DidChangeWatchedFilesParams): Unit = {
    server.sendMsgToClient("didChangeWatchedFiles of Workspace")
  }

  override def didChangeConfiguration(params: DidChangeConfigurationParams): Unit = {
    server.sendMsgToClient("didChangeConfiguration of Workspace")
  }
}