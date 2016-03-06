package com.joescii.sbtjs

import java.io.{InputStreamReader, BufferedReader}
import java.util.regex.Pattern

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

  private [this] def locator(lib:String) = new WebJarAssetLocator(
    WebJarAssetLocator.getFullPathIndex(Pattern.compile(".*"+Pattern.quote(lib)+".*"), this.getClass.getClassLoader)
  )
  private [this] val jasmineLocator = locator("jasmine")
  private [this] def read(classpath:String):String = {
    val url = this.getClass.getClassLoader.getResource(classpath)
    val r = new BufferedReader(new InputStreamReader(url.openStream()))
    Iterator.continually(r.readLine()).takeWhile(_ != null).mkString("\n")
  }
  private [this] def cat(classpath:String) = new {
    private [this] lazy val contents = read(classpath)
    def > (f:File):Unit = IO.write(f, contents)
  }

  def jasmine(target:File) = target / "jasmine" / "jasmine.js"
  def jasmineHtmlUnitBoot(target:File) = target / "jasmine" / "htmlunit_boot.js"
  def jasmineConsole(target:File) = target / "jasmine" / "console.js"
  def jasmineAssets(target:File):Seq[File] = Seq(
    jasmine(target),
    jasmineConsole(target),
    jasmineHtmlUnitBoot(target)
  )

  val writeJsAssetsTask = (streams, jsResourceTargetDir).map { (s, target) =>
    s.log.info("Writing js assets...")

    cat(jasmineLocator.getFullPath("jasmine.js")) > jasmine(target)
    cat(jasmineLocator.getFullPath("console.js")) > jasmineConsole(target)
    cat("js/htmlunit_jasmine_boot.js") > jasmineHtmlUnitBoot(target)

    jasmineAssets(target)
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

  val writeConsoleHtmlTask = (streams, jsResources, consoleHtml, jsResourceTargetDir).map { (s, rsrcs, html, target) =>
    s.log.info(s"Generating ${html.getCanonicalPath}...")
    val allJs = jasmineAssets(target).toList ++ lsR(rsrcs)
    IO.write(html, htmlFor(allJs))
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
