"use strict";
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    var desc = Object.getOwnPropertyDescriptor(m, k);
    if (!desc || ("get" in desc ? !m.__esModule : desc.writable || desc.configurable)) {
      desc = { enumerable: true, get: function() { return m[k]; } };
    }
    Object.defineProperty(o, k2, desc);
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __importStar = (this && this.__importStar) || (function () {
    var ownKeys = function(o) {
        ownKeys = Object.getOwnPropertyNames || function (o) {
            var ar = [];
            for (var k in o) if (Object.prototype.hasOwnProperty.call(o, k)) ar[ar.length] = k;
            return ar;
        };
        return ownKeys(o);
    };
    return function (mod) {
        if (mod && mod.__esModule) return mod;
        var result = {};
        if (mod != null) for (var k = ownKeys(mod), i = 0; i < k.length; i++) if (k[i] !== "default") __createBinding(result, mod, k[i]);
        __setModuleDefault(result, mod);
        return result;
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
exports.activate = activate;
exports.deactivate = deactivate;
const path = __importStar(require("path"));
const node_1 = require("vscode-languageclient/node");
function activate(context) {
    console.log('Congratulations, your extension "amy" is now active!');
    // The server path
    let serverPath = context.asAbsolutePath(path.join('server', 'lsp-server.jar'));
    // Arguments to pass to the LSP server
    const args = ['-jar', serverPath, 'amyc.Main.scala', '--server_mode'];
    // The server options
    const serverOptions = {
        command: 'java',
        args
    };
    // The client options
    const clientOptions = {
        documentSelector: [{ scheme: 'file', language: 'amy' }]
    };
    // Create the language client and start the client
    const client = new node_1.LanguageClient('amyLanguageServer', 'AMY Language Server', serverOptions, clientOptions);
    client.start();
    context.subscriptions.push(client);
}
// This method is called when your extension is deactivated
function deactivate() {
    console.log('Your extension "amy" is now deactivated!');
}
//# sourceMappingURL=extension.js.map