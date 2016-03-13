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
    main / "js" / "angular" / "angular.js",       // Make sure angular loads before the mocks
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

## Contributing

As with any open source project, contributions are greatly appreciated.
If you find an issue or have a feature idea, we'd love to know about it!
Any of the following will help this effort tremendously.

1. Issue a Pull Request with the fix/enhancement and tests to validate the changes.  OR
2. Issue a Pull Request with failing tests to show what needs to be changed OR
3. At a minimum, [open an issue](https://github.com/joescii/sbt-js-test/issues/new) to let us know about what you've discovered.

### Pull Requests

Below is the recommended procedure for git:

1. Fork it
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push the branch (`git push origin my-new-feature`)
5. Create new Pull Request

Please include as much as you are able, such as tests, documentation, updates to this README, etc.

### Testing

Part of contributing your changes will involve testing.
You can execute them via the `it:test` sbt task in this project.
The [test-projects](https://github.com/joescii/sbt-js-test/tree/master/test-projects) sub-directory contains an independent sbt multi-project that the tests run against.
At a minimum, we ask that you run the tests with your changes to ensure nothing gets inadvertently broken.
If possible, include tests which validate your fix/enhancement in any Pull Requests.

## Change Log

* *0.1.0*: Initial release.

## License

*sbt-js-test* is licensed under [APL 2.0](http://www.apache.org/licenses/LICENSE-2.0).

Copyright 2016 Joe Barnes

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

