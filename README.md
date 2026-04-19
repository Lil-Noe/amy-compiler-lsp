# Amy Compiler & LSP

Implements a complete compiler and interpreter pipeline for Amy language, along with a formatter and a Language Server Protocol.
The LSP includes the server part on Amy side and a VS Code extension that implements the client side and some syntax highlighting.

This project is part of the EPFL CS-320 Computer Language Processing course.
Authors: Lilian Noé, Tristan Cendan, Roman Batut.

## Compiler & Interpreter

The core of this project is a fully functional compiler and interpreter for Amy, a specific statically-typed programming language based on Scala. The pipeline can either execute Amy source code (`.amy` files) directly on the JVM with the interpreter, or translate it into executable WebAssembly (`.wasm`) binaries with the compiler.
This part was guided in the context of EPFL CS-320 class.

### Compilation Pipeline

The compiler consists in a multi-stage pipeline:

1. **Lexical Analysis (Lexer)**: Uses the `Silex` library to read source code and transform it into a sequence of structured Tokens.
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

## Formatter Feature

The in-place formatter aims to keep familiar and consistent syntax by re-writing Amy files following pre-defined format rules.

To format the desired files, run the command `--format` next to the files path:
```bash
sbt "run --format examples/Hello.amy examples/Hanoi.amy"
```

Note must be taken that the concerned file must at least be tokenizable for the formatter to work.

Here is an example of how the formatter works :

```scala
// Before formatting: 
        object Hanoi
def 
solve(n : Int(
    32)
) : Int(32) = {    
     if (n < 1) {      error("can't solve Hanoi for less than 1 plate") // Skip if not enough plates
   }                else 
   {      if (n == 1) {        1      } else {   
     2 * (solve(n - 1) + 1)      }   
 }  }  Std.printString("Hanoi for 4 plates: " ++ Std.


intToString(solve(4)
))
end                 Hanoi
```

```scala
// After formatting: 
object Hanoi

  def solve(n: Int(32)): Int(32) = 
  {
    if (n < 1)
    {
      error("can't solve Hanoi for less than 1 plate")
      // Skip if not enough plates
    }
    else 
    {
      if (n == 1)
      {
        1
      }
      else 
      {
        2 * (solve(n - 1) + 1)
      }
    }
  }

  Std.printString("Hanoi for 4 plates: " ++ Std.intToString(solve(4)))
end Hanoi

```


## Language Server Protocol

The **LSP (Language Server Protocol)** is a communication protocol to exchange messages between an IDE (the client) and the language (the server). Based on information the client sends (e.g. the file and the cursor's position), the server provides specific functionalities, like go-to-definition
or dynamic completion.

### Client Side

To show how the LSP works and try it, we have created a VS Code extension in [*`amy-extension`*](/amy-extension/).  
This extension also features a syntax highlighter for Amy files.

Here is the process to launch this Amy extension: 
1. Open the [*`amy-extension`*](/amy-extension/) folder.
2. Run the command `npm install` in the terminal.
3. Run the command `npm run compile` in the terminal.
4. Go to '*Run and Debug*' on VSCode and click on the '*Run Extension*' button.

You can also compress the folder to a `.vsix` file and it install permanently using the extension manager:
1. Open the [*`amy-extension`*](/amy-extension/) folder and run `npm run compile`.
2. Run the command `vsce package`.
3. Install the `amy.vsix` file using the extension manager ([here](https://code.visualstudio.com/docs/configure/extensions/extension-marketplace#_install-from-a-vsix) is the documentation to install a `.vsix` file in VS Code). 

Once the extension is installed or launched, you can test it with amy files present in  [*`examples`*](/examples/) folder.


### Server Side

The Language Server implements the **Go-to-definition** feature, which consists in getting redirected to a variable or a function definition when clicking on an occurrence.  

With the VS Code extension running (or other IDE Amy extension), one only needs to `Ctrl+Click` (or `Cmd+Click` on Mac) on a variable, or press `F12` when the variable is selected, to get re-directed to the definition. 

If changes are made to the compiler and LSP Server, here is the way to compress it to a `.jar` file:
1. First, run the following command: 
```bash 
sbt reload clean assembly 
```
2. Then, look for the `.jar` file at *`target/scala-3.7.2/amyc-assembly-1.7.jar`*
3. Rename it to `lsp-server.jar` and put it in the [*`amy-extension/server/`*](./amy-extension/server/) folder.

## Project Structure

```text
amy-compiler-lsp/
├── README.md
├── .gitignore
├── build.sbt
│
├── docs/
├── lib/
├── project/
├── scripts/
│
├── amy-extension/              # The LSP VS Code Client
│
├── examples/                   # Some Amy files
│
│
├── src/
│   └── amyc/
│       │
│       ├── analyzer/           # Name Analysis and Type Checking
│       ├── ast/                # Abstract Syntax Tree definitions
│       ├── codegen/            # WebAssembly Code Generation
│       ├── formatting/         # The formatter feature
│       ├── interpreter/        # JVM-based tree-walking interpreter for direct execution
│       ├── lsp/                # Language Server Protocol Implementation
│       ├── parsing/            # Lexer (Silex) and Parser (Scallion)
│       ├── wasm/               # WebAssembly AST definitions and module printer logic
│       ├── utils/   
│       │
│       └── Main.scala
│ 
├── test/                       # JUnit Test Suites
│   ├── resources/       
│   └── scala/ 
```
