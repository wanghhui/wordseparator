import java.io.{File, PrintWriter}

import scala.io.Source

object WordsListGenerator extends App {
  val keywords_lines = Source.fromFile("keywords").getLines().toList
  val writer = new PrintWriter(new File("pure_keywords"))
  var i = 0
  for (line <- keywords_lines; if i < 2000) {
    i = i + 1
    if (line.contains(","))
      writer.write(line.substring(0, line.indexOf(",")) + "\n")
    else println(line)
  }
  writer.close()
}
