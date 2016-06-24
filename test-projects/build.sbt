lazy val commonSettings = Seq(
  organization := "com.example",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.11.7",
  resolvers ++= Seq(
    "snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    "releases" at "https://oss.sonatype.org/content/repositories/releases"
  )
)

lazy val allPass = (project in file("allPass")).
  settings(commonSettings: _*).
  settings(
    name := "allPass"
  )
  
lazy val oneFailOnePending = (project in file("oneFailOnePending")).
  settings(commonSettings: _*).
  settings(
    name := "oneFailOnePending",
    jsTestColor := false
  )

lazy val jsLs = (project in file("jsLs")).
  settings(commonSettings: _*).
  settings(
    name := "jsLs"
  )

lazy val angular = (project in file("angular")).
  settings(commonSettings: _*).
  settings(
    name := "angular",
    jsResources := {
      val main = (sourceDirectory in Compile).value
      Seq(
        main / "js" / "angular" / "angular.js",
        main / "js" / "angular" / "angular-mocks.js",
        main / "js" / "sample-app.js"
    )}
  )

import JsTestBrowsers._
lazy val allBrowsers = (project in file("allBrowsers")).
  settings(commonSettings: _*).
  settings(
    name := "allBrowsers",
    jsTestBrowsers := Seq(Firefox38, InternetExplorer11, Chrome)
  )

lazy val nonExistentDir = (project in file("nonExistentDir")).
  settings(commonSettings: _*).
  settings(
    name := "nonExistentDir"
  )

lazy val testOnly = (project in file("testOnly")).
  settings(commonSettings: _*).
  settings(
    name := "testOnly"
  )

lazy val requireJs = (project in file("requireJs")).
  settings(commonSettings: _*).
  settings(
    name := "requireJs",
    jsAsyncWait := true,

    jsResources := {
      val main = (sourceDirectory in Compile).value
      Seq(
        main / "requirejs" / "require.js"
      )
    },

    jsTestResources := {
      val test = (sourceDirectory in Test).value
      Seq(test / "js" / "test-main.js")
    }
  )

lazy val requireJsTimeout = (project in file("requireJsTimeout")).
  settings(commonSettings: _*).
  settings(
    name := "requireJsTimeout",
    jsAsyncWait := true,
    jsAsyncWaitTimeout := Some(2500),

    jsResources := {
      val main = (sourceDirectory in Compile).value
      Seq(
        main / "requirejs" / "require.js"
      )
    },

    jsTestResources := {
      val test = (sourceDirectory in Test).value
      Seq(test / "js" / "test-main.js")
    }
  )


lazy val bigSuite = (project in file("bigSuite")).
  settings(commonSettings: _*).
  settings(
    name := "bigSuite"
  )
