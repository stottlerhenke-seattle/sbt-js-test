package com.joescii.sbtjs

import implicits._
import sbt.{Def, Plugin}
import sbt.Keys._

object SbtJsTestPlugin extends Plugin with SbtJsTestKeys {
  import SbtJsTestTasks._

  val sbtJsTestSettings:Seq[Def.Setting[_]] = List(
    jsResources := Seq.empty,
    jsTestTargetDir <<= (target in sbt.Test) (_ / "sbt-js-test"),
    jsResourceTargetDir <<= (jsTestTargetDir) (_ / "assets"),
    consoleHtml <<= (jsTestTargetDir) (_ / "console.html"),

    jsTest <<= jsTestTask,
    jsTest <<= jsTest dependsOn writeConsoleHtml,
    jsTest <<= jsTest dependsOn writeWebjars,
    lsJs <<= lsJsTask,
    writeConsoleHtml <<= writeConsoleHtmlTask,
    writeWebjars <<= writeWebjarsTask
  )
}
