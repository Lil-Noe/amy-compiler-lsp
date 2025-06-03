# CS-320 Amy Project

This project has been made by Tristan Cendan, Roman Batut and Lilian Noé.

This goal of the class and of the project is to implement a compiler for the Amy language, a specific programming language based on Scala. The original pipeline build was guided up to WebAssembly code generation.

The final part of this project is to implement additional features on top of this project.
Our goal was to have user-oriented features, that's why we decided to implement a formatter and an LSP using a VSCode Extension.


## Language Server Process Extension

To launch the Amy extension, either : 
* Opening amy-extension folder in VSCode
* Run the command "npm install" in the terminal
* Run the command "npm run compile" in the terminal
* Go to 'Run and Debug' on VSCode and click on the 'Run Extension' button

Or
* We packaged the extension into amy.vsix file for anyone to install it on VSCode using extension manager
* Here is the documentation to install a vsix file : [link](https://code.visualstudio.com/docs/configure/extensions/extension-marketplace#_install-from-a-vsix)


When the extension is installed, we can open the test-folder or use the extension with any .amy files.


## Formatter

To format the files, we need to run the command '--format' next to the files name in the terminal