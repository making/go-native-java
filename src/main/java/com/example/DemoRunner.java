package com.example;

import java.util.function.Function;

public class DemoRunner {

	public static void run(Function<String, ? extends SuffixArray> factory) {
		String input = "abracadabra";
		try (SuffixArray suffixArray = factory.apply(input)) {
			measure(() -> {
				suffixArray.searchQuery("ra");
				suffixArray.searchQuery("ab");
				suffixArray.searchQuery("br");
			}, 10000, 10000);
		}
	}

	static void measure(Runnable runnable, int iterations, int warmup) {
		for (int i = 0; i < warmup; i++) {
			runnable.run();
		}
		long begin = System.nanoTime();
		for (int i = 0; i < iterations; i++) {
			runnable.run();
		}
		System.out.println("process time = " + (System.nanoTime() - begin) / iterations + " [ns/ops]");
	}

}
