import java.io.PrintWriter
import java.io.File

import scala.io.Source

class FreqScrawler {
  def FreqScrawl(keywords_raw: Map[String, Int],
                 raw_lines: Iterator[String]): Seq[(String, Int)] = {
    var keywords_map = keywords_raw
    for (raw_line <- raw_lines)
      keywords_map = keywords_map.map {
        case (keyword, freq) =>
          if (raw_line.contains(keyword)) (keyword, freq + 1)
          else (keyword, freq)
      }

    keywords_map.toSeq.sortWith(_._2 > _._2)
  }

  def keywords_writer(keywords: Seq[(String, Int)], fileName: String) = {
    val writer = new PrintWriter(new File(fileName))
    for (i <- keywords) {
      writer.write(i._1 + ", " + i._2 + "\n")
    }
    writer.close()
  }
}

object Main extends App {
  val fs = new FreqScrawler
  val blackList = Map(
    Source
      .fromFile("black")
      .getLines()
      .toList
      .map(s =>
        (s.substring(0, s.indexOf(",")),
         s.substring(s.indexOf(",") + 1).trim.toInt)): _*)
  val raw_text_lines = Source.fromFile("raw_text").getLines()
  val kw1 = Source.fromFile("keywords_before_freqscan").getLines().toList
  val kw2 = Source.fromFile("keywords_large.txt").getLines().toList

  var kmfull = Map(kw1.map(s => (s, 0)): _*) ++ Map(kw2.map(s => (s, 0)): _*)

  //for(i <- 2 to 20)  fs.keywords_writer(fs.FreqScrawl(kmfull.filter(kf =>kf._1.length==i && !blackList.contains(kf._1)),raw_lines),i+"CKeyW.txt")

  fs.keywords_writer(
    fs.FreqScrawl(
      kmfull.filter(kf =>
        kf._1.length > 1 && kf._1.length < 6 && !blackList.contains(kf._1)),
      raw_text_lines),
    "f2t5.txt")

}
