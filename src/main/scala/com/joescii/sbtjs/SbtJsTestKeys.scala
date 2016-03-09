package com.joescii.sbtjs

import sbt.{File, SettingKey, TaskKey}

trait SbtJsTestKeys {
  val jsResources = SettingKey[Seq[File]]("jsResources", "JavaScript directories and files needed for running tests")
  val jsTestColor = SettingKey[Boolean]("jsTestColor", "Enables the use of color in console messages")
  val jsTestBrowsers = SettingKey[Seq[Browser]]("jsTestBrowsers", "The list of browsers to emulate for tests")
  val jsTestTargetDir = SettingKey[File]("jsTestTargetDir", "Target directory for sbt-js-test to work in")

  val jsTest = TaskKey[Unit]("jsTest", "Run JavaScript tests")
  val jsLs = TaskKey[Unit]("jsLs", "Lists all js files configured for testing")
}
