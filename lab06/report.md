# Lab06 - LSP & Formatting

## Computer Language Processing 2025 Extension Lab Final Report

## Contact Information

- Lilian Noé
  - email: <lilian.noe@epfl.ch>
- Roman Batut
  - email: <roman.batut@epfl.ch>
- Tristan Cendan
  - email: <tristan.cendan@epfl.ch>

## Abstract

The Lab06 was the final lab of the whole semester's project. The five first labs correctly and fully implement a compiler for Amy, and the sixth lab introduces the base of a language server and a formatter for this language. The motivation behind the choice of these features is the need for a guided and familiar user experience. In theory, we would want the formatter to turn any tokenizable .amy file into a well structured and standard form. This would imply removing unnecessary white spaces and new lines, arrange parentheses and brackets, comments, etc... The language server should work seemlessly with VSCode, giving access to goto definitions, code highlighting and formatting directly from the extension.

// TODO: Add what we actually did.

## Introduction

In the CS-320 class, Computer Language Processing, we implemented a compiler from a language called Amy to WebAssembly. Amy is similar to Scala, but a lot simpler. The exact specifications can be found [here](https://gitlab.epfl.ch/lara/cs320/-/blob/main/info/labs/amy-specification/AmySpec.md?ref_type=heads). 

The compiler is built as a pipeline, i.e. a succession of steps that gradually manipulate and transform the input. We start with a raw text file, or even multiples if there are dependencies. This string is then tokenized by the lexer, before being abstracted into an Abstract Syntax Tree by the parser, and finally turned into WebAssembly. Between the last two steps, the compiler takes care of the different variables, functions, classes and types name, as well as the types themselves, thanks to the name analyzer and the type checker. Those steps specifically determine whether the program can be compiled.

Our extensions are a formatter and a language server. A formatter is quite self explanatory ; it allows to have a consistent and familiar syntax. The language server allows for more in depth integration of the language when using an IDE, providing highlighting, goto definitions, etc... We believe those extensions fit well in this whole project, as they focus on the user experience, a point that was not considered before. Those tools would allow for a more intuitive and familiar environment to the programmer. 

The formatter is simply a fork at a set point of the pipeline, reusing the already present infrastructure and only adding components when absolutely needed. The language server acts as a standalone. It uses the whole compiling pipeline, then wraps around it with the necessary server and client infrastructures. 

## Examples and Use of the Extension

Let's suppose we have the following unclear and confusing module:  

```scala
        object Hanoi
	
def 

solve(n : Int(
    32)
) : Int(32) = {    
     if (n < 1) {      error("can't solve Hanoi for less than 1 plate") 
   }                else 
   {      if (n == 1) {        1      } else {   
     2 * (solve(n - 1) + 1)      }   
 }  }  Std.printString("Hanoi for 4 plates: " ++ Std.


intToString(solve(4)
))

end                 Hanoi
```

Thankfully, the Amy compiler can successfully tokenize this code. Since this condition is met, running the Amy compiler with the `--format` parameter will turn this unusable piece of code into this consistent and standard format:

```scala
object Hanoi

  def solve(n: Int(32)): Int(32) = 
  {
    if (n < 1)
    {
      error("can't solve Hanoi for less than 1 plate")
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

This formatted string will then be written back to its corresponding file. The formatter does not care about the number of files or the number of modules in each file. Similarly, it does not care about the content of the module itself. The only condition that implies the possibility of formatting is that the code reaches successfully the end of the lexer.

---

Give code examples where your extension is useful, and describe how users would work with it. Make sure you include examples where the most intricate features of your extension are used, so that we have an immediate understanding of what the challenges are. For this section, assume that your reader is a user of Amy language that received a feature upgrade thanks to your project.

You can pretty-print tool code like this, using triple backticks and scala as the language. 

```scala
object {
  def main() : Unit = { println(new A().foo(−41)); }
}

class A {
  def foo(i : Int) : Int = {
    var j : Int;
    if(i < 0) { j = 0− i; } else { j = i; }
    return j + 1;
  }
}
```

If you introduce new syntax, do not worry if it is not colored correctly.

This section should convince us that you understand how your extension can be useful and that you thought about the corner cases.

Please also use this section to explain how users would see the results of your work, including how to invoke the compiler with additional command-line options, if it applies to your work.

## Implementation

This is a very important section, you explain to us how you made it work. It it not acceptable to have a project that you do not understand why it works. If you used tools or friends to help you generate the code, you still need to understand and describe it.


### Background

If you are using theoretical concepts, explain them first in this subsection. Even if they come from the course, try to explain the essential points in your own words. Cite any reference work you used like this [Appel 2002]. This should convince us that you know the theory behind what you coded. Web sites and blog posts are not ideal, but if you used them, create references for them and cite them. If you made use of GitHub projects, also create citations for them and cite them. 

### Implementation details

Describe all non-obvious techniques that you used. Tell us what you thought was hard and why. If it took you time to figure out the solution to a problem, it probably means it wasn’t easy and you should definitely describe the solution in details here. If you used what you think is a cool algorithm for some problem, tell us. Do not spend time describing trivial things (what a tree traversal is, for instance). Do not repeat the material that was explained in the lecture.

After reading this section, we should be convinced that you knew what you were doing when you wrote your extension, and that you put some extra considera- tion for the harder parts.

## Possible Extensions

The most obvious 

---

If you did not finish what you had planned, explain here what is missing.

In any case, describe how you could further extend your compiler in the direction you chose. This section should convince us that you understand the challenges of writing a good compiler for high-level programming languages.

## References

- A. W. Appel. *Modern Compiler Implementation in Java.* Cambridge University Press, 2nd edition, 2002.
- V. Kuncak. *How to use DBLP web site to generate nice citations and why you should include DOI links.*, Madeup Conference, Lausanne, May 2025, https://doi.org/10.1145/3385412.3385992
