package com.joescii.sbtjs

import com.gargoylesoftware.htmlunit.{BrowserVersion, WebClient}
import sbt.{IO, File}
import sbt.Keys._

object SbtJsTestTasks extends SbtJsTestKeys {
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

  val writeConsoleHtmlTask = (streams, jsResources, consoleHtml).map { (s, rsrcs, html) =>
    s.log.info(s"Generating ${html.getCanonicalPath}...")
    IO.write(html, htmlFor(lsR(rsrcs)))
    html
  }

  private [this] def runJs(html:File):Unit = {
    val client = new WebClient(BrowserVersion.CHROME)
    val options = client.getOptions()
    options.setHomePage(WebClient.URL_ABOUT_BLANK.toString())
    options.setJavaScriptEnabled(true)

    client.getPage(html.toURI.toURL)
  }

  val jsTestTask = (streams, consoleHtml).map { (s, html) =>
    s.log.info("Running JavaScript tests...")
    LogAdapterScala.logger = s.log
    runJs(html)
  }
}
