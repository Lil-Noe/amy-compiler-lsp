# CS-320 Amy Project

This project has been made by Tristan Cendan, Roman Batut and Lilian Noé.

This goal of the class and of the project is to implement a compiler for the Amy language, a specific programming language based on Scala. The original pipeline build was guided up to WebAssembly code generation.

The final part of this project is to implement additional features on top of this project.
Our goal was to have user-oriented features, that's why we decided to implement a formatter and an LSP using a VSCode Extension.


## Language Server Process Extension

To launch the Amy extension that includes the LSP, either : 
1. Opening amy-extension folder in VSCode
2. Run the command "npm install" in the terminal
3. Run the command "npm run compile" in the terminal
4. Go to 'Run and Debug' on VSCode and click on the 'Run Extension' button

Or
1. Use the amy.vsix extension file and install it on VSCode using extension manage (here is the documentation to install a vsix file : [link](https://code.visualstudio.com/docs/configure/extensions/extension-marketplace#_install-from-a-vsix) )


When the extension is installed, open the CS-320_Amy_LSP\amy-files folder at the root of the project to test it with amy files.

Here are a few point to take care regarding the LSP use : 
- The files to test it on have to include the "amy-files" folder somewhere in their parents folders.
- The "library" folder including Std.amy, List.amy and Option.amy has to be directly in the "amy-files" folder without any other subfolders in between.
- The go to definition feature only works in the same file. This means the definition has to be in the same file as the variable for it to work.


If changes are made to the compiler and LSP Server, here is the procedure to compress it to a .jar file and include it in the extension.
1. First, open the lab06 folder and run "sbt reload clean assembly"
2. Then go look for the .jar file at \target\scala-3.5.2\amyc-assembly-1.7.jar
3. Rename it to lsp-server.jar and include it into CS-320_Amy_LSP\amy-extension\server\


## Formatter

To format the files, we need to run the command '--format' next to the files name in the terminal
