package com.joescii.sbtjs

import java.io.{ByteArrayOutputStream, PrintStream}

import org.apache.commons.logging.Log

object LogAdapter {
  var logger:sbt.Logger = null
  System.setProperty("org.apache.commons.logging.Log", "com.joescii.sbtjs.LogAdapter")
}

// The Log must have a string constructor
class LogAdapter(s:String) extends Log {
  import LogAdapter. { logger => l }

  override def isTraceEnabled:Boolean = true
  override def isDebugEnabled:Boolean = true
  override def isInfoEnabled:Boolean = true
  override def isWarnEnabled:Boolean = true
  override def isErrorEnabled:Boolean = true
  override def isFatalEnabled:Boolean = true

  def stacktrace(message:Object, t:Throwable):String = {
    val bs = new ByteArrayOutputStream()
    val ps = new PrintStream(bs)
    t.printStackTrace(ps)
    message.toString + "\n" + new String(bs.toByteArray, "utf-8")
  }

  override def trace(m:Object):Unit = debug(m)
  override def trace(m:Object, t:Throwable):Unit = debug(m, t)

  override def debug(m:Object):Unit = l.debug(m.toString)
  override def debug(m:Object, t:Throwable):Unit = l.debug(stacktrace(m, t))

  override def info(m:Object):Unit = l.info(m.toString)
  override def info(m:Object, t:Throwable):Unit = l.info(stacktrace(m, t))

  override def warn(m:Object):Unit =
    if(m != "Obsolete content type encountered: 'text/javascript'.") // Ignoring this dumb message
      l.warn(m.toString)
  override def warn(m:Object, t:Throwable):Unit = l.warn(stacktrace(m, t))

  override def error(m:Object):Unit = l.error(m.toString)
  override def error(m:Object, t:Throwable):Unit = l.error(stacktrace(m, t))

  override def fatal(m:Object):Unit = error(m)
  override def fatal(m:Object, t:Throwable):Unit = error(m, t)
}
