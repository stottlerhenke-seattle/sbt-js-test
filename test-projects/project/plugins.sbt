//resolvers += "Local Maven Repository" at Path.userHome.asFile.toURI.toURL + ".m2/repository"

libraryDependencies ++= Seq(
  "net.sourceforge.htmlunit"  % "htmlunit"             % "2.19"       % "runtime",
  "org.webjars"               % "webjars-locator-core" % "0.30"       % "runtime",
  "org.webjars.bower"         % "jasmine"              % "2.4.1"      % "runtime"
)