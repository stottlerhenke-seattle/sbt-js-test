package com.joescii.sbtjs

import java.io.{InputStreamReader, BufferedReader}

import implicits._

import com.gargoylesoftware.htmlunit.{BrowserVersion, WebClient}
import org.webjars.WebJarAssetLocator
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

  private [this] val locator = new WebJarAssetLocator()
  private [this] def read(classpath:String):String = {
    val url = this.getClass.getClassLoader.getResource(classpath)
    val r = new BufferedReader(new InputStreamReader(url.openStream()))
    Iterator.continually(r.readLine()).takeWhile(_ != null).mkString("\n")
  }
  private [this] def cat(webjarAsset:String) = new {
    private [this] lazy val contents = read(locator.getFullPath(webjarAsset))
    def > (f:File):Unit = IO.write(f, contents)
  }

  val writeWebjarsTask = (streams, jsResourceTargetDir).map { (s, target) =>
    s.log.info("Writing webjar assets...")
    cat("jasmine.js") > target / "jasmine.js"

    Seq(target / "jasmine.js")
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
