package com.joescii.sbtjs

class AllBrowsers extends SbtJsTestSpec("allBrowsers") {
  "The allBrowsers project" should {
    "successfully run 'jsTest'" in {
      result = runSbt("jsTest")
      result.futureValue._1 shouldEqual 0
    }

    "announce running on Firefox" in {
      result.futureValue._2.find(_.contains(Firefox38.toString)) shouldEqual Some(s"[info] Running JavaScript tests on $Firefox38...")
    }

    "announce running on IE11" in {
      result.futureValue._2.find(_.contains(InternetExplorer11.toString)) shouldEqual Some(s"[info] Running JavaScript tests on $InternetExplorer11...")
    }

    "announce running on Chrome" in {
      result.futureValue._2.find(_.contains(Chrome.toString)) shouldEqual Some(s"[info] Running JavaScript tests on $Chrome...")
    }
  }
}
