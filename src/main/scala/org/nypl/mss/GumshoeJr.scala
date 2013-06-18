package org.nypl.mss

import com.typesafe.config._
import scala.collection.mutable
import java.io.{File, FileInputStream}
import scala.io.Source
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FilenameUtils
import org.apache.solr.client.solrj.impl.HttpSolrServer

object Launch{
  def main(args: Array[String]) = {
    println("GumshoeJr Solrizer v.0.0.1")
    println("You passed: " + args(0))
    val gs: GumshoeSolrizer = new GumshoeSolrizer(args(0))
  }
}

class GumshoeSolrizer(packLoc: String){
  val pack: GumshoePackage = new GumshoePackage(packLoc)
  val fileHash = new mutable.HashMap[String, File]()
  val accessMap = new mutable.HashMap[String, File]()
  val conf = ConfigFactory.load()
  val solr : HttpSolrServer = new HttpSolrServer(conf.getString("solr.test"))

  if(solr.ping().getStatus == 0){
    println("Connected to Solr Server")
    mapFiles()
    if(pack.configMap("access").equals("TRUE")){mapAccessFiles}
    parseInventory()
    solr.commit
    solr.optimize


    } else {
    println("Unable to connect to Solr server, exiting")
    System.exit(1)
  }



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
        solr.add(new FileProcessor(line, fileHash(line.split("\t")(6).toLowerCase()), pack.configMap).solrDoc)
        solr.commit
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