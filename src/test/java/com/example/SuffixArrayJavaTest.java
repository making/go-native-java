package com.example;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SuffixArrayJavaTest {

	@Test
	void searchQuery() {
		try (SuffixArray suffixArray = new SuffixArrayJava("abracadabra")) {
			assertThat(suffixArray.searchQuery("ra")).containsExactly("ra", "racadabra");
			assertThat(suffixArray.searchQuery("ab")).containsExactly("abra", "abracadabra");
			assertThat(suffixArray.searchQuery("br")).containsExactly("bra", "bracadabra");
		}
	}

}