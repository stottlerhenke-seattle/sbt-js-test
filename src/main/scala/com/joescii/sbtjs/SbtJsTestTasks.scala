package com.joescii.sbtjs

import sbt.Keys._

object SbtJsTestTasks {
  val jsTestTask = (streams).map { s =>
    s.log.info("Running JavaScript tests...")
  }
}
