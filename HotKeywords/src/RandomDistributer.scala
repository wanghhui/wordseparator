import java.io.{File, PrintWriter}

import scala.collection.immutable.HashSet

import scala.io.Source
import scala.util.Random

object RandomDistributer extends App {
  val textLines = Source
    .fromFile("f2t5.txt")
    .getLines()
    .map(s => s.substring(0, s.indexOf(',')))
    .toList
  val full = Map(textLines.map(s => (s, 0)): _*)

  //val all :HashSet[String]= new HashSet[String](textLines)
  var processed: HashSet[String] = new HashSet[String]

  for (i <- 1 to 9) {
    val writer = new PrintWriter(new File("word" + i + ".txt"))

    for (i <- 1 to 2000) {
      val word = getNumber(textLines, Random.nextInt(textLines.size))
      processed = processed + word
      writer.write(word + "\n")
    }

    writer.close()
  }
  val writer = new PrintWriter(new File("word10.txt"))

  val dif = full.filter(s => !processed.contains(s._1))
  for (word <- dif) writer.write(word._1 + "\n")
  writer.close()

  def getNumber(list: List[String], index: Int): String = {
    var listVar = list
    for (i <- 0 until index) listVar = listVar.tail
    listVar.head
  }

}
