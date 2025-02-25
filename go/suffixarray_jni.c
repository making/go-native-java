#include <jni.h>
#include <stdlib.h>
#include <string.h>
#include "libsuffixarray.h"

// nativeCreate: calls CreateSuffixArray from the Go library.
JNIEXPORT jlong JNICALL Java_com_example_SuffixArrayJNI_nativeCreate
  (JNIEnv *env, jobject obj, jstring input) {
    const char *cInput = (*env)->GetStringUTFChars(env, input, NULL);
    if (cInput == NULL) {
        return 0; // OutOfMemoryError already thrown
    }
    SuffixArrayHandle* handle = CreateSuffixArray((char*)cInput, (int)strlen(cInput));
    (*env)->ReleaseStringUTFChars(env, input, cInput);
    return (jlong)handle;
}

// nativeSearch: calls SearchSuffixArray and returns an array of Java strings,
// where each element is the suffix of the input starting at the index returned by the search.
JNIEXPORT jobjectArray JNICALL Java_com_example_SuffixArrayJNI_nativeSearch
  (JNIEnv *env, jobject obj, jlong handle, jstring query, jstring input) {
    const char *cQuery = (*env)->GetStringUTFChars(env, query, NULL);
    if (cQuery == NULL) {
        return NULL;
    }
    IntArray* result = SearchSuffixArray((SuffixArrayHandle*)handle, (char*)cQuery, (int)strlen(cQuery));
    (*env)->ReleaseStringUTFChars(env, query, cQuery);
    if (result == NULL) {
        return NULL;
    }
    int len = result->length;

    // Get the original input string as a C string.
    const char *cInput = (*env)->GetStringUTFChars(env, input, NULL);
    if (cInput == NULL) {
        FreeIntArray(result);
        return NULL;
    }

    // Allocate a Java String array for the result.
    jclass stringClass = (*env)->FindClass(env, "java/lang/String");
    jobjectArray ret = (*env)->NewObjectArray(env, len, stringClass, NULL);

    // For each index in result->data, create a Java string using the substring of cInput.
    int *indices = result->data;
    for (int i = 0; i < len; i++) {
        int idx = indices[i];
        // Create a Java string from cInput starting at idx.
        jstring jstr = (*env)->NewStringUTF(env, cInput + idx);
        (*env)->SetObjectArrayElement(env, ret, i, jstr);
    }

    (*env)->ReleaseStringUTFChars(env, input, cInput);
    FreeIntArray(result);
    return ret;
}

// nativeClose: calls FreeSuffixArray to free the native suffix array handle.
JNIEXPORT void JNICALL Java_com_example_SuffixArrayJNI_nativeClose
  (JNIEnv *env, jobject obj, jlong handle) {
    FreeSuffixArray((SuffixArrayHandle*)handle);
}
