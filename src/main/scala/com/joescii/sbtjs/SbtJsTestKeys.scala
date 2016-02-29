package com.joescii.sbtjs

import sbt.TaskKey

object SbtJsTestKeys {
  val jsTest = TaskKey[Unit]("testJs", "Run JavaScript tests")
}
