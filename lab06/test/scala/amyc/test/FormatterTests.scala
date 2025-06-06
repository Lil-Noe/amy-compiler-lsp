package amyc.test

import amyc.utils._
import amyc.ast._
import amyc.parsing._
import amyc.formatting._
import java.io.File
import org.junit.Test

class FormatterTests extends TestSuite {

  private val testFormatPrinter: Pipeline[List[(File, String)], Unit] = {
    new Pipeline[List[(File, String)], Unit] {
      def run(ctx: Context)(f: List[(File, String)]): Unit = {
        f.foreach { case (filename, content) =>
          println(filename.getName)
          println(content)
        }
      }
    }
  }
  
  val pipeline = Formatter.andThen(testFormatPrinter)

  val baseDir = "formatter"

  val outputExt = "txt"

  @Test def testEmpty = shouldOutput("Empty")

  @Test def testNoChange = shouldOutput("NoChange")

}