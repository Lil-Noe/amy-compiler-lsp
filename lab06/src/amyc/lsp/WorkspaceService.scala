package amyc.lsp

import org.eclipse.lsp4j.services.WorkspaceService
import org.eclipse.lsp4j.DidChangeConfigurationParams
import org.eclipse.lsp4j.DidChangeWatchedFilesParams

class AmyWorkspaceService extends WorkspaceService {

  override def didChangeWatchedFiles(params: DidChangeWatchedFilesParams): Unit = {
  }

  override def didChangeConfiguration(params: DidChangeConfigurationParams): Unit = {
  }
}