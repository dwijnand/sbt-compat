# [sbt-compat][] [![travis-badge][]][travis]

[sbt-compat]:         https://github.com/dwijnand/sbt-compat
[travis]:          https://travis-ci.org/dwijnand/sbt-compat
[travis-badge]:    https://travis-ci.org/dwijnand/sbt-compat.svg?branch=master

`sbt-compat` is an [sbt](http://www.scala-sbt.org/) plugin that backports parts of sbt 1's public API on top of
sbt 0.13 implementation.

The idea of providing the latest (and greatest) API on top of older versions comes from Miles Sabin's
[macro-compat](https://github.com/milessabin/macro-compat) where the newer macro APIs are provided for older
versions of Scala.

## Setup

Add this plugin to your sbt plugin (in `build.sbt`, **NOT** `project/plugins.sbt`):

    addSbtPlugin("com.dwijnand" % "sbt-compat" % "1.0.0")

## Usage

Use sbt 1's API, importing the relevant packages.

It might be necessary to wildcard import at times, and sometimes it might be necessary to import `sbt.compat._`.

## Licence

Copyright 2017 Dale Wijnand

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
