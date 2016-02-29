package com.joescii.sbtjs

import sbt.{Project, Plugin}

object SbtJsTestPlugin extends Plugin {
  import SbtJsTestKeys._
  import SbtJsTestTasks._

  val sbtJsTestSettings:Seq[Project.Setting[_]] = List(
    jsTest <<= jsTestTask
  )
}
