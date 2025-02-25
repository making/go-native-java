#!/bin/bash
set -ex
./mvnw clean compile test exec:java -Dexec.mainClass=com.example.DemoJava -Dtest=com.example.SuffixArrayJavaTest