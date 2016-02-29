package com.joescii.sbtjs

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{ShouldMatchers, WordSpec}

import scala.concurrent.Future

class TestProjectSpecs extends WordSpec with ShouldMatchers with ScalaFutures {
  implicit val defaultPatience = PatienceConfig(timeout = Span(10, Seconds), interval = Span(500, Millis))

  var result:Result = Future.failed(new Exception)

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
