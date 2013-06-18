package org.nypl.mss

import com.typesafe.config._

object Launch{
  def main(args: Array[String]) = {
    println("GumshoeJr Solrizer v.0.0.1")
	  println("You passed: " + args(0))
	  val gs: GumshoeSolrizer = new GumshoeSolrizer(args(0))
  }
}