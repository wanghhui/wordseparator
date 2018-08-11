import java.io.{File, PrintWriter}


import scala.collection.immutable.HashSet
import scala.io.Source

object DupRemover extends App {

  val source = HashSet(Source.fromFile("./keywords/keyword20180724").getLines().toList: _*)
  val writer = new PrintWriter(new File("./keywords/white.txt"))
  for(i <- source if i.length >1 )writer.write(i+'\n')
  writer.close()

}
