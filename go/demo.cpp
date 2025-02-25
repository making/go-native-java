#include <iostream>
#include <vector>
#include <string>
#include <stdexcept>
#include "libsuffixarray.h"

namespace demo {

class SuffixArray {
public:
    // Constructor: creates the suffix array from the given input string.
    SuffixArray(const std::string& input) : input_(input) {
        // Create suffix array using the input string.
        // We pass a mutable pointer to the C-string.
        handle_ = (SuffixArrayHandle*)CreateSuffixArray(const_cast<char*>(input.c_str()),
                                                          static_cast<int>(input.size()));
        if (!handle_) {
            throw std::runtime_error("Failed to create suffix array");
        }
    }

    // searchQuery: Searches for the query in the suffix array and returns a list of substrings.
    std::vector<std::string> searchQuery(const std::string& query) {
        std::vector<std::string> results;
        // Call SearchSuffixArray with the query string.
        IntArray* result = SearchSuffixArray(handle_,
                                             const_cast<char*>(query.c_str()),
                                             static_cast<int>(query.size()));
        if (result && result->length > 0 && result->data) {
            // The result->data array contains indices into the input string.
            for (int i = 0; i < result->length; i++) {
                int idx = result->data[i];
                if (idx >= 0 && idx < static_cast<int>(input_.size())) {
                    results.push_back(input_.substr(idx));
                }
            }
        }
        // Free the result structure.
        FreeIntArray(result);
        return results;
    }

    // Destructor: frees the allocated suffix array.
    ~SuffixArray() {
        if (handle_) {
            FreeSuffixArray(handle_);
            handle_ = nullptr;
        }
    }

    // Delete copy constructor and assignment.
    SuffixArray(const SuffixArray&) = delete;
    SuffixArray& operator=(const SuffixArray&) = delete;

private:
    std::string input_;
    SuffixArrayHandle* handle_;
};

} // namespace demo

// Helper function: perform search and print the results.
void printResults(demo::SuffixArray& sa, const std::string& query) {
    std::vector<std::string> results = sa.searchQuery(query);
    std::cout << "begin from '" << query << "': ";
    for (const auto& str : results) {
        std::cout << str << " ";
    }
    std::cout << std::endl;
}

int main() {
    try {
        std::string input = "abracadabra";
        demo::SuffixArray sa(input);

        printResults(sa, "ra");
        printResults(sa, "ab");
        printResults(sa, "br");
    } catch (const std::exception& ex) {
        std::cerr << "Error: " << ex.what() << std::endl;
        return 1;
    }
    return 0;
}
