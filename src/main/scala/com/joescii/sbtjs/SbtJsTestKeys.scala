package com.joescii.sbtjs

import sbt.{File, SettingKey, TaskKey}

trait SbtJsTestKeys {
  val jsResources = SettingKey[Seq[File]]("jsResources", "JavaScript directories and files needed for running tests")
  val jsTestTargetDir = SettingKey[File]("jsTestTargetDir", "Target directory for sbt-js-test to work in")
  val jsResourceTargetDir = SettingKey[File]("jsResourceTargetDir", "Target directory for writing JavaScript assets from the classpath")
  val consoleHtml = SettingKey[File]("consoleHtml", "Location for writing console.html")

  val jsTest = TaskKey[Unit]("testJs", "Run JavaScript tests")
  val lsJs = TaskKey[Unit]("lsJs", "Lists all js files configured for testing")
  val writeJsAssets = TaskKey[Seq[File]]("writeJsAssets", "Writes JavaScript assets to target for HtmlUnit to consume")
  val writeConsoleHtml = TaskKey[File]("writeConsoleHtml", "Writes an HTML file containing all of the JavaScript resources for tests")
}
