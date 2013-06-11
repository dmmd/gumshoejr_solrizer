package org.nypl.gumshoejr

import scala.collection.mutable
import java.io.{File, FileInputStream}
import scala.io.Source
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FilenameUtils

class GumshoeSolrizer(packLoc: String){

  val pack: GumshoePackage = new GumshoePackage(packLoc)
  val fileHash = new mutable.HashMap[String, File]()
  val accessMap = new mutable.HashMap[String, File]()

  mapFiles
  println("Files mapped")
  if(pack.configMap("access").equals("TRUE")){
    mapAccessFiles
  }
  parseInventory();

  def mapFiles() : Unit = {
    for(file <- pack.parserFiles.listFiles){
      if(file.isFile)
        fileHash += (DigestUtils.md5Hex(new FileInputStream(file)) -> file)
    }
  }

  def parseInventory() : Unit = {
    var count = 0;
    for(line <- Source.fromFile(pack.indexFile).getLines()){
      if(pack.configMap("access").equals("TRUE")){
        println(line)
      } else {
        val fp = new FileProcessor(line, fileHash(line.split("\t")(6).toLowerCase()), pack.configMap)
      }
    }
  }

  def mapAccessFiles() : Unit = {
    val accessDir = new File(pack.parserFiles.getAbsolutePath + File.separator + "access")
    for(file <- accessDir.listFiles){
      accessMap += (FilenameUtils.getBaseName(file.getName) -> file)
    }
  }
}

object Main{
  def main(args: Array[String]) = {
    println("GumshoeJr Solrizer v.0.0.1")
    val gs: GumshoeSolrizer = new GumshoeSolrizer("src/main/resources/M6196_ER_0001")
  }
}