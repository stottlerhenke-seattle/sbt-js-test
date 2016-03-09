package com.joescii.sbtjs

class WriteConsoleHtml extends SbtJsTestSpec("jsLs") {
  "The jsLs project" should {
    "successfully run 'jsLs'" in {
      result = runSbt("writeConsoleHtml")
      result.futureValue._1 shouldEqual 0
    }
  }
}
