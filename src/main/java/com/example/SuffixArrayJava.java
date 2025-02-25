package com.example;

import java.util.ArrayList;
import java.util.List;

public class SuffixArrayJava implements SuffixArray {

	private final String input;

	private final int[] sa; // Suffix array: sorted starting indices

	/**
	 * Constructs a suffix array for the given input string. All starting indices [0, 1,
	 * …, n-1] are sorted lexicographically by comparing their corresponding suffixes.
	 * @param input the input string
	 */
	public SuffixArrayJava(String input) {
		this.input = input;
		int n = input.length();
		sa = new int[n];
		for (int i = 0; i < n; i++) {
			sa[i] = i;
		}
		// Convert to Integer[] for sorting with a custom comparator.
		Integer[] indices = new Integer[n];
		for (int i = 0; i < n; i++) {
			indices[i] = sa[i];
		}
		// Sort indices by comparing the suffixes starting at those indices.
		// This exactly reproduces the blog's approach.
		java.util.Arrays.sort(indices, (i, j) -> input.substring(i).compareTo(input.substring(j)));
		for (int i = 0; i < n; i++) {
			sa[i] = indices[i];
		}
	}

	/**
	 * Performs binary search on the suffix array. When lower is true, finds the first
	 * index where the suffix is not less than the pattern. When lower is false, finds the
	 * first index where the suffix does not start with the pattern.
	 * @param pattern the query string
	 * @param lower true to find the lower bound; false for the upper bound.
	 * @return the index in the suffix array.
	 */
	private int binarySearch(String pattern, boolean lower) {
		int lo = 0, hi = sa.length;
		while (lo < hi) {
			int mid = (lo + hi) / 2;
			String suffix = input.substring(sa[mid]);
			if (suffix.compareTo(pattern) < 0) {
				lo = mid + 1;
			}
			else if (!lower && suffix.startsWith(pattern)) {
				lo = mid + 1;
			}
			else {
				hi = mid;
			}
		}
		return lo;
	}

	/**
	 * Searches for all occurrences of the query string in the input. This method uses the
	 * binarySearch method (with a boolean flag) to determine the lower and upper bounds
	 * of the suffix array where suffixes start with the query.
	 * @param query the query string to search for
	 * @return a list of substrings starting at the matching indices.
	 */
	@Override
	public List<String> searchQuery(String query) {
		List<String> results = new ArrayList<>();
		int n = input.length();
		int lowerBound = binarySearch(query, true);
		if (lowerBound == n || !input.substring(sa[lowerBound]).startsWith(query)) {
			return results; // No match found.
		}
		int upperBound = binarySearch(query, false);
		// Collect all suffixes in the range [lowerBound, upperBound).
		for (int i = lowerBound; i < upperBound; i++) {
			results.add(input.substring(sa[i]));
		}
		return results;
	}

	/**
	 * Closes the suffix array. In this pure Java implementation, no resources are
	 * allocated.
	 */
	@Override
	public void close() {
		// Nothing to release.
	}

}
