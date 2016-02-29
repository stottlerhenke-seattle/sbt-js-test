package com.joescii.sbtjs

class TestProjectSpecs extends SbtJsTestSpec {
  "The test-project" should {
    "successfully run 'testJs'" in {
      result = runSbt(cd / "test-project", "testJs")
      result.futureValue._1 shouldEqual 0
    }

    "announce that the tests are running" in {
      result.futureValue._2(0) shouldEqual "[info] Running JavaScript tests..."
    }

    "announce that the task was successful" in {
      result.futureValue._2(1) should startWith ("[success]")
    }
  }
}
