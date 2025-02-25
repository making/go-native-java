#!/bin/bash
set -ex
JAVA_TOOL_OPTIONS="-Djna.library.path=$PWD/go" ./mvnw clean compile test exec:java -Dexec.mainClass=com.example.DemoJNA -Dtest=com.example.SuffixArrayJNATest