# sbt-js-test
sbt plugin for running JavaScript tests on the JVM with browser APIs

## Why?

Why another JavaScript test plugin?
*sbt-js-test* was built to provide 

1. A JavaScript test environment with the browser API available
2. Supporting multiple browser APIs
3. While only requiring the JVM
4. And being JavaScript test framework agnostic (Only Jasmine 2.x is supported at the moment, though.)

## Installation

*sbt-js-test* is an [auto plugin](http://www.scala-sbt.org/0.13/docs/Plugins.html#Using+an+auto+plugin).
Simply add the following lines to an `sbt` file in your project's `project` directory, such as `project/plugins.sbt`:

```scala
libraryDependencies += "org.webjars.bower" % "jasmine" % "2.4.1" % "runtime"
addSbtPlugin("com.joescii" % "sbt-js-test" % "0.1.0-SNAPSHOT")
```

## Usage

After installation, execute the `jsLs` task to list the JavaScript assets that will be used for running tests.
The first list are files found in the `jsResource` setting and will be loaded for every test.
The second list are files found in the `jsTestResource` setting and will all be executed by `jsTest` or individually by `jsTestOnly`.
Note that `jsLs` lists the files in the exact order they will be loaded.
If you don't see your javascript files or they are not in the correct order to run, see [Custom Configuration](#custom-configuration) below.

Run *all* of your tests with `jsTest`.
Selectively run individual test files from `jsTestResources` with `jsTestOnly`.


## Custom Configuration

By default, we look in `src/main/js`, `src/main/javascript`, and `src/main/resources` for `*.js` files in `jsResource`.
Similarly for `jsTestResource`, we look in `src/test/js`, `src/test/javascript`, and `src/test/resources`.
The files are loaded in alphabetical order thanks to your OS's implementation of `java.io.File.listFiles()`.
You can control the files as you would with any other sbt setting of type `Seq[File]`.

```scala
jsResources := {
  val main = (sourceDirectory in Compile).value
  val test = (sourceDirectory in Test).value
  Seq(
    main / "js" / "angular" / "angular.js",        // Make sure angular loads before the mocks
    main / "js" / "angular" / "angular-mocks.js",
    main / "js" / "sample-app.js",
    test / "js" / "mocks"                         // Have mocks always available for jsTestOnly 
)}

jsTestResources := {
  val test = (sourceDirectory in Test).value
  ((test / "scripts") ** "*.spec.js").get         // Using sbt's handy PathFinder
}
```

By default, the browser API provided mimics Chrome.
You can configure any of Chrome, Firefox, and IE11 for your testing.
All of your tests will be run for each browser.

```scala
import JsTestBrowsers._
jsTestBrowsers := Seq(Chrome, Firefox38, InternetExplorer11)
```

By default, test output will print to your console in color.
If this makes a mess in your console or in your continuous integration server logs, you can turn it off.

```scala
jsTestColor := false
```

## TODO
1.  ~~Define key for locating js artifacts~~
2.  ~~Render `console.html` containing the scripts in `target`~~
3.  ~~Run page in `HtmlUnit`~~
4.  ~~Cleanup `HtmlUnit` logging.~~
5.  ~~Create jasmine integration~~
6.  ~~Read jasmine from webjar~~
7.  ~~Fail task if jasmine fails~~
8.  ~~Better stack traces when encountering JS errors~~
9.  ~~Define color print key~~
10. ~~Define key for browser version~~
11. ~~Handle empty directories~~
12. ~~Change `testJs` to `jsTest`~~
13. ~~Support watching with `~jsTest`~~
14. ~~Support `jsTestOnly`~~ 
15. ~~Remove newlines between test progress indicators~~
16. ~~Define `SettingKey`s for jasmine configuration~~
17. ~~Refactor to match latest plugin best practices~~
18. ~~Address compiler warning related to `HtmlUnit`~~
19. ~~Add `Spec` suffix to test files~~
20. ~~Document~~ 
22. Wait on [webjars PR 10](https://github.com/webjars/webjars-locator-core/pull/10)
21. Release
22. Open issues for remaining ideas and TODOs
23. Read any asset from webjar
24. Jasmine runner html page
25. Integrate another JS test framework like Mocha
26. `jsTestOnly` tab completion