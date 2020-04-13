name := """covid19-tracker"""
organization := "education"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
  ehcache,
  ws,
  filters,
  guice,
  "net.codingwell" %% "scala-guice" % "4.1.0",
  "com.typesafe.play" %% "play-slick" % "3.0.3",
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.0",
  "mysql" % "mysql-connector-java" % "5.1.35",
  "com.cloudphysics" % "jerkson_2.10" % "0.6.3",
  "com.typesafe.play" % "play-json-joda_2.11" % "2.6.0-RC1",
  specs2 % Test,
  evolutions
)