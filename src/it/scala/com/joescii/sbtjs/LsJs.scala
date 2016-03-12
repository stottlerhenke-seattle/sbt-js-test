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

  "let the user know the first list is defined by jsResources" in {
    whenReady(result) { case (_, out) =>
      out(0).contains("jsResources") shouldBe true
    }
  }

  "list all 4 jsResource files" in {
    whenReady(result) { case (_, out) =>
      val pathsSorted = out.drop(1).take(4).map(path).sortBy(_.mkString(slash))

      pathsSorted(0) shouldEqual List("3rdLib", "angular-mocks.js")
      pathsSorted(1) shouldEqual List("3rdLib", "angular.js")
      pathsSorted(2) shouldEqual List("Hello.js")
      pathsSorted(3) shouldEqual List("Resource.js")
    }
  }

  "let the user know the second list is defined by jsTestResources" in {
    whenReady(result) { case (_, out) =>
      out.drop(5).apply(0).contains("jsTestResources") shouldBe true
    }
  }

  "list both jsTestResource files" in {
    whenReady(result) { case (_, out) =>
      val pathsSorted = out.drop(6).take(2).map(path).sortBy(_.mkString(slash))

      pathsSorted(0) shouldEqual List("test-js.js")
      pathsSorted(1) shouldEqual List("test-resource.js")
    }
  }
}
