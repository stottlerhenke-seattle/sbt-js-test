package com.joescii.sbtjs

import com.joescii.sbtjs.SbtJsTestPlugin.autoImport.JsTestBrowsers.Chrome

class AllPass extends SbtJsTestSpec("allPass") {
  "The allPass project" should {
    "successfully run 'jsTest'" in {
      result = runSbt("jsTest")
      result.futureValue._1 shouldEqual 0
    }

    "cause writeJsAssets to run" in {
      result.futureValue._2(0) shouldEqual ("[info] Writing jasmine2 assets...")
    }

    "cause writeConsoleHtml to run" in {
      result.futureValue._2(1) should startWith ("[info] Generating")
      result.futureValue._2(1) should endWith   ("console.html...")
    }

    "announce that the tests are running on Chrome" in {
      result.futureValue._2(2) shouldEqual s"[info] Running JavaScript tests on $Chrome..."
    }

    "announce that 1 spec ran with 0 failures" in {
      result.futureValue._2.reverse.apply(2) shouldEqual "[info] 1 spec, 0 failures"
    }

    "announce that the task was successful" in {
      result.futureValue._2.last should startWith ("[success]")
    }
  }
}
