package com.example;

import java.util.List;

import static java.util.Arrays.asList;

public class SuffixArrayJNI implements SuffixArray {

	// Native handle stored as a long (points to a SuffixArrayHandle)
	private long handle;

	private final String input;

	static {
		// Load the JNI wrapper library (e.g., libsuffixarray_jni.so or
		// libsuffixarray_jni.dylib)
		System.loadLibrary("suffixarray_jni");
	}

	/**
	 * Constructs the suffix array by calling the native CreateSuffixArray function.
	 * @param input the input string
	 */
	public SuffixArrayJNI(String input) {
		this.input = input;
		handle = nativeCreate(input);
		if (handle == 0) {
			throw new RuntimeException("Failed to create native suffix array");
		}
	}

	/**
	 * Searches for all occurrences of the query string. The native method returns an
	 * array of matching suffix strings.
	 * @param query the query string
	 * @return a list of matching suffix strings
	 */
	@Override
	public List<String> searchQuery(String query) {
		String[] arr = nativeSearch(handle, query, input);
		return (arr == null) ? List.of() : asList(arr);
	}

	/**
	 * Closes the native suffix array.
	 */
	@Override
	public void close() {
		nativeClose(handle);
		handle = 0;
	}

	// Native method declarations.
	private native long nativeCreate(String input);

	private native String[] nativeSearch(long handle, String query, String input);

	private native void nativeClose(long handle);

}
