package com.xenosync.gumshoejr

import java.io.File
import org.apache.commons.io.FilenameUtils

class Package(packageLoc: String){
	val file: File = new File(packageLoc)
	val isValid = checkValid
	
	def checkValid() : Boolean = {
		val files = file.listFiles
		if(file.exists && file.isDirectory && files.size == 2 && hasIndex(files) && hasFilesDir(files)) 
			true
		else 
			false
	}
	
	def hasIndex(files: Array[File]) : Boolean = {
		var valid = false
		for(file <- files){
			if(FilenameUtils.getExtension(file.getName) == "tsv")
				valid = true
		}
		valid
	}
	
	def hasFilesDir(files: Array[File]) : Boolean = {
		var valid = false
		for(file <- files){
			if(file.getName == "files" && file.isDirectory && file.listFiles.size > 1)
				valid = true
		}
		valid
	}
	
	def getIndex(): File ={
		var indexFile: File = null;
		for(f <- file.listFiles){
			if(FilenameUtils.getExtension(f.getName) == "tsv")
				indexFile = f.getAbsoluteFile
		}
		indexFile
	}
	
	def getFiles(): File ={
		var files: File = null
		for(f <- file.listFiles){
			if(f.getName == "files")
				files = f.getAbsoluteFile
		}
		files
	}
}