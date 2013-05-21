package com.xenosync.gumshoejr

import java.io.File
import java.io.FileInputStream
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FilenameUtils
import org.apache.solr.client.solrj.impl.HttpSolrServer
import scala.io.Source
import scala.collection.mutable.HashMap

class IndexParser(val pack: Package){
	var inventoryState = false
	val componentInfo : HashMap[String, String] = new HashMap[String, String]
	val fileHash : HashMap[String, String] = new HashMap[String, String]
	val accessMap : HashMap[String, File] = new HashMap[String, File] 
	val server : HttpSolrServer = new HttpSolrServer("http://localhost:8983/solr")
		
	mapFiles
	parseInventory

	def parseInventory() = {
		var count = 0;
		for(line <- Source.fromFile(pack.getIndex.getAbsolutePath).getLines()){
			line.split("\t")(0) match {
				case "SERIESINFO" => inventoryState = false
				case "INVENTORY" => 
					inventoryState = true
					if(componentInfo("access") == "true"){
						getAccessMap()
					}
				case _ => 
					if(inventoryState == true){
						val fp : FileProcessor  = new FileProcessor(line.split("\t"), componentInfo, fileHash, pack.getFiles.getAbsolutePath, accessMap)
						count += 1
						println(count + ": " + fp.solrDoc.getField("filename"))
						server.add(fp.solrDoc)
						server.commit
					} else {
						addSeriesInfo(line.split("\t"))
					}
			}
		}
		server.commit
		server.optimize
	}

	def processInventory(line : Array[String]) = {}
	
	def addSeriesInfo(line : Array[String]) = {componentInfo += (line(0) -> line(1))}
	
	def mapFiles() = {
		for(file <- pack.getFiles.listFiles){
			if(file.isFile)
				fileHash += (DigestUtils.md5Hex(new FileInputStream(file)) -> file.getName)
		}
	}
	
	def getAccessMap() = {	
		val accessDir = new File(pack.getFiles.getAbsolutePath + File.separator + "access")
		for(file <- accessDir.listFiles){
			accessMap += (FilenameUtils.getBaseName(file.getName) -> file)
		}
	}
	
	def printMap(map : HashMap[String, String]) = {
		for((k,v) <- map){
			println(k + "\t" + v)
		}
	}
}