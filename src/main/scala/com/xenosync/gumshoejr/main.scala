package com.xenosync.gumshoejr

object GumshoeJr{
	def main(args : Array[String]) = {
		println("GumshoeJR Solrizer v.0.0.0")
		val pack : Package = new Package("Packages/M18867/M18867_ER_0002")
		if(!pack.isValid){
			println("The specified package is not valid")
			System.exit(1)
		}
			
		val ip = new IndexParser(pack)
	}
}