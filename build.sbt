sbtPlugin := true

name := "sbt-js-test"

organization := "com.joescii"

homepage := Some(url("https://github.com/joescii/sbt-js-test"))

version := "0.1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "net.sourceforge.htmlunit" % "htmlunit"  % "2.19" % "compile"
)

// don't bother publishing javadoc
publishArtifact in (Compile, packageDoc) := false

sbtVersion in Global := {
  scalaBinaryVersion.value match {
    case "2.10" => "0.13.5"
    case "2.9.2" => "0.12.4"
  }
}

scalaVersion in Global := "2.10.5"

crossScalaVersions := Seq("2.10.5")

scalacOptions ++= Seq("-unchecked", "-deprecation")

//publishMavenStyle := false

//bintrayPublishSettings

//repository in bintray := "sbt-plugins"

licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.html"))

//bintrayOrganization in bintray := None
