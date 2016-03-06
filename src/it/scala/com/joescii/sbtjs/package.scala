package com.joescii

import java.io._

import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

package object sbtjs {
  import implicits._

  type Result = Future[(Int, List[String])]

  val separator = System.getProperty("file.separator")
  val windows = System.getProperty("os.name").matches("(?i).*win.*")
  val cd = new File(".")

  implicit class EnhancedString(val s:String) extends AnyVal {
    def /(child:String):String = s + separator + child
  }

  def echo(str:String) = new {
    def > (f:File):Unit = {
      val out = new PrintStream(new FileOutputStream(f))
      out.print(str)
      out.close()
    }
  }

  def copy(from:File, to:File):Unit = new FileOutputStream(to).getChannel().transferFrom( new FileInputStream(from).getChannel, 0, Long.MaxValue )

  class SbtJsTestSpec(project:String) extends WordSpec with ShouldMatchers with ScalaFutures {
    import build.BuildInfo._

    implicit val defaultPatience = PatienceConfig(timeout = Span(8, Seconds), interval = Span(500, Millis))

    var result:Result = Future.failed(new Exception)

    val dir = cd / "test-projects" / project

    def copyPluginJar():Unit = {
      val Regex = """(.*)\Q.\E[^.]*$""".r
      val Regex(sbtBinaryVersion) = sbtVersion
      val lib = dir / "project" / "lib"
      lib.mkdirs()
      copy(cd / "target" / s"scala-$scalaBinaryVersion" / s"sbt-$sbtBinaryVersion" / s"sbt-js-test-$version.jar", lib / "sbt-js-test.jar")
    }

    def writeDependenciesSbt():Unit = {
      val contents =
        s"""resolvers += "Local Maven Repository" at Path.userHome.asFile.toURI.toURL + ".m2/repository"
        |libraryDependencies ++= Seq(
        |  "net.sourceforge.htmlunit"  % "htmlunit"             % "$htmlunitVersion"       % "runtime",
        |  "org.webjars"               % "webjars-locator-core" % "$webjarLocatorVersion"  % "runtime",
        |  "org.webjars.bower"         % "jasmine"              % "$jasmineVersion"        % "runtime"
        |)""".stripMargin

      echo(contents) > dir / "project" / "htmlunit.sbt"

    }

    def runSbt(tasks:String*):Result = Future {
      val sbtScript = "sbt" + (if(windows) ".bat" else "")
      val sbtBin = Option(System.getenv("sbt_home")).map(_ / sbtScript).getOrElse(sbtScript)
      val cmd = sbtBin :: tasks.toList
      val builder = new ProcessBuilder(cmd:_*)
      builder.directory(dir)
      builder.redirectErrorStream(true)

      println(s"Running ${cmd.mkString(" ")} in $dir...")

      val process = builder.start()
      val stream = process.getInputStream
      val buffer = new BufferedReader(new InputStreamReader(stream))
      lazy val status = process.waitFor()
      lazy val output = Iterator.continually(buffer.readLine())
        .takeWhile(_ != null)
        .map { line => if(itDebug) println(line); line }
        .dropWhile(!_.startsWith("[info] Set current project to")) // Drop all of the usual junk
        .drop(1) // then drop the last line of usual junk
        .toList

      (status, output)
    }

    copyPluginJar()
    writeDependenciesSbt()
  }
}
