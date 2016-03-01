package com.joescii.sbtjs

import sbt.Keys._

object SbtJsTestTasks extends SbtJsTestKeys {
  val jsTestTask = (streams).map { s =>
    s.log.info("Running JavaScript tests...")
  }

  val lsJsTask = (streams, jsResources).map { (s, rsrc) =>
    rsrc.get.foreach { f =>
      s.log.info(f.getCanonicalPath)
    }
  }
}
