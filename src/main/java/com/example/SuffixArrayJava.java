package com.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SuffixArrayJava implements SuffixArray {

	private final String text;

	private final Integer[] suffix;

	/**
	 * Constructs the suffix array for the given input string. It builds an array of
	 * indices [0, 1, ..., n-1] and sorts them lexicographically by comparing the
	 * corresponding suffixes.
	 * @param text the input string
	 */
	public SuffixArrayJava(String text) {
		this.text = text;
		int n = text.length();
		suffix = new Integer[n];
		build();
	}

	/**
	 * Builds the suffix array. Stores each index and then sorts the indices so that the
	 * suffixes are in ascending order.
	 */
	private void build() {
		for (int i = 0; i < text.length(); i++) {
			suffix[i] = i;
		}
		Arrays.sort(suffix, new java.util.Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				String s1 = text.substring(o1);
				String s2 = text.substring(o2);
				return s1.compareTo(s2);
			}
		});
	}

	/**
	 * Performs binary search to find the first index in the suffix array whose
	 * corresponding suffix starts with the given key. This method exactly reproduces the
	 * logic from the blog article.
	 * @param key the query string
	 * @return the index in the suffix array, or -1 if no suffix starts with key.
	 */
	private int binarySearch(String key) {
		int size = suffix.length;
		int l = -1;
		int u = size;
		int ks = key.length();

		while (l + 1 != u) {
			int m = (l + u) / 2;
			int t = suffix[m];
			String s = text.substring(t);
			if (ks < s.length()) {
				s = s.substring(0, ks);
			}
			int c = s.compareTo(key);
			if (c < 0) {
				l = m;
			}
			else {
				u = m;
			}
		}

		int p = u;
		if (p < size) {
			int t = suffix[p];
			String s = text.substring(t, t + ks);
			if (s.compareTo(key) == 0) {
				return p;
			}
		}
		return -1;
	}

	/**
	 * Searches for all occurrences of the query string in the input. It finds the first
	 * suffix that starts with the query using binarySearch, and then iterates until the
	 * suffix no longer matches.
	 * @param query the query string to search for
	 * @return a list of substrings (suffixes) starting at the matching indices.
	 */
	@Override
	public List<String> searchQuery(String query) {
		List<String> result = new ArrayList<>();
		int p = binarySearch(query);
		int ks = query.length();

		if (p != -1) {
			while (p < suffix.length) {
				int i = suffix[p];
				String s = text.substring(i, i + ks);
				if (s.equals(query)) {
					result.add(text.substring(i));
				}
				else {
					break;
				}
				p++;
			}
		}
		return Collections.unmodifiableList(result);
	}

	/**
	 * Closes this suffix array. No resources to free in this pure Java implementation.
	 */
	@Override
	public void close() {
		// Nothing to release.
	}

}
