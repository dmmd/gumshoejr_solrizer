import AssemblyKeys._

assemblySettings

name := "GumshoeJr Solrizer"

version := "0.0.1"

scalaVersion := "2.10.2"

jarName in assembly := "eri_solrizer.jar"

mainClass in assembly := Some("org.nypl.mss.Launch")

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
  {
    case PathList("javax", "xml", "XMLConstants") => MergeStrategy.first
  }
}

libraryDependencies ++= Seq(
	"com.typesafe" % "config" % "1.0.0",
	"commons-codec" % "commons-codec" % "1.6",
	"commons-io" % "commons-io" % "2.4",
	"commons-logging" % "commons-logging" % "1.1.1",
	"org.apache.httpcomponents" % "httpclient" % "4.2.5",
	"org.apache.httpcomponents" % "httpcore" % "4.1.4",
	"org.apache.httpcomponents" % "httpmime" % "4.2.5",
	"org.apache.opennlp" % "opennlp-maxent" % "3.0.3",
	"org.apache.opennlp" % "opennlp-tools" % "1.5.3",
	"org.apache.opennlp" % "opennlp-uima" % "1.5.3",
	"org.apache.tika" % "tika-app" % "1.3",
	"org.apache.solr" % "solr-solrj" % "4.0.0",
	"org.codehaus.woodstox" % "wstx-asl" % "3.2.7",
	"org.slf4j" % "log4j-over-slf4j" % "1.6.4",
	"org.slf4j" % "slf4j-api" % "1.6.4",
	"org.slf4j" % "slf4j-jdk14" % "1.6.4"
)
