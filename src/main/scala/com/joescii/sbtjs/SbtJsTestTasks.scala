package com.joescii.sbtjs

import sbt.File
import sbt.Keys._

object SbtJsTestTasks extends SbtJsTestKeys {
  val jsTestTask = (streams).map { s =>
    s.log.info("Running JavaScript tests...")
  }

  private [this] def lsR(f:File):List[File] =
    if(!f.isDirectory) List(f)
    else f.listFiles().toList.flatMap(lsR)

  val lsJsTask = (streams, jsResources).map { (s, rsrcs) =>
    for {
      maybeDir <- rsrcs
      leaf <- lsR(maybeDir)
    } { s.log.info(leaf.getCanonicalPath) }
  }
}
