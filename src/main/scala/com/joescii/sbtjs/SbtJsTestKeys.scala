package com.joescii.sbtjs

import sbt.{InputKey, File, SettingKey, TaskKey}

trait SbtJsTestKeys {
  val jsResources = SettingKey[Seq[File]]("jsResources", "JavaScript directories and files needed for running your tests, including mocks")
  val jsTestResources = SettingKey[Seq[File]]("jsTestResources", "JavaScript directories and files containing tests to run")
  val jsTestColor = SettingKey[Boolean]("jsTestColor", "Enables the use of color in console messages")
  val jsTestBrowsers = SettingKey[Seq[Browser]]("jsTestBrowsers", "The list of browsers to emulate for tests")
  val jsFrameworks = SettingKey[Seq[Framework]]("jsFramekworks", "The list of JavaScript test frameworks to utilize")
  val jsTestTargetDir = SettingKey[File]("jsTestTargetDir", "Target directory for sbt-js-test to work in")
  val jsAsyncSupport = SettingKey[Boolean]("jsAsyncSupport", "Enables the wait for asynchronously-loaded js assets before running the tests")
  val jsAsyncSupportTimeout = SettingKey[Long]("jsAsyncSupportTimeout", "Amount of time the plugin should wait for asynchronously-loaded js assets before continue")

  val jsTest = TaskKey[Unit]("jsTest", "Run JavaScript tests")
  val jsTestOnly = InputKey[Unit]("jsTestOnly", "Run only one JavaScript test file from jsTestResources")
  val jsLs = TaskKey[Unit]("jsLs", "Lists all js files configured for testing")
}
