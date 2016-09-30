import com.typesafe.sbt.packager.docker._

name := "countries-ms"

organization := "cirruslogicweb"

version := "1.0"

scalaVersion := "2.11.8"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

enablePlugins(DockerPlugin)
enablePlugins(JavaAppPackaging)

dockerRepository := Some("quay.io/cirruslogicweb")

/*
dockerCommands := dockerCommands.value.filterNot {
  case Cmd("ADD", _) => true
  case _ => false
}
*/

libraryDependencies ++= {
  val akkaV         = "2.4.10"
  val scalaTestV    = "3.0.0"

  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-http-experimental" % akkaV,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaV,
    "com.typesafe.akka" %% "akka-http-xml-experimental" % akkaV,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaV,
    "org.scalatest"     %% "scalatest" % scalaTestV % "test"
  )
}

