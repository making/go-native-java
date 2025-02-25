#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>
#include "libsuffixarray.h"

// search_and_print: Invokes SearchSuffixArray using the given handle and prints the results.
void search_and_print(const char *query, const char *input, SuffixArrayHandle *handle) {
    // SearchSuffixArray expects a non-const char* for query.
    IntArray *result = SearchSuffixArray(handle, (char *)query, (int)strlen(query));
    printf("begin from '%s': ", query);
    if (result && result->length > 0 && result->data) {
        int *indices = result->data;
        for (int i = 0; i < result->length; i++) {
            int idx = indices[i];
            // Print the substring of input starting at the found index.
            printf("%s ", input + idx);
        }
    }
    printf("\n");
    FreeIntArray(result);
}

int main(void) {
    char *input = "abracadabra";
    SuffixArrayHandle *handle = CreateSuffixArray(input, (int)strlen(input));

    search_and_print("ra", input, handle);
    search_and_print("ab", input, handle);
    search_and_print("br", input, handle);

    FreeSuffixArray(handle);
    return 0;
}
