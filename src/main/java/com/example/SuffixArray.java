package com.example;

import java.util.List;

public interface SuffixArray extends AutoCloseable {

	List<String> searchQuery(String query);

	void close();

}
