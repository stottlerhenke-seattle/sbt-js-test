package com.joescii.sbtjs

class AllPass extends SbtJsTestSpec("allPass") {
  "The allPass project" should {
    "successfully run 'testJs'" in {
      result = runSbt("testJs")
      result.futureValue._1 shouldEqual 0
    }

    "cause writeJsAssets to run" in {
      result.futureValue._2(0) shouldEqual ("[info] Writing js assets...")
    }

    "cause writeConsoleHtml to run" in {
      result.futureValue._2(1) should startWith ("[info] Generating")
      result.futureValue._2(1) should endWith   ("console.html...")
    }

    "announce that the tests are running" in {
      result.futureValue._2(2) shouldEqual "[info] Running JavaScript tests..."
    }

    "announce that 1 spec ran with 0 failures" in {
      result.futureValue._2.reverse.apply(2) shouldEqual "[info] 1 spec, 0 failures"
    }

    "announce that the task was successful" in {
      result.futureValue._2.last should startWith ("[success]")
    }
  }
}
