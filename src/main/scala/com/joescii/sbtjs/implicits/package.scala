package com.joescii.sbtjs

import java.io.{FileOutputStream, PrintStream, File}

package object implicits {
  implicit class EnhancedFile(val f:File) extends AnyVal {
    def /(child:String):File = new File(f, child)
  }
}
