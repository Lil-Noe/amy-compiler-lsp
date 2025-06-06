package amyc.test

import amyc.utils._
import amyc.ast._
import amyc.parsing._
import amyc.formatter._
import org.junit.Test

class FormatterTests extends TestSuite {

  private val testFormatPrinter: Pipeline[List[(String, String)], Unit] = {
    new Pipeline[List[(String, String)], Unit] {
      def run(ctx: Context)(f: List[(String, String)]): Unit = {
        f.foreach { case (filename, content) =>
          println(filename)
          println(content)
        }
      }
    }
  }
  
  val pipeline = AmyLexer.andThen(Formatter).andThen(testFormatPrinter)

  val baseDir = "formatter"

  val outputExt = "txt"

  @Test def testEmpty = shouldOutput("Empty")

  @Test def testNoChange = shouldOutput("NoChange")

}