lazy val commonSettings = Seq(
  organization := "com.example",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.11.7",
  resolvers ++= Seq(
    "snapshots"         at "https://oss.sonatype.org/content/repositories/snapshots",
    "releases"          at "https://oss.sonatype.org/content/repositories/releases"
  )
) ++ seq(sbtJsTestSettings : _*)

lazy val allPass = (project in file("allPass")).
  settings(commonSettings: _*).
  settings(
    name := "allPass",
    jsResources <<= (sourceDirectory in Compile, sourceDirectory in Test) { (main, test) => Seq(
      test / "js"
    )}
  )
  
lazy val oneFail = (project in file("oneFail")).
  settings(commonSettings: _*).
  settings(
    name := "oneFail",
    jsResources <<= (sourceDirectory in Compile, sourceDirectory in Test) { (main, test) => Seq(
      test / "js"
    )}
  )

lazy val lsJs = (project in file("lsJs")).
  settings(commonSettings: _*).
  settings(
    name := "lsJs",
    jsResources <<= (sourceDirectory in Compile) { main =>
      Seq(main / "js")
    }
  )

