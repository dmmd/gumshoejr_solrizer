package com.xenosync.gumshoejr

import java.io.File
import java.util.Date
import java.util.Calendar
import java.util.GregorianCalendar
import org.apache.solr.common.SolrInputDocument
import org.apache.commons.io.FilenameUtils
import scala.collection.mutable.HashMap
import scala.collection.JavaConversions._

class FileProcessor(fileInfo : Array[String], seriesInfo : HashMap[String, String], 
	fileHash : HashMap[String, String], 
	fileDir : String, accessMap : HashMap[String, File]){
	val solrDoc : SolrInputDocument = new SolrInputDocument

	//map componentInfo to solrDoc
	solrDoc.addField("id", seriesInfo("cid") + "." + fileInfo(0))
	solrDoc.addField("colId", seriesInfo("cid"))
	solrDoc.addField("colName", seriesInfo("cname"))
	solrDoc.addField("componentIdentifier", seriesInfo("compId"))
	solrDoc.addField("componentTitle", seriesInfo("component"))
	solrDoc.addField("parentComponentTitle", seriesInfo("parentComponent"))
	
	//map fileInfo to solrDoc
	solrDoc.addField("filename", fileInfo(1))
	solrDoc.addField("accessFilename", fileHash(fileInfo(6).toLowerCase).toString())
	solrDoc.addField("filePath", fileInfo(2));
    solrDoc.addField("fileType", fileInfo(3));
    solrDoc.addField("fileSize", fileInfo(4));
	solrDoc.addField("modDate",convertDateField(fileInfo(5)))
	solrDoc.addField("diskId", fileInfo(2).split("/")(0));
	solrDoc.addField("md5", fileInfo(6));
	solrDoc.addField("fileId", fileInfo(0))
	
	//Tikafy
	tikafy(getAccessFile(fileHash(fileInfo(6).toLowerCase).toString()))
	
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
	
	def tikafy(file : File) = {
		val tikaTool = new TikaTool(file)
		solrDoc.addField("tikaMime", tikaTool.getType)
		solrDoc.addField("language", tikaTool.getLang)
		solrDoc.addField("text", tikaTool.getFullText)
		for(name <- tikaTool.getNames()){solrDoc.addField("names", name)}
		for(org <- tikaTool.getOrgs()){solrDoc.addField("orgs", org)}
		for(loc <- tikaTool.getLocs()){solrDoc.addField("locs", loc)}
	}
	
	def getAccessFile(fileString : String) : File = {
		if(seriesInfo("access") == "true"){
			accessMap(fileString)
		} else {
			val file = new File(fileDir + File.separator + fileString)
			file
		}
	}
		
	override def toString() : String = {
		solrDoc.toString
	}
}