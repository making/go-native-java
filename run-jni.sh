#!/bin/bash
set -ex
JAVA_TOOL_OPTIONS="-Djava.library.path=$PWD/go" ./mvnw clean compile test exec:java -Dexec.mainClass=com.example.DemoJNI -Dtest=com.example.SuffixArrayJNITest
