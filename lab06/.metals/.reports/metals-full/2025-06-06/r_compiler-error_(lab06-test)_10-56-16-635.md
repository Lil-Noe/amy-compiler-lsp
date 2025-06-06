file:///C:/Users/lilia/Documents/EPFL/BA6/CLP/CS-320_Amy_LSP/lab06/test/scala/amyc/test/CompilerTest.scala
### java.lang.AssertionError: assertion failed

occurred in the presentation compiler.

presentation compiler configuration:


action parameters:
uri: file:///C:/Users/lilia/Documents/EPFL/BA6/CLP/CS-320_Amy_LSP/lab06/test/scala/amyc/test/CompilerTest.scala
text:
```scala
package amyc.test

import amyc.utils._
import java.io.File

import org.junit.Assert.fail

abstract class CompilerTest extends TestUtils {
  private def runPipeline(pipeline: Pipeline[List[File], Unit], fileNames: List[String]) = {
    val ctx = Context(new Reporter, fileNames)
    val files = ctx.files.map(new File(_))
    pipeline.run(ctx)(files)
    ctx.reporter.terminateIfErrors()
  }

  private def runPipelineRedirected(
    pipeline: Pipeline[List[File], Unit],
    compiledFiles: List[String],
    input: String
  ): String = {
    testWithRedirectedIO(runPipeline(pipeline, compiledFiles), input)
  }

  private def assertEqual(output: String, expected: String) = {
    val rejectLine = (s: String) =>
      s.isEmpty ||
        s.startsWith("[ Info  ]") ||
        s.startsWith("[Warning]") ||
        s.startsWith("[ Error ]") ||
        s.startsWith("[ Fatal ]")
    def filtered(s: String) = s.linesIterator.filterNot(rejectLine).mkString("\n")
    val filteredOutput = filtered(output)
    val filteredExpected = filtered(expected)
    if (filteredOutput != filteredExpected) {
      val sb = new StringBuffer()
      sb.append("\nOutput is different:\n")
      sb.append("\nOutput: \n")
      sb.append(filteredOutput)
      sb.append("\n\nExpected output: \n")
      sb.append(filteredExpected)
      sb.append("\n")
      fail(sb.toString)
    }
  }

  protected def compareOutputs(
    pipeline: Pipeline[List[File], Unit],
    compiledFiles: List[String],
    expectedFile: String,
    input: String = ""
  ) = {
    try {
      val output = runPipelineRedirected(pipeline, compiledFiles, input)
      val expected = scala.io.Source.fromFile(new File(expectedFile)).mkString
      assertEqual(output, expected)
    } catch {
      // We only want to catch AmyFatalError gracefully, the rest can propagate
      case AmycFatalError(msg) =>
        fail(s"\n  $msg\n")
    }
  }

  protected def demandPass(
    pipeline: Pipeline[List[File], Unit],
    compiledFiles: List[String],
    input: String = ""
  ) = {
    try {
      runPipelineRedirected(pipeline, compiledFiles, input)
    } catch {
      case AmycFatalError(msg) =>
        fail(s"\n  $msg\n")
    }
  }

  protected def demandFailure(
    pipeline: Pipeline[List[File], Unit],
    compiledFiles: List[String],
    input: String = ""
  ) = {
    try {
      runPipelineRedirected(pipeline, compiledFiles, input)
      fail("Test should fail but it passed!")
    } catch {
      case AmycFatalError(_) =>
      // Ok, this is what we wanted. Other exceptions should propagate though
    }

  }


}

```



#### Error stacktrace:

```
scala.runtime.Scala3RunTime$.assertFailed(Scala3RunTime.scala:11)
	dotty.tools.dotc.core.TypeOps$.dominators$1(TypeOps.scala:245)
	dotty.tools.dotc.core.TypeOps$.approximateOr$1(TypeOps.scala:381)
	dotty.tools.dotc.core.TypeOps$.orDominator(TypeOps.scala:399)
	dotty.tools.dotc.core.Types$OrType.join(Types.scala:3684)
	dotty.tools.dotc.core.Types$OrType.widenUnionWithoutNull(Types.scala:3700)
	dotty.tools.dotc.core.Types$Type.widenUnion(Types.scala:1386)
	dotty.tools.dotc.core.ConstraintHandling.widenOr$1(ConstraintHandling.scala:663)
	dotty.tools.dotc.core.ConstraintHandling.widenInferred(ConstraintHandling.scala:684)
	dotty.tools.dotc.core.ConstraintHandling.widenInferred$(ConstraintHandling.scala:29)
	dotty.tools.dotc.core.TypeComparer.widenInferred(TypeComparer.scala:31)
	dotty.tools.dotc.core.TypeComparer$.widenInferred(TypeComparer.scala:3298)
	dotty.tools.dotc.typer.Namer.rhsType$1(Namer.scala:2138)
	dotty.tools.dotc.typer.Namer.cookedRhsType$1(Namer.scala:2144)
	dotty.tools.dotc.typer.Namer.lhsType$1(Namer.scala:2145)
	dotty.tools.dotc.typer.Namer.inferredResultType(Namer.scala:2156)
	dotty.tools.dotc.typer.Namer.inferredType$1(Namer.scala:1802)
	dotty.tools.dotc.typer.Namer.valOrDefDefSig(Namer.scala:1808)
	dotty.tools.dotc.typer.Namer.defDefSig(Namer.scala:1934)
	dotty.tools.dotc.typer.Namer$Completer.typeSig(Namer.scala:830)
	dotty.tools.dotc.typer.Namer$Completer.completeInCreationContext(Namer.scala:985)
	dotty.tools.dotc.typer.Namer$Completer.complete(Namer.scala:859)
	dotty.tools.dotc.core.SymDenotations$SymDenotation.completeFrom(SymDenotations.scala:175)
	dotty.tools.dotc.core.Denotations$Denotation.completeInfo$1(Denotations.scala:190)
	dotty.tools.dotc.core.Denotations$Denotation.info(Denotations.scala:192)
	dotty.tools.dotc.core.SymDenotations$SymDenotation.ensureCompleted(SymDenotations.scala:393)
	dotty.tools.dotc.typer.Typer.retrieveSym(Typer.scala:3362)
	dotty.tools.dotc.typer.Typer.typedNamed$1(Typer.scala:3387)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3499)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3577)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3581)
	dotty.tools.dotc.typer.Typer.traverse$1(Typer.scala:3603)
	dotty.tools.dotc.typer.Typer.typedStats(Typer.scala:3649)
	dotty.tools.dotc.typer.Typer.typedClassDef(Typer.scala:3097)
	dotty.tools.dotc.typer.Typer.typedTypeOrClassDef$1(Typer.scala:3403)
	dotty.tools.dotc.typer.Typer.typedNamed$1(Typer.scala:3407)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3499)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3577)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3581)
	dotty.tools.dotc.typer.Typer.traverse$1(Typer.scala:3603)
	dotty.tools.dotc.typer.Typer.typedStats(Typer.scala:3649)
	dotty.tools.dotc.typer.Typer.typedPackageDef(Typer.scala:3230)
	dotty.tools.dotc.typer.Typer.typedUnnamed$1(Typer.scala:3449)
	dotty.tools.dotc.typer.Typer.typedUnadapted(Typer.scala:3500)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3577)
	dotty.tools.dotc.typer.Typer.typed(Typer.scala:3581)
	dotty.tools.dotc.typer.Typer.typedExpr(Typer.scala:3692)
	dotty.tools.dotc.typer.TyperPhase.typeCheck$$anonfun$1(TyperPhase.scala:47)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:15)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:10)
	dotty.tools.dotc.core.Phases$Phase.monitor(Phases.scala:503)
	dotty.tools.dotc.typer.TyperPhase.typeCheck(TyperPhase.scala:53)
	dotty.tools.dotc.typer.TyperPhase.$anonfun$4(TyperPhase.scala:99)
	scala.collection.Iterator$$anon$6.hasNext(Iterator.scala:479)
	scala.collection.Iterator$$anon$9.hasNext(Iterator.scala:583)
	scala.collection.immutable.List.prependedAll(List.scala:152)
	scala.collection.immutable.List$.from(List.scala:685)
	scala.collection.immutable.List$.from(List.scala:682)
	scala.collection.IterableOps$WithFilter.map(Iterable.scala:900)
	dotty.tools.dotc.typer.TyperPhase.runOn(TyperPhase.scala:98)
	dotty.tools.dotc.Run.runPhases$1$$anonfun$1(Run.scala:343)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:15)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:10)
	scala.collection.ArrayOps$.foreach$extension(ArrayOps.scala:1323)
	dotty.tools.dotc.Run.runPhases$1(Run.scala:336)
	dotty.tools.dotc.Run.compileUnits$$anonfun$1(Run.scala:384)
	dotty.tools.dotc.Run.compileUnits$$anonfun$adapted$1(Run.scala:396)
	dotty.tools.dotc.util.Stats$.maybeMonitored(Stats.scala:69)
	dotty.tools.dotc.Run.compileUnits(Run.scala:396)
	dotty.tools.dotc.Run.compileSources(Run.scala:282)
	dotty.tools.dotc.interactive.InteractiveDriver.run(InteractiveDriver.scala:161)
	dotty.tools.pc.MetalsDriver.run(MetalsDriver.scala:47)
	dotty.tools.pc.WithCompilationUnit.<init>(WithCompilationUnit.scala:31)
	dotty.tools.pc.SimpleCollector.<init>(PcCollector.scala:351)
	dotty.tools.pc.PcSemanticTokensProvider$Collector$.<init>(PcSemanticTokensProvider.scala:63)
	dotty.tools.pc.PcSemanticTokensProvider.Collector$lzyINIT1(PcSemanticTokensProvider.scala:63)
	dotty.tools.pc.PcSemanticTokensProvider.Collector(PcSemanticTokensProvider.scala:63)
	dotty.tools.pc.PcSemanticTokensProvider.provide(PcSemanticTokensProvider.scala:88)
	dotty.tools.pc.ScalaPresentationCompiler.semanticTokens$$anonfun$1(ScalaPresentationCompiler.scala:116)
```
#### Short summary: 

java.lang.AssertionError: assertion failed