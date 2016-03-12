package com.joescii.sbtjs

class NonExistentDir extends SbtJsTestSpec("nonExistentDir") {
  "The nonExistentDir project" should {
    "successfully run 'jsLs'" in {
      result = runSbt("jsLs")
      result.futureValue._1 shouldEqual 0
    }
  }

  "have only 3 lines of output" in {
    result.futureValue._2.length shouldEqual 3
  }

  "announce that the task was successful" in {
    result.futureValue._2.last should startWith ("[success]")
  }

}
