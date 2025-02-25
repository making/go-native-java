.PHONY: all gobuild cbuild crun cppbuild cpprun clean

# Default target: run C demo.
all: crun

# Build the Go shared library (excluding demo.go).
gobuild:
	@echo ">> Building Go shared library (libsuffixarray.dylib)..."
	go build -buildmode=c-shared -o libsuffixarray.dylib suffixarray.go

# Build the C demo executable.
cbuild: gobuild
	@echo ">> Building C demo executable..."
	gcc -o demo demo.c -L. -lsuffixarray -Wl,-rpath,.

# Run the C demo executable.
crun: cbuild
	@echo ">> Running C demo..."
	./demo

# Build the C++ demo executable.
cppbuild: gobuild
	@echo ">> Building C demo executable..."
	g++ -std=c++17 -o demo demo.cpp -L. -lsuffixarray

# Run the C demo executable.
cpprun: cppbuild
	@echo ">> Running C++ demo..."
	./demo

jni: gobuild
	@echo ">> Building JNI wrapper..."

	gcc -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/darwin" -shared -o libsuffixarray_jni.dylib suffixarray_jni.c -L. -lsuffixarray
	install_name_tool -change libsuffixarray.dylib @loader_path/libsuffixarray.dylib libsuffixarray_jni.dylib


# Clean up generated files.
clean:
	@echo ">> Cleaning up generated files..."
	rm -f *.dylib *.h demo
