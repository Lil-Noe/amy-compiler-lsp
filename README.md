# Amy Compiler & LSP

This project has been made by Tristan Cendan, Roman Batut and Lilian Noé.

The goal of the class and of the project is to implement a compiler for the Amy language, a specific programming language based on Scala. The original pipeline build was guided up to WebAssembly code generation.

The final part of this project is to implement additional features on top of this project.
Our goal was to have user-oriented features, that's why we decided to implement a formatter and an LSP using a VSCode Extension.


## Compiler & Interpreter

The core of this project is a fully functional compiler and interpreter for Amy, a specific statically-typed programming language based on Scala. The pipeline can either execute Amy source code (`.amy` files) directly on the JVM with the interpreter, or translate it into executable WebAssembly (`.wasm`) binaries with the compiler.
This part was guided in the context of EPFL CS-320 class.

### Compilation Pipeline

The compiler consists in a multi-stage pipeline:

1. **Lexical Analysis (Lexer)**: Uses the formally verified `ZipLex` library to read source code and transform it into a sequence of structured Tokens.
2. **Syntax Analysis (Parser)**: Uses the `Scallion` parser combinator library to process the tokens through a LL(1) grammar to build an Abstract Syntax Tree (AST).
3. **Semantic Analysis**:
    - **Name Analyzer**: Resolves variable and function scopes, transforming the Nominal AST into a Symbolic AST. 
    - **Type Checker**: Implements a simplified Hindley-Milner type inference algorithm to statically prove the absence of runtime type errors.
4. **Execution**:
    - **Interpretation**: Executes directly the verified program in memory on the JVM using an AST-walking interpreter, evaluating expressions and managing local variable state.
    - **Code Generation**: Flattens the verified AST into WebAssembly instructions, managing memory allocation for Case Classes on the WebAssembly memory heap.

### Usage

To run the project, you will need both Java and `sbt` installed.

* **Direct Interpretation**:

To execute the code immediately without generating binaries, use the `--interpret` flag:
```bash
sbt "run --interpret examples/Hello.amy"
```

* **Code Generation**: 

Run the compiler through `sbt` by passing the target program:

```bash
sbt "run examples/Hello.amy"
```

If the compilation is successful, the compiler will generate two files in the `wasmout/` directory:
* `Hello.wat`: A human-readable text representation of the WebAssembly code.
* `Hello.wasm`: The binary executable.

Because WebAssembly is a low-level binary format, you need a virtual machine to execute it locally.  
You will need `wasmtime` (the WebAssembly runtime) and optionally `wabt` (the WebAssembly Binary Toolkit, containing `wat2wasm`).
* **macOS**: `brew install wasmtime wabt`
* **Linux**: `apt install wabt and curl https://wasmtime.dev/install.sh -sSf | bash`
* **Other (`wasmtime`)**: Download it at https://docs.wasmtime.dev/cli-install.html
* **Other (`wat2wasm`)**: Download it at https://github.com/WebAssembly/wabt/releases/tag/1.0.31

To finally run the program, you can pass the generated `.wasm` binary to the `wasmtime` engine:

```bash
wasmtime wasmout/Hello.wasm
```

### Execution Scripts

For faster execution, this project provides bash wrapper scripts in the [*scripts*](./scripts/) folder. These scripts bypass the `sbt` boot sequence by directly executing the compiled `.jar` artifact.

1. The Compiler (`amyc.sh`)
Compiles the provided files into WebAssembly binaries (.wasm).
```bash
./amyc.sh examples/Hello.amy
```

2. The Interpreter (`amyi.sh`)
Directly interprets and executes the provided files on the JVM.
```bash
./amyi.sh examples/Hello.amy
```

3. The Type-Checker (`amytc.sh`)
Runs the frontend pipeline up to the Type-Checking phase and halts. Useful for verifying program correctness without executing it.
```bash
./amytc.sh examples/Hello.amy
```







## Language Server Process Extension

To launch the Amy extension that includes the LSP, either : 
1. Open amy-extension folder in VSCode
2. Run the command `npm install` in the terminal
3. Run the command `npm run compile` in the terminal
4. Go to 'Run and Debug' on VSCode and click on the 'Run Extension' button

Or
- Use the amy.vsix extension file and install it on VSCode using extension manager. You can find [here](https://code.visualstudio.com/docs/configure/extensions/extension-marketplace#_install-from-a-vsix) the documentation to install a vsix file.


When the extension is installed, open the `CS-320_Amy_LSP\amy-files folder` (or any folder that contains amy files) at the root of the project to test it with amy files.

If changes are made to the compiler and LSP Server, here is the procedure to compress it to a .jar file and include it in the extension.
1. First, open the lab05 folder and run `sbt reload clean assembly`
2. Then go look for the .jar file at `\target\scala-3.7.2\amyc-assembly-1.7.jar`
3. Rename it to lsp-server.jar and include it into `CS-320_Amy_LSP\amy-extension\server\`

If changes are made to the extension, here is the procedure to compress it to a .vsix file.
1. Open the `amy-extension` folder and run `npm run compile` in the terminal
2. Run the command `vsce package` in the terminal

## Formatter

To format the files, run the command `--format` next to the files name in the terminal

e.g. `sbt "run --format examples/Hello.amy examples/Hanoi.amy"`
