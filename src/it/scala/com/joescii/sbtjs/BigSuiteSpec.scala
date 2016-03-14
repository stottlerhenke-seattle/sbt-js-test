package com.joescii.sbtjs

class BigSuiteSpec extends SbtJsTestSpec("bigSuite") {
  "The bigSuite project" should {
    "successfully run 'jsTest'" ignore {
      result = runSbt("jsTest")
      result.futureValue._1 shouldEqual 0
    }

    "announce that 88 specs ran with 0 failures" ignore {
      result.futureValue._2.reverse.apply(2) shouldEqual "[info] 88 specs, 0 failures, 4 pending specs"
    }

    "announce that the task was successful" ignore {
      result.futureValue._2.last should startWith ("[success]")
    }
  }
}
