package com.joescii.sbtjs

import com.gargoylesoftware.htmlunit. { BrowserVersion => HUBrowserVersion }
import HUBrowserVersion._

sealed trait Browser

object Browsers {
  case object Firefox38 extends Browser
  case object InternetExplorer8 extends Browser
  case object InternetExplorer11 extends Browser
  case object Chrome extends Browser
  case object Edge extends Browser
}

private [sbtjs] object BrowserVersion {
  import Browsers._

  def apply(b:Browser):HUBrowserVersion = b match {
    case Firefox38 => FIREFOX_38
    case InternetExplorer8 => INTERNET_EXPLORER_8
    case InternetExplorer11 => INTERNET_EXPLORER_11
    case Chrome => CHROME
    case Edge => EDGE
  }
}
