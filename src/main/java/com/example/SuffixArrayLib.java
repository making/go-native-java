package com.example;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;

public interface SuffixArrayLib extends Library {

	SuffixArrayLib INSTANCE = Native.load("suffixarray", SuffixArrayLib.class);

	// CreateSuffixArray: (char* data, int length) -> SuffixArrayHandle*
	SuffixArrayHandle CreateSuffixArray(Pointer data, int length);

	// SearchSuffixArray: (SuffixArrayHandle* handle, char* query, int queryLength) ->
	// IntArray*
	IntArray SearchSuffixArray(SuffixArrayHandle handle, Pointer query, int queryLength);

	// FreeIntArray: frees the IntArray structure and its allocated array.
	void FreeIntArray(IntArray arr);

	// FreeSuffixArray: frees the SuffixArrayHandle.
	void FreeSuffixArray(SuffixArrayHandle handle);

	class SuffixArrayHandle extends Structure implements Structure.ByReference {

		public Pointer ptr;

		@Override
		protected List<String> getFieldOrder() {
			return List.of("ptr");
		}

	}

	class IntArray extends Structure implements Structure.ByReference {

		public Pointer data; // Pointer to an array of int values

		public int length;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("data", "length");
		}

	}

}
