# Java Native Library Calling via Go

This project demonstrates how to call a native library written in Go from Java. The Go library implements a suffix array algorithm (using `index/suffixarray`) with functions to create the suffix array, search for query strings, and free allocated resources. We showcase three different Java approaches for calling the native library:

* **JNI Implementation:** Uses Java’s JNI to call a native wrapper that in turn invokes the Go library.
* **JNA Implementation:** Uses Java Native Access (JNA) to call the exported functions directly.
* **FFM Implementation:** Uses Java 22+’s Foreign Function & Memory (FFM) API to invoke native functions.

In addition, a pure Java implementation is provided for performance comparison.

## How to Run the Samples

### Build the Go Shared Library

Build the Go shared library (`libsuffixarray.dylib`) by running:

```bash
make -C go gobuild

# For the JNI version, also run:
make -C go jni
```

### Run with Java 23+

Use the provided shell scripts to run the different implementations:

```bash
# Run the JNI version
./run-jni.sh

# Run the JNA version
./run-jna.sh

# Run the FFM version
./run-ffm.sh

# Run the Pure Java version
./run-java.sh
```

## Performance Comparison

Below are the processing times observed for each implementation:

```
# JNI
== Long Input ==
process time = 779 [ns/op]
== Small Input ==
process time = 378 [ns/op]

# JNA
== Long Input ==
process time = 4203 [ns/op]
== Small Input ==
process time = 2612 [ns/op]

# FFM
== Long Input ==
process time = 1745 [ns/op]
== Small Input ==
process time = 452 [ns/op]

# Pure Java
== Long Input ==
process time = 593 [ns/op]
== Small Input ==
process time = 102 [ns/op]
```