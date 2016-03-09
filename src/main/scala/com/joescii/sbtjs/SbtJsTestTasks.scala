package com.joescii.sbtjs

import java.io.{InputStreamReader, BufferedReader}
import java.util.regex.Pattern

import com.gargoylesoftware.htmlunit.html.HtmlPage
import implicits._

import com.gargoylesoftware.htmlunit.WebClient
import net.sourceforge.htmlunit.corejs.javascript. { ScriptableObject, Function => JsFunction }
import org.webjars.WebJarAssetLocator
import sbt.{TestsFailedException, IO, File}
import sbt.Keys._

object SbtJsTestTasks extends SbtJsTestKeys {
  private [this] def lsR(fs:Seq[File]):List[File] =
    fs.flatMap(lsR).toList

  private [this] def lsR(f:File):List[File] =
    if(!f.exists()) List()
    else if(!f.isDirectory) List(f)
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
  private [this] def cat(s:String) = new {
    def > (f:File):Unit = IO.write(f, s)
  }

  def sbtJsTest(target:File) = target / "sbtJsTest.js"
  def jasmine(target:File) = target / "jasmine" / "jasmine.js"
  def jasmineHtmlUnitBoot(target:File) = target / "jasmine" / "htmlunit_boot.js"
  def jasmineConsole(target:File) = target / "jasmine" / "console.js"
  def jasmineAssets(target:File):Seq[File] = Seq(
    sbtJsTest(target),
    jasmine(target),
    jasmineConsole(target),
    jasmineHtmlUnitBoot(target)
  )

  val writeJsAssetsTask = (streams, jsResourceTargetDir, jsTestColor).map { (s, target, color) =>
    s.log.info("Writing js assets...")

    val colorJs = s"""
        |window.sbtJsTest = window.sbtJsTest || {};
        |window.sbtJsTest.showColors = $color;
      """.stripMargin

    cat(colorJs) > sbtJsTest(target)
    cat(read(jasmineLocator.getFullPath("jasmine.js"))) > jasmine(target)
    cat(read(jasmineLocator.getFullPath("console.js"))) > jasmineConsole(target)
    cat(read("js/htmlunit_jasmine_boot.js")) > jasmineHtmlUnitBoot(target)

    jasmineAssets(target)
  }

  private [this] def htmlFor(js:List[File]):String = {
    val doctype = "<!DOCTYPE html>"
    val scripts = js map ( f => <script type="application/javascript" language="javascript" src={f.toURI.toASCIIString}></script> )
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

  private [this] def runJs(html:File, browser: Browser):Boolean = {
    val client = new WebClient(BrowserVersion(browser))
    val options = client.getOptions()
    options.setHomePage(WebClient.URL_ABOUT_BLANK.toString())
    options.setJavaScriptEnabled(true)

    client.getPage(html.toURI.toURL)
    val window = client.getCurrentWindow().getTopWindow
    val page:HtmlPage = window.getEnclosedPage().asInstanceOf[HtmlPage] // asInstanceOf because ... java...

    val js = """
        |jasmine.getEnv().execute();
        |return window.sbtJsTest.complete && window.sbtJsTest.allPassed;
      """.stripMargin
    val toRun = "function() {\n"+js+"\n};"
    val result = page.executeJavaScript(toRun)
    val func:JsFunction = result.getJavaScriptResult().asInstanceOf[JsFunction]

    val exeResult = page.executeJavaScriptFunctionIfPossible(
      func,
      window.getScriptObject().asInstanceOf[ScriptableObject],
      Array.empty,
      page.getDocumentElement()
    )

    exeResult.getJavaScriptResult.toString == "true"
  }

  val jsTestTask = (streams, consoleHtml, jsTestBrowsers).map { (s, html, browsers) =>
    LogAdapter.logger = s.log

    browsers.foreach { browser =>
      s.log.info(s"Running JavaScript tests on $browser...")
      val success = runJs(html, browser)
      if(!success) throw new TestsFailedException()
    }
  }
}
