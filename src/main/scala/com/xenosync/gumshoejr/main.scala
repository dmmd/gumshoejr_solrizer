package com.xenosync.gumshoejr

import org.apache.solr.client.solrj.impl.HttpSolrServer;

object GumshoeJr{
	def main(args : Array[String]) = {
		println("GumshoeJR Solrizer v.0.0.0")
		val index = "1987.tsv"
		val files = "files/1987"
		val ip = new IndexParser(index, files)
	}
}