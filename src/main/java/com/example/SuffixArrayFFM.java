package com.example;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SuffixArrayFFM implements SuffixArray {

	private final String input;

	private final long id;

	private final Arena arena;

	// Pre-created MethodHandles and VarHandles for efficiency.
	private final MethodHandle searchHandle;

	private final MethodHandle freeIntHandle;

	private final MethodHandle freeSuffixHandle;

	private final VarHandle lengthHandle;

	private final VarHandle dataHandle;

	// Layout for the Go IntArray structure: struct { void* data; int length; }
	static final MemoryLayout INT_ARRAY_LAYOUT = MemoryLayout.structLayout(ValueLayout.ADDRESS.withName("data"),
			ValueLayout.JAVA_INT.withName("length"));

	// Constructor: creates the suffix array from the input string and initializes
	// MethodHandles.
	public SuffixArrayFFM(String input) {
		this.input = input;
		this.arena = Arena.ofShared();
		Linker linker = Linker.nativeLinker();
		System.loadLibrary("suffixarray");
		// Using loaderLookup() to find symbols from the shared library.
		SymbolLookup lookup = SymbolLookup.loaderLookup();

		MemorySegment inputSeg = arena.allocateFrom(input);
		int inputLen = input.getBytes(StandardCharsets.UTF_8).length;
		MethodHandle createMH = linker.downcallHandle(lookup.find("CreateSuffixArray").orElseThrow(),
				FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS, ValueLayout.JAVA_INT));
		try {
			this.id = (long) createMH.invoke(inputSeg, inputLen);
		}
		catch (Throwable e) {
			throw new IllegalStateException("Failed to invoke 'CreateSuffixArray'", e);
		}

		// Pre-create MethodHandles and VarHandles used in searchQuery and close.
		this.searchHandle = linker.downcallHandle(lookup.find("SearchSuffixArray").orElseThrow(), FunctionDescriptor
			.of(ValueLayout.ADDRESS, ValueLayout.JAVA_LONG, ValueLayout.ADDRESS, ValueLayout.JAVA_INT));
		this.freeIntHandle = linker.downcallHandle(lookup.find("FreeIntArray").orElseThrow(),
				FunctionDescriptor.ofVoid(ValueLayout.JAVA_LONG));
		this.freeSuffixHandle = linker.downcallHandle(lookup.find("FreeSuffixArray").orElseThrow(),
				FunctionDescriptor.ofVoid(ValueLayout.JAVA_LONG));
		this.lengthHandle = INT_ARRAY_LAYOUT.varHandle(PathElement.groupElement("length"));
		this.dataHandle = INT_ARRAY_LAYOUT.varHandle(PathElement.groupElement("data"));
	}

	@Override
	public List<String> searchQuery(String query) {
		List<String> results = new ArrayList<>();
		MemorySegment querySeg = arena.allocateFrom(query);
		int queryLen = query.getBytes(StandardCharsets.UTF_8).length;
		MemorySegment resultSeg;
		try {
			resultSeg = (MemorySegment) searchHandle.invoke(id, querySeg, queryLen);
		}
		catch (Throwable e) {
			throw new IllegalStateException("Failed to invoke 'SearchSuffixArray'", e);
		}

		// Reinterpret resultSeg as the IntArray structure.
		MemorySegment resultStruct = resultSeg.reinterpret(INT_ARRAY_LAYOUT.byteSize());
		int resultLength = (int) lengthHandle.get(resultStruct, 0L);
		MemorySegment rawDataPtr = (MemorySegment) dataHandle.get(resultStruct, 0L);
		long targetSize = resultLength * ValueLayout.JAVA_INT.byteSize();
		MemorySegment pointedData = rawDataPtr.reinterpret(targetSize);

		if (resultLength > 0 && !rawDataPtr.equals(MemorySegment.NULL)) {
			int intSize = (int) ValueLayout.JAVA_INT.byteSize();
			for (int i = 0; i < resultLength; i++) {
				int idx = pointedData.get(ValueLayout.JAVA_INT, (long) i * intSize);
				results.add(input.substring(idx));
			}
		}

		try {
			freeIntHandle.invoke(resultSeg.address());
		}
		catch (Throwable e) {
			throw new IllegalStateException("Failed to invoke 'FreeIntArray'", e);
		}
		return Collections.unmodifiableList(results);
	}

	@Override
	public void close() {
		try {
			freeSuffixHandle.invoke(id);
		}
		catch (Throwable e) {
			throw new IllegalStateException("Failed to invoke 'FreeSuffixArray'", e);
		}
		finally {
			arena.close();
		}
	}

}
