package com.joescii.sbtjs

import sbt.{PathFinder, Project, Plugin}

object SbtJsTestPlugin extends Plugin with SbtJsTestKeys {
  import SbtJsTestTasks._

  val sbtJsTestSettings:Seq[Project.Setting[_]] = List(
    jsTest <<= jsTestTask,
    lsJs <<= lsJsTask,
    jsResources := Seq.empty
  )
}
