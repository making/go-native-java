# Java Native Library Calling via Go

This project demonstrates how to call a native library written in Go from Java. The Go library implements a SuffixArray (`index/suffixarray`) algorithm with functions to create a suffix array, search for a query string, and free allocated resources. We compare three different Java approaches to calling the native library:

* JNA Implementation: Uses Java Native Access (JNA) to call the exported functions.
* FFM Implementation: Uses Java 22+'s Foreign Function & Memory (FFM) API to invoke native functions.
* JNI Implementation: Uses Java’s JNI to call a native wrapper that in turn calls the Go library.

Additionally, a pure Java implementation is provided for functional comparison.

## How to run the samples

Build the Go shared library (libsuffixarray.dylib).

```
make -C go gobuild

# If you run the JNI version
make -C go jni
```

Use Java 23+

```
# Run JNA version
./run-jna.sh

# RUN FFM version
./run-ffm.sh

# RUN JNI version
./run-jni.sh

# Run Pure Java version
./run-java.sh
```

### Performance Comparison

```
# JNA
process time = 6577 [ns/ops]

# FFM
process time = 2203 [ns/ops]

# JNI
process time = 2090 [ns/ops]

# Pure Java
process time = 517 [ns/ops]
```