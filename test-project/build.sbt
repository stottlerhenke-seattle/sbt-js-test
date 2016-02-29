name := "sbt-js-test-demo"

organization := "com.joescii"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.7"

resolvers ++= Seq(
  "snapshots"         at "https://oss.sonatype.org/content/repositories/snapshots",
  "releases"          at "https://oss.sonatype.org/content/repositories/releases"
)

