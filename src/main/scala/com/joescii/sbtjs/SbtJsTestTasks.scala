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

  private [this] def doJsLs(log:Logger, main:Seq[File], test:Seq[File]):Unit = {
    log.info("jsResources (assets loaded for every test)")
    lsR(main).foreach(f => log.info(f.getCanonicalPath))
    log.info("jsTestResources (assets defining tests)")
    lsR(test).foreach(f => log.info(f.getCanonicalPath))
  }

  val jsLsTask = sbt.Def.task {
    doJsLs(streams.value.log, jsResources.value, jsTestResources.value)
  }

  private [this] def locator(lib:String) = {
    val regex = Pattern.compile(".*" + Pattern.quote(lib) + ".*")
    val classLoader = this.getClass.getClassLoader
    val ctxClassLoader = Thread.currentThread().getContextClassLoader
    Thread.currentThread().setContextClassLoader(classLoader)
    val l = new WebJarAssetLocator(WebJarAssetLocator.getFullPathIndex(regex, classLoader))
    Thread.currentThread().setContextClassLoader(ctxClassLoader)
    l
  }
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

  private [this] def writeJasmineAssets(log:Logger, target:File, color:Boolean):List[File] = {
    log.info("Writing jasmine2 assets...")

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

  private [this] def runJs(html:File, browser: Browser, asyncWait: Boolean, asyncWaitTimeout: Option[Long]):Boolean = {
    val client = new WebClient(BrowserVersion(browser))
    val options = client.getOptions()
    options.setHomePage(WebClient.URL_ABOUT_BLANK.toString())
    options.setJavaScriptEnabled(true)

    client.getPage(html.toURI.toURL)
    val window = client.getCurrentWindow().getTopWindow
    val page:HtmlPage = window.getEnclosedPage().asInstanceOf[HtmlPage] // asInstanceOf because ... java...

    def exec(js:String):String = {
      val toRun = "function() {\n"+js+"\n};"
      val result = page.executeJavaScript(toRun)
      val func:JsFunction = result.getJavaScriptResult().asInstanceOf[JsFunction]

      val exeResult = page.executeJavaScriptFunctionIfPossible(
        func,
        window.getScriptableObject(),
        Array.empty,
        page.getDocumentElement()
      )

      exeResult.getJavaScriptResult.toString
    }

    val timeout:Option[Long] = asyncWaitTimeout.map(System.currentTimeMillis() + _)

    while(asyncWait
      && timeout.map(System.currentTimeMillis() < _).getOrElse(true)
      && exec("return window.sbtJsTest.readyForTestsToRun") != "true") {
      Thread.sleep(250)
    }

    exec("jasmine.getEnv().execute();")

    while(exec("return window.sbtJsTest.complete") != "true") {
      Thread.sleep(250)
    }

    exec("return window.sbtJsTest.allPassed") == "true"
  }

  private [this] def runTests(log:Logger, rsrcs:Seq[File], target:File, color:Boolean, browsers:Seq[Browser],
                              frameworks:Seq[Framework], asyncWait: Boolean, asyncWaitTimeout: Option[Long]) = {
    import SbtJsTestPlugin.autoImport.JsTestFrameworks._
    LogAdapter.logger = log

    val html = target / "console.html"
    val frameworkAssets:List[File] =
      if(frameworks contains Jasmine2) writeJasmineAssets(log, target / "assets", color)
      else List()

    writeConsoleHtml(log, frameworkAssets ++ rsrcs, html)

    browsers.foreach { browser =>
      log.info(s"Running JavaScript tests on $browser...")
      val success = runJs(html, browser, asyncWait, asyncWaitTimeout)
      if (!success) throw new TestsFailedException()
    }
  }

  val jsTestTask = sbt.Def.task {
    val resources = jsResources.value ++ jsTestResources.value

    runTests(streams.value.log, resources, jsTestTargetDir.value, jsTestColor.value, jsTestBrowsers.value,
      jsFrameworks.value, jsAsyncWait.value, jsAsyncWaitTimeout.value)
  }

  val jsTestOnlyTask = sbt.Def.inputTask {
    val tests: Seq[String] = sbt.complete.DefaultParsers.spaceDelimited("<arg>").parsed
    val testFiles = tests.map(name => lsR(jsTestResources.value).find(_.getCanonicalPath.endsWith(name))).flatten
    val resources = jsResources.value ++ testFiles

    runTests(streams.value.log, resources, jsTestTargetDir.value, jsTestColor.value, jsTestBrowsers.value,
      jsFrameworks.value, jsAsyncWait.value, jsAsyncWaitTimeout.value)
  }

}

private [sbtjs] object BrowserVersion {
  import com.gargoylesoftware.htmlunit. { BrowserVersion => HUBrowserVersion }
  import HUBrowserVersion._
  import SbtJsTestPlugin.autoImport.JsTestBrowsers._

  def apply(b:Browser):HUBrowserVersion = b match {
    case Firefox38 => FIREFOX_38
//    case InternetExplorer8 => INTERNET_EXPLORER_8
    case InternetExplorer11 => INTERNET_EXPLORER_11
    case Chrome => CHROME
//    case Edge => EDGE
  }
}

