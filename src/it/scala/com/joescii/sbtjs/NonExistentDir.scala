package com.joescii.sbtjs

class NonExistentDir extends SbtJsTestSpec("nonExistentDir") {
  "The nonExistentDir project" should {
    "successfully run 'lsJs'" in {
      result = runSbt("lsJs")
      result.futureValue._1 shouldEqual 0
    }
  }

  "have only 1 line of output" in {
    result.futureValue._2.length shouldEqual 1
  }

  "announce that the task was successful" in {
    result.futureValue._2.last should startWith ("[success]")
  }

}
