package amyc.formatter

import amyc.utils._
import amyc.parsing._
import amyc.parsing.Tokens._
import scala.collection.mutable.ListBuffer
import java.nio.file._
import java.nio.charset.StandardCharsets

object FormatPrinter extends Pipeline[List[(String, String)], Unit] {
    override def run(ctx: Context)(files: List[(String, String)]) = {
      files.foreach { case (fileName, content) =>
        ctx.reporter.info(s"Formatting $fileName")
        Files.write(
            Paths.get(fileName),
            content.getBytes(StandardCharsets.UTF_8)
        )
      }
    }
}
