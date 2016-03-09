package com.joescii.sbtjs

class LsJs extends SbtJsTestSpec("jsLs") {
  "The jsLs project" should {
    "successfully run 'jsLs'" in {
      result = runSbt("jsLs")
      result.futureValue._1 shouldEqual 0
    }
  }

  val slash = System.getProperty("file.separator")
  val slashRegex = java.util.regex.Pattern.quote(slash)
  def path(line:String):List[String] =
    line.split(slashRegex).toList.reverse.span(_ != "js")._1.reverse

  "list all 3 js source files" in {
    whenReady(result) { case (status, out) =>
      val pathsSorted = out.take(3).map(path).sortBy(_.mkString(slash))

      pathsSorted(0) shouldEqual List("3rdLib", "angular-mocks.js")
      pathsSorted(1) shouldEqual List("3rdLib", "angular.js")
      pathsSorted(2) shouldEqual List("Hello.js")
    }
  }
}
