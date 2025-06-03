import * as path from 'path';
import { ExtensionContext } from 'vscode';

import { LanguageClient, LanguageClientOptions, ServerOptions } from 'vscode-languageclient/node'

export function activate(context: ExtensionContext) {
	console.log('Congratulations, your extension "amy" is now active!');

	// The server path
	let serverPath = context.asAbsolutePath(path.join('server', 'lsp-server.jar'));

	// Arguments to pass to the LSP server
	const args = ['-jar', serverPath, 'amyc.Main.scala', '--server_mode'];
	
	// The server options
	const serverOptions: ServerOptions = {
		command: 'java', 
		args
	};

	// The client options
	const clientOptions: LanguageClientOptions = {
		documentSelector: [{ scheme: 'file', language: 'amy' }]	
	};

	// Create the language client and start the client
	const client = new LanguageClient(
		'amyLanguageServer',
		'AMY Language Server',
		serverOptions,
		clientOptions
	)
	client.start();

	context.subscriptions.push(client);
}

// This method is called when your extension is deactivated
export function deactivate() {
	console.log('Your extension "amy" is now deactivated!');
}
