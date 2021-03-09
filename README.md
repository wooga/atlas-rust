atlas-rust
============

[![Gradle Plugin ID](https://img.shields.io/badge/gradle-net.wooga.rust-brightgreen.svg?style=flat-square)](https://plugins.gradle.org/plugin/net.wooga.github)
[![Build Status](https://img.shields.io/travis/wooga/atlas-rust/master.svg?style=flat-square)](https://travis-ci.org/wooga/atlas-rust)
[![Coveralls Status](https://img.shields.io/coveralls/wooga/atlas-rust/master.svg?style=flat-square)](https://coveralls.io/github/wooga/atlas-rust?branch=master)
[![Apache 2.0](https://img.shields.io/badge/license-Apache%202-blue.svg?style=flat-square)](https://raw.githubusercontent.com/wooga/atlas-rust/master/LICENSE)
[![GitHub tag](https://img.shields.io/github/tag/wooga/atlas-rust.svg?style=flat-square)]()
[![GitHub release](https://img.shields.io/github/release/wooga/atlas-rust.svg?style=flat-square)]()

A simple gradle plugin to build rust library crates.

# Applying the plugin

**build.gradle**
```groovy
plugins {
    id 'net.wooga.rust.lib' version '0.1.0'
}
```

Gradle and Java Compatibility
=============================

| Gradle Version | Works       |
| :------------- | :---------: |
| 6.8.2          | ![yes]      |

Development
===========

[Code of Conduct](docs/Code-of-conduct.md)

LICENSE
=======

Copyright 2021 Wooga GmbH

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

<http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
