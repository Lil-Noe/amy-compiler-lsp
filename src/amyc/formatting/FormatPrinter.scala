package amyc.formatting

import amyc.utils._
import amyc.parsing._
import amyc.parsing.Tokens._
import scala.collection.mutable.ListBuffer
import java.nio.file._
import java.nio.charset.StandardCharsets
import java.io.File

object FormatPrinter extends Pipeline[List[(File, String)], Unit] {
    override def run(ctx: Context)(files: List[(File, String)]) = {
      files.foreach { case (file, content) =>
        Files.write(
            file.toPath,
            content.getBytes(StandardCharsets.UTF_8)
        )
        ctx.reporter.info(s"Formatted file: ${file.toString}")
      }
    }
}
