package com.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	 * array of matching indices, which are then used to construct the corresponding
	 * suffix substrings from the input.
	 * @param query the query string
	 * @return a list of matching suffix strings
	 */
	@Override
	public List<String> searchQuery(String query) {
		int[] indices = nativeSearch(handle, query);
		List<String> results = new ArrayList<>();
		if (indices != null) {
			for (int idx : indices) {
				// Create substring from the index.
				results.add(input.substring(idx));
			}
		}
		return Collections.unmodifiableList(results);
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

	private native int[] nativeSearch(long handle, String query);

	private native void nativeClose(long handle);

}
