import java.io.{File, PrintWriter}
import scala.io.Source
import scala.util.matching.Regex

object OutputScreener {
  def main(args: Array[String]): Unit = {
    val output_lines = Source.fromFile("AudioOutput.log").getLines().toList
    var result = scala.collection.mutable.HashMap[String, String]()
    for (line <- output_lines) {
      val tax_pattern: Regex = "\\[.*\\]\\[.*\\].*\\[.*\\].*".r
      val item_pattern: Regex = "\\[.*?\\]".r
      tax_pattern.findFirstIn(line) match {
        case Some(a: String) => {
          val s = item_pattern.findAllIn(a)
          s.next()
          val filename = s.next()
          val truncated_filename = filename.substring(filename.lastIndexOf('/') + 1, filename.indexOf('.'))
          val content = s.next()
          val tailored = if(content.size>2)content.substring(2, content.size - 2)else ""
          if (result.contains(truncated_filename)) result += (truncated_filename -> (result(truncated_filename) + tailored))
          else result += (truncated_filename -> tailored)
        }
        case None =>
      }
    }
    val writer = new PrintWriter(new File("export.csv"), "UTF8")
    for ((k, v) <- result) writer.write('"' + k + '"' + ',' + '"' + v + '"' + '\n')
    writer.close()
  }
}
