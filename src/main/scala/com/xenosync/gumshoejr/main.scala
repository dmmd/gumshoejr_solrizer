package com.xenosync.gumshoejr

import org.apache.solr.client.solrj.impl.HttpSolrServer;

class GumshoeJr{
	println("GumshoeJR Solrizer v.0.0.0")
}

object GumshoeJr{
	def main(args : Array[String]) = {
		
		val index = "Packages/M6196_ER_0001/M6196_ER_0001.tsv"
		val files = "Packages/M6196_ER_0001/files"
		val ip = new IndexParser(index, files)
	}
}