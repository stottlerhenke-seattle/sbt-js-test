package com.joescii.sbtjs

import sbt._
import Keys._


sealed trait Framework // Mostly placeholder for now
sealed trait Browser

object SbtJsTestPlugin extends AutoPlugin with SbtJsTestKeys {
  import SbtJsTestTasks._

  object autoImport extends SbtJsTestKeys {
    object JsTestBrowsers {
      case object Firefox38 extends Browser
//      case object InternetExplorer8 extends Browser
      case object InternetExplorer11 extends Browser
      case object Chrome extends Browser
//      case object Edge extends Browser
    }

    object JsTestFrameworks {
      case object Jasmine2 extends Framework
    }
  }

  override def trigger = allRequirements
  override lazy val projectSettings = sbtJsTestSettings

  lazy val sbtJsTestSettings:Seq[Def.Setting[_]] = List(
    jsResources <<= (sourceDirectory in Compile, unmanagedResourceDirectories in Compile) { (main, rsrc) =>
      ((main / "js") +: (main / "javascript") +: rsrc).flatMap(r => (r ** "*.js").get)
    },
    watchSources <++= jsResources.map(identity),

    jsTestResources <<= (sourceDirectory in Test, unmanagedResourceDirectories in Test) { (test, rsrc) =>
      ((test / "js") +: (test / "javascript") +: rsrc).flatMap(r => (r ** "*.js").get)
    },
    watchSources <++= jsTestResources.map(identity),

    jsTestColor := true,
    jsTestBrowsers := Seq(autoImport.JsTestBrowsers.Chrome),
    jsFrameworks := Seq(autoImport.JsTestFrameworks.Jasmine2),
    jsTestTargetDir <<= (target in Test) (_ / "sbt-js-test"),
    jsAsyncWait := false,
    jsAsyncWaitTimeout := None,

    jsTest <<= jsTestTask,
    jsTestOnly <<= jsTestOnlyTask,
    jsLs <<= jsLsTask
  )
}
