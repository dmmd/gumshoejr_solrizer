package com.xenosync.gumshoejr

import java.io.File
import java.io.FileInputStream
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FilenameUtils
import org.apache.solr.client.solrj.impl.HttpSolrServer
import scala.io.Source
import scala.collection.mutable.HashMap

class IndexParser(val indexLoc : String, val fileLoc : String){
	var inventoryState = false
	val componentInfo : HashMap[String, String] = new HashMap[String, String]
	val fileHash : HashMap[String, String] = new HashMap[String, String]
	val accessMap : HashMap[String, File] = new HashMap[String, File] 
	val server : HttpSolrServer = new HttpSolrServer("http://localhost:8983/solr")
	
	if(checkIndex && checkFiles){
		mapFiles
		parseInventory
	}
	
	def checkIndex() = {
		val f : File = new File(indexLoc)
		if(f.exists && f.canRead && f.isFile)
			true
		else
			false
	}

	def checkFiles() = {
		val f : File = new File(fileLoc)
		if(f.exists && f.canRead && f.isDirectory)
			true
		else
			false
	}
	
	def parseInventory() = {
		var count = 0;
		for(line <- Source.fromFile(indexLoc).getLines()){
			line.split("\t")(0) match {
				case "SERIESINFO" => inventoryState = false
				case "INVENTORY" => inventoryState = true
					if(componentInfo("access") == "true")
						getAccessMap()
				case _ => 
					if(inventoryState == true){
						val fp : FileProcessor  = new FileProcessor(line.split("\t"), componentInfo, fileHash, fileLoc, accessMap)
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
		for(file <- new File(fileLoc).listFiles){
			if(file.isFile)
				fileHash += (DigestUtils.md5Hex(new FileInputStream(file)) -> file.getName)
		}
	}
	
	def getAccessMap() = {	
		val accessDir = new File(fileLoc + File.separator + "access")
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