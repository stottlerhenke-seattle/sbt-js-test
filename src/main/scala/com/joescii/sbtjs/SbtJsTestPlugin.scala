package com.joescii.sbtjs

import sbt._
import Keys._

object SbtJsTestPlugin extends AutoPlugin with SbtJsTestKeys {
  import SbtJsTestTasks._

  override def trigger = allRequirements
  override lazy val projectSettings = sbtJsTestSettings

  lazy val sbtJsTestSettings:Seq[Def.Setting[_]] = List(
    jsResources <<= (sourceDirectory in Compile, unmanagedResourceDirectories in Compile) { (main, rsrc) =>
      ((main / "js") +: rsrc).flatMap(r => (r ** "*.js").get)
    },
    watchSources <++= jsResources.map(identity),

    jsTestResources <<= (sourceDirectory in Test, unmanagedResourceDirectories in Test) { (test, rsrc) =>
      ((test / "js") +: rsrc).flatMap(r => (r ** "*.js").get)
    },
    watchSources <++= jsTestResources.map(identity),

    jsTestColor := true,
    jsTestBrowsers := Seq(Browsers.Chrome),
    jsFrameworks := Seq(Frameworks.Jasmine2),
    jsTestTargetDir <<= (target in Test) (_ / "sbt-js-test"),

    jsTest <<= jsTestTask,
    jsTestOnly <<= jsTestOnlyTask,
    jsLs <<= jsLsTask
  )
}
