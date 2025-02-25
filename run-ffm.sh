#!/bin/bash
set -ex
JAVA_TOOL_OPTIONS="-Djava.library.path=$PWD/go --enable-native-access=ALL-UNNAMED" ./mvnw clean compile test exec:java -Dexec.mainClass=com.example.DemoFFM -Dtest=com.example.SuffixArrayFFMTest