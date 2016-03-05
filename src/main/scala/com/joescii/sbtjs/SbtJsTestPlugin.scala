package com.joescii.sbtjs

import implicits._
import sbt.{Def, Plugin}
import sbt.Keys._

object SbtJsTestPlugin extends Plugin with SbtJsTestKeys {
  import SbtJsTestTasks._

  val sbtJsTestSettings:Seq[Def.Setting[_]] = List(
    lsJs <<= lsJsTask,
    consoleHtml <<= (target in sbt.Test) (_ / "sbt-js-test" / "console.html"),
    writeConsoleHtml <<= writeConsoleHtmlTask,
    jsTest <<= jsTestTask,
    jsTest <<= jsTest dependsOn writeConsoleHtml,
    jsResources := Seq.empty
  )
}
