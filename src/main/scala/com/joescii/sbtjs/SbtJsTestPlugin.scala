package com.joescii.sbtjs

import sbt.{Def, Plugin}

object SbtJsTestPlugin extends Plugin with SbtJsTestKeys {
  import SbtJsTestTasks._

  val sbtJsTestSettings:Seq[Def.Setting[_]] = List(
    jsTest <<= jsTestTask,
    lsJs <<= lsJsTask,
    writeHtml <<= writeHtmlTask,
    jsResources := Seq.empty
  )
}
