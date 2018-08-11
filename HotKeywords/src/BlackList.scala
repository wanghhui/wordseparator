import java.io.{File, PrintWriter}

import scala.io.Source
import scala.collection.immutable.HashSet

class BlackList {
  def compareKeywords(sourceFileName:String, processedFileName:String):HashSet[String] = {
    val source = HashSet(Source.fromFile(sourceFileName).getLines().toList: _*)

    val a = Source.fromFile(processedFileName).getLines()
    val processed = HashSet(Source.fromFile(processedFileName).getLines().toList: _*)

    val black_writer = new PrintWriter(new File(sourceFileName+"_black.txt"))

    val diff = source.filter(s => !processed.contains(s))

    for(word <- diff)black_writer.write(word+'\n')
    black_writer.close()

    processed
  }
}
object BlackList extends App {

  val bl = new BlackList
  val path_source = "./keywords/before_screen/"
  val path_processed = "./keywords/after_screen/"

  var result: HashSet[String] = new HashSet[String]
  for(i <- 1 to 10) result = result ++ bl.compareKeywords(path_source+"word"+i+".txt",path_processed+"word"+i+".txt")
  val writer = new PrintWriter(new File("./keywords/white.txt"))
  for(i <- result if i.length >1 )writer.write(i+'\n')
  writer.close()

//
//  val before = Source.fromFile("keywords_before_LM").getLines().toList
//  val after = Source.fromFile("keywords_after_LM").getLines().toList
//
//  val full = Map(
//    before.map(s =>
//      (s.substring(0, s.indexOf(",")),
//       s.substring(s.indexOf(",") + 1).trim.toInt)): _*)
//  val tailor = Map(
//    after.map(s =>
//      (s.substring(0, s.indexOf(",")),
//       s.substring(s.indexOf(",") + 1).trim.toInt)): _*)
//
//  val dif = full.filter(elem => !tailor.contains(elem._1))
//
//  val fs = new FreqScrawler
//  fs.keywords_writer(dif.toSeq, "black")
//

}
