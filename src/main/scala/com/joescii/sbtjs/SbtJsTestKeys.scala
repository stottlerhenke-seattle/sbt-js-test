package com.joescii.sbtjs

import sbt.{PathFinder, SettingKey, TaskKey}

trait SbtJsTestKeys {
  val jsTest = TaskKey[Unit]("testJs", "Run JavaScript tests")
  val jsResources = SettingKey[PathFinder]("jsResources", "JavaScript resources needed for running tests")
  val lsJs = TaskKey[Unit]("lsJs", "Lists all js files configured for testing")
}
