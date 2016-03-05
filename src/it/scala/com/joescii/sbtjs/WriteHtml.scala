package com.joescii.sbtjs

class WriteHtml extends SbtJsTestSpec("lsJs") {
  "The lsJs project" should {
    "successfully run 'lsJs'" in {
      result = runSbt("writeHtml")
      result.futureValue._1 shouldEqual 0
    }
  }
}
