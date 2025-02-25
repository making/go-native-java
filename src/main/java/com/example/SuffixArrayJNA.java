package com.example;

import com.sun.jna.Memory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SuffixArrayJNA implements SuffixArray {

	private final String input;

	private final SuffixArrayLib.SuffixArrayHandle handle;

	public SuffixArrayJNA(String input) {
		this.input = input;
		byte[] inputBytes = input.getBytes(); // using platform default charset
		Memory inputMem = new Memory(inputBytes.length);
		inputMem.write(0, inputBytes, 0, inputBytes.length);
		// Create suffix array; the refactored API returns a handle.
		handle = SuffixArrayLib.INSTANCE.CreateSuffixArray(inputMem, inputBytes.length);
	}

	@Override
	public List<String> searchQuery(String query) {
		List<String> results = new ArrayList<>();
		byte[] queryBytes = query.getBytes();
		Memory queryMem = new Memory(queryBytes.length);
		queryMem.write(0, queryBytes, 0, queryBytes.length);
		SuffixArrayLib.IntArray result = SuffixArrayLib.INSTANCE.SearchSuffixArray(handle, queryMem, queryBytes.length);
		if (result != null) {
			result.read();
			if (result.length > 0 && result.data != null) {
				int[] indices = result.data.getIntArray(0, result.length);
				for (int idx : indices) {
					results.add(input.substring(idx));
				}
			}
			SuffixArrayLib.INSTANCE.FreeIntArray(result);
		}
		return Collections.unmodifiableList(results);
	}

	@Override
	public void close() {
		SuffixArrayLib.INSTANCE.FreeSuffixArray(handle);
	}

}
