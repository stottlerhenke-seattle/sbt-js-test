package com.joescii.sbtjs

import java.io.{InputStreamReader, BufferedReader}
import java.util.regex.Pattern

import com.gargoylesoftware.htmlunit.html.HtmlPage
import implicits._

import com.gargoylesoftware.htmlunit.WebClient
import net.sourceforge.htmlunit.corejs.javascript. { ScriptableObject, Function => JsFunction }
import org.webjars.WebJarAssetLocator
import sbt.{Logger, TestsFailedException, IO, File}
import sbt.Keys._

object SbtJsTestTasks extends SbtJsTestKeys {
  private [this] def lsR(fs:Seq[File]):List[File] =
    fs.flatMap(lsR).toList

  private [this] def lsR(f:File):List[File] =
    if(!f.exists()) List()
    else if(!f.isDirectory) List(f)
    else f.listFiles().toList.flatMap(lsR)

  val jsLsTask = (streams, jsResources).map { (s, rsrcs) =>
    lsR(rsrcs).foreach(f => s.log.info(f.getCanonicalPath))
  }

  private [this] def locator(lib:String) = new WebJarAssetLocator(
    WebJarAssetLocator.getFullPathIndex(Pattern.compile(".*"+Pattern.quote(lib)+".*"), this.getClass.getClassLoader)
  )
  private [this] def cat(classpath:String) = {
    val url = this.getClass.getClassLoader.getResource(classpath)
    val r = new BufferedReader(new InputStreamReader(url.openStream()))
    echo(Iterator.continually(r.readLine()).takeWhile(_ != null).mkString("\n"))
  }
  private [this] def echo(s:String) = new {
    def > (f:File):File = { IO.write(f, s); f }
  }

  private [this] val jasmineLocator = locator("jasmine")
  private [this] def sbtJsTest(target:File) = target / "sbtJsTest.js"
  private [this] def jasmine(target:File) = target / "jasmine" / "jasmine.js"
  private [this] def jasmineHtmlUnitBoot(target:File) = target / "jasmine" / "htmlunit_boot.js"
  private [this] def jasmineConsole(target:File) = target / "jasmine" / "console.js"

  private [this] def writeJsAssets(log:Logger, target:File, color:Boolean):List[File] = {
    log.info("Writing js assets...")

    val colorJs = s"""
        |window.sbtJsTest = window.sbtJsTest || {};
        |window.sbtJsTest.showColors = $color;
      """.stripMargin

    List(
      echo(colorJs) > sbtJsTest(target),
      cat(jasmineLocator.getFullPath("jasmine.js")) > jasmine(target),
      cat(jasmineLocator.getFullPath("console.js")) > jasmineConsole(target),
      cat("js/htmlunit_jasmine_boot.js") > jasmineHtmlUnitBoot(target)
    )
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

  private [this] def writeConsoleHtml(log:Logger, rsrcs:Seq[File], html:File):Unit = {
    log.info(s"Generating ${html.getCanonicalPath}...")
    IO.write(html, htmlFor(lsR(rsrcs)))
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

  private [this] def runTests(log:Logger, rsrcs:Seq[File], target:File, color:Boolean, browsers:Seq[Browser]) = {
    LogAdapter.logger = log

    val html = target / "console.html"
    val assets = writeJsAssets(log, target / "assets", color)
    writeConsoleHtml(log, assets ++ rsrcs, html)

    browsers.foreach { browser =>
      log.info(s"Running JavaScript tests on $browser...")
      val success = runJs(html, browser)
      if (!success) throw new TestsFailedException()
    }
  }

  val jsTestTask = sbt.Def.task {
    val resources = jsResources.value ++ jsTestResources.value

    runTests(streams.value.log, resources, jsTestTargetDir.value, jsTestColor.value, jsTestBrowsers.value)
  }

  val jsTestOnlyTask = sbt.Def.inputTask {
    val tests: Seq[String] = sbt.complete.DefaultParsers.spaceDelimited("<arg>").parsed
    val testFiles = tests.map(name => lsR(jsTestResources.value).find(_.getCanonicalPath.endsWith(name))).flatten
    val resources = jsResources.value ++ testFiles

    runTests(streams.value.log, resources, jsTestTargetDir.value, jsTestColor.value, jsTestBrowsers.value)
  }

}
