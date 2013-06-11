package org.nypl.gumshoejr

import java.io.File
import scala.collection.mutable
import org.apache.solr.common.SolrInputDocument
import java.util.{Calendar, GregorianCalendar, Date}

class FileProcessor(fileInfo : String, file: File, config: mutable.HashMap[String, String]) {
  val info = fileInfo.split("\t")
  val solrDoc : SolrInputDocument = new SolrInputDocument
  solrDoc.addField("id", config("colId") + "." + info(0))
  solrDoc.addField("colId", config("colId"))
  solrDoc.addField("colName", config("colName"))
  solrDoc.addField("componentIdentifier", config("componentIdentifier"))
  solrDoc.addField("filename", info(1))
  //solrDoc.addField("accessFilename", fileHash(info(6).toLowerCase).toString())
  solrDoc.addField("filePath", info(2));
  solrDoc.addField("fileType", info(3));
  solrDoc.addField("fileSize", info(4));
  solrDoc.addField("modDate",convertDateField(info(5)))
  solrDoc.addField("diskId", info(2).split("/")(0));
  solrDoc.addField("md5", info(6));
  solrDoc.addField("fileId", info(0))
  val tikaTool = new TikaTool(file)
  solrDoc.addField("tikaMime", tikaTool.getType)
  solrDoc.addField("language", tikaTool.getLang)
  solrDoc.addField("text", tikaTool.getFullText)
  System.out.println(tikaTool.getNames().getClass)
  //for(name <- tikaTool.getNames()){solrDoc.addField("names", name)}
  //for(org <- tikaTool.getOrgs()){solrDoc.addField("orgs", org)}
  //for(loc <- tikaTool.getLocs()){solrDoc.addField("locs", loc)}
  println(solrDoc)

  def convertDateField(dateString : String) : Date = {
    val cal : Calendar = new GregorianCalendar
    for(dateTime <- (("\\(.*\\)$").r findFirstIn dateString)){
      val date : Array[String]  = dateTime.split(" ")(0).substring(1).split("-");
      val time : Array[String] = dateTime.split(" ")(1).substring(1).split(":");
      cal.set(
        Integer.parseInt(date(0)), Integer.parseInt(date(1)) - 1, Integer.parseInt(date(2)),
        Integer.parseInt(time(0)), Integer.parseInt(time(1)), Integer.parseInt(time(2))
      )
    }
    val date = new Date(cal.getTimeInMillis())
    date
  }
}
