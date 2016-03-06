package com.joescii.sbtjs

class AllPass extends SbtJsTestSpec("allPass") {
  "The all-pass project" should {
    "successfully run 'testJs'" in {
      result = runSbt("testJs")
      result.futureValue._1 shouldEqual 0
    }

    "cause writeConsoleHtml to run" in {
      result.futureValue._2(0) should startWith ("[info] Generating")
      result.futureValue._2(0) should endWith   ("console.html...")
    }

    "announce that the tests are running" in {
      result.futureValue._2(1) shouldEqual "[info] Running JavaScript tests..."
    }

    "announce that the task was successful" in {
      result.futureValue._2.last should startWith ("[success]")
    }
  }
}
