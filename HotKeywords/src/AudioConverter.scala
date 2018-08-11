import sys.process._
import java.io.File

//sox 1000352.V3 1000352.wav silence -l 1 0.3 1% -1 0.5 1%

object AudioConverter {
  val outputPath = "D:\\output\\"
  val mp3 = new File(outputPath+"mp3")
  if(!mp3.exists())mp3.mkdir()
  val pcm = new File(outputPath+"pcm")
  if(!pcm.exists())pcm.mkdir()


  def main(args: Array[String]): Unit = {
    val workingDirPath = "d:\\input"
    silenceRemover(workingDirPath)
    converter

  }

  def silenceRemover(path: String): Unit = {
    val workingDir = new File(path)
    for (file <- workingDir.listFiles) {
      if (file.isDirectory) silenceRemover(file.getAbsolutePath)
      else {
        //iffile.length())
        val s: Array[String] = file.getAbsoluteFile.toString.split('\\').reverse
        val output_filename = outputPath + s(0).substring(0, s(0).indexOf('.')) + '_' + s(1) + '_' + s(2) + ".wav"
        if(file.length()>300000 && file.length() < 1300000 ) {
          "sox " + file.getAbsoluteFile + " " + output_filename + " silence -l 1 0.3 1% -1 0.5 1%" !
        }
      }
    }
  }

  def converter = {
         for(file <- new File(outputPath).listFiles.filter(_.getName.contains("wav"))){
          // println(file.getName + "  "+ file.getAbsoluteFile)
           "ffmpeg -y -i " + file.getAbsoluteFile + " -acodec pcm_s16le -f s16le -ac 1 -ar 16000 " + outputPath+"pcm\\"+file.getName+".pcm" !

           "ffmpeg -i " + file.getAbsoluteFile + " -acodec libmp3lame " + outputPath+"mp3\\"+file.getName+".mp3" !

           file.delete()
         }
  }
}

