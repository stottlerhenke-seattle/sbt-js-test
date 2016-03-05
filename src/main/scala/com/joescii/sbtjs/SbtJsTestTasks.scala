package com.joescii.sbtjs

import implicits._

import sbt.{IO, File}
import sbt.Keys._

object SbtJsTestTasks extends SbtJsTestKeys {
  val jsTestTask = (streams).map { s =>
    s.log.info("Running JavaScript tests...")
  }

  private [this] def lsR(fs:Seq[File]):List[File] =
    fs.flatMap(lsR).toList

  private [this] def lsR(f:File):List[File] =
    if(!f.isDirectory) List(f)
    else f.listFiles().toList.flatMap(lsR)

  val lsJsTask = (streams, jsResources).map { (s, rsrcs) =>
    lsR(rsrcs).foreach(f => s.log.info(f.getCanonicalPath))
  }

  private [this] def htmlFor(js:List[File]):String = {
    val doctype = "<!DOCTYPE html>"
    val scripts = js map ( f => <script type="application/javascript" src={f.toURI.toASCIIString}></script> )
    val html =
      <html>
        <head>
          {scripts}
        </head>
        <body>
        </body>
      </html>

    doctype + "\n" + html.toString
  }

  val writeHtmlTask = (streams, jsResources, target).map { (s, rsrcs, target) =>
    val f = target / "sbt-js-test" / "console.html"
    s.log.info(s"Generating ${f.getCanonicalPath}...")
    IO.write(f, htmlFor(lsR(rsrcs)))
    f
  }
}
