package com.joescii.sbtjs

import sbt.{File, SettingKey, TaskKey}

trait SbtJsTestKeys {
  val jsTest = TaskKey[Unit]("testJs", "Run JavaScript tests")
  val jsResources = SettingKey[Seq[File]]("jsResources", "JavaScript directories and files needed for running tests")
  val lsJs = TaskKey[Unit]("lsJs", "Lists all js files configured for testing")
  val writeConsoleHtml = TaskKey[File]("writeConsoleHtml", "Writes an HTML file containing all of the JavaScript resources for tests")
}
