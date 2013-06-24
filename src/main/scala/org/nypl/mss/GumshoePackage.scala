package org.nypl.mss

import java.io.{FileInputStream, File}
import org.apache.commons.io.FilenameUtils
import scala.collection.mutable
import scala.io.Source

class GumshoePackage(packageLoc: String) {
  val files: File = new File(packageLoc)
  var indexFile: File = null
  var parserFiles: File = null
  val configMap = new mutable.HashMap[String, String]()

  for(file <- files.listFiles()){
    if(FilenameUtils.getExtension(file.getName) == "tsv"){
      indexFile = file
    } else if(FilenameUtils.getExtension(file.getName) == "txt"){
      for(line <- Source.fromInputStream(new FileInputStream(file)).getLines()){
        val kv = line.split(":\\s*")
        configMap += (kv(0) -> kv(1))
      }
    } else if(file.getName == "files" && file.isDirectory && file.listFiles.size > 1){
      parserFiles = file
    }
  }

  private def checkValid() : Boolean = {
    if(indexFile != null && parserFiles != null) true else false
  }

  override def toString() : String = {
    val sb = new mutable.StringBuilder()

    sb.append(files.getName() + "\nfiles: " + parserFiles.getAbsolutePath + "\nindex: " + indexFile.getAbsoluteFile
    + "\nconfiguration:")
    for(kv <- configMap){
      sb.append("\n\t" + kv._1 +": " + kv._2);
    }
    sb.toString
  }

}
