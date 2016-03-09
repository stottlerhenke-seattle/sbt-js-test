import com.joescii.sbtjs._

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

lazy val jsLs = (project in file("jsLs")).
  settings(commonSettings: _*).
  settings(
    name := "jsLs",
    jsResources <<= (sourceDirectory in Compile) { main => Seq(
      main / "js"
    )}
  )

lazy val angular = (project in file("angular")).
  settings(commonSettings: _*).
  settings(
    name := "angular",
    jsResources <<= (sourceDirectory in Compile, sourceDirectory in Test) { (main, test) => Seq(
      main / "js" / "angular" / "angular.js",
      main / "js" / "angular" / "angular-mocks.js",
      main / "js" / "sample-app.js",
      test / "js"
    )}
  )

lazy val allBrowsers = (project in file("allBrowsers")).
  settings(commonSettings: _*).
  settings(
    name := "allBrowsers",
    jsResources <<= (sourceDirectory in Compile, sourceDirectory in Test) { (main, test) => Seq(
      test / "js"
    )},
    jsTestBrowsers := Seq(Firefox38, InternetExplorer11, Chrome)
  )

lazy val nonExistentDir = (project in file("nonExistentDir")).
  settings(commonSettings: _*).
  settings(
    name := "nonExistentDir",
    jsResources <<= (sourceDirectory in Compile, sourceDirectory in Test) { (main, test) => Seq(
      main / "js",
      test / "js"
    )}
  )

lazy val testOnly = (project in file("testOnly")).
  settings(commonSettings: _*).
  settings(
    name := "testOnly",
    jsResources <<= (sourceDirectory in Compile) { main => Seq(
      main / "js"
    )},
    jsTestResources <<= (sourceDirectory in Test) { test => Seq(
      test / "js"
    )}
  )

