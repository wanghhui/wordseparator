import scala.io.Source

object CompareSpreadSheet extends App{
  val ztc = Source.fromFile("ztc.csv")("GBK").getLines().toList
  val mxb = Source.fromFile("mxb.csv")("GBK").getLines().toList
  for(i<-ztc){
    val elems = i.split(",",4)
    val num = elems(0).trim.toInt
    val name = elems(1).trim
    val id = elems(2).trim
    for(j<-mxb){
      if(id.equalsIgnoreCase("")) {
        if (j.contains(name)) {
          val amount = j.substring(0, j.indexOf(',')).trim.toInt
          if (amount != num) println(name + id)
        }
      }
      else{
        if(j.contains(id)){
          val amount = j.substring(0,j.indexOf(',')).trim.toInt
          if(amount != num)println(name+id)}
      }
    }
  }
}
