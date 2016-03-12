package com.joescii.sbtjs

// Mostly placeholder for now
sealed trait Framework

object Frameworks extends Frameworks
trait Frameworks {
  case object Jasmine2 extends Framework
}
