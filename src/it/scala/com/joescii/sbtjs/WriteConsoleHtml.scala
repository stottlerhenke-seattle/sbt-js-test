package com.joescii.sbtjs

class WriteConsoleHtml extends SbtJsTestSpec("lsJs") {
  "The lsJs project" should {
    "successfully run 'lsJs'" in {
      result = runSbt("writeConsoleHtml")
      result.futureValue._1 shouldEqual 0
    }
  }
}
