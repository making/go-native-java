package main

/*
#include <stdlib.h>

// SuffixArrayHandle holds a pointer to a suffixarray.Index.
typedef struct {
    void* ptr;
} SuffixArrayHandle;

// IntArray represents an array of ints.
typedef struct {
    int *data;
    int length;
} IntArray;
*/
import "C"
import (
	"index/suffixarray"
	"unsafe"
)

// CreateSuffixArray creates a suffix array from the given byte slice
// and returns a pointer to a SuffixArrayHandle.
//export CreateSuffixArray
func CreateSuffixArray(data *C.char, length C.int) unsafe.Pointer {
	// Convert the C string to a Go byte slice.
	goData := C.GoBytes(unsafe.Pointer(data), length)
	// Create the suffix array.
	sa := suffixarray.New(goData)
	// Allocate memory for a SuffixArrayHandle and store the pointer.
	handle := C.malloc(C.size_t(unsafe.Sizeof(uintptr(0))))
	*(*uintptr)(handle) = uintptr(unsafe.Pointer(sa))
	return handle
}

// SearchSuffixArray searches for the query in the suffix array referenced by handle.
// It returns a pointer to an IntArray structure containing the indices where the query is found.
//export SearchSuffixArray
func SearchSuffixArray(handle unsafe.Pointer, query *C.char, queryLength C.int) *C.IntArray {
	// Retrieve the suffix array pointer from the handle.
	saPtr := *(*uintptr)(handle)
	sa := (*suffixarray.Index)(unsafe.Pointer(saPtr))
	// Convert the query C string to a Go byte slice.
	queryBytes := C.GoBytes(unsafe.Pointer(query), queryLength)
	indices := sa.Lookup(queryBytes, -1)
	length := len(indices)
	var cArray unsafe.Pointer
	if length > 0 {
		// Allocate native memory for the int array.
		cArray = C.malloc(C.size_t(length) * C.size_t(unsafe.Sizeof(C.int(0))))
		intArray := (*[1 << 30]C.int)(cArray)
		for i, idx := range indices {
			intArray[i] = C.int(idx)
		}
	} else {
		cArray = nil
	}
	// Allocate memory for the IntArray structure and populate it.
	ret := (*C.IntArray)(C.malloc(C.size_t(unsafe.Sizeof(C.IntArray{}))))
	ret.data = (*C.int)(cArray)
	ret.length = C.int(length)
	return ret
}

// FreeIntArray frees the memory allocated for the search result (both the int array and the structure).
//export FreeIntArray
func FreeIntArray(arr *C.IntArray) {
	if arr == nil {
		return
	}
	if arr.data != nil {
		C.free(unsafe.Pointer(arr.data))
	}
	C.free(unsafe.Pointer(arr))
}

// FreeSuffixArray frees the memory allocated for the SuffixArrayHandle.
//export FreeSuffixArray
func FreeSuffixArray(handle unsafe.Pointer) {
	C.free(handle)
}

func main() {}
