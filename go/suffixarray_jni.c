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

// nativeSearch: calls SearchSuffixArray and returns a jintArray of matching indices.
JNIEXPORT jintArray JNICALL Java_com_example_SuffixArrayJNI_nativeSearch
  (JNIEnv *env, jobject obj, jlong handle, jstring query) {
    const char *cQuery = (*env)->GetStringUTFChars(env, query, NULL);
    if (cQuery == NULL) {
        return NULL;
    }
    // Call SearchSuffixArray with the query.
    IntArray* result = SearchSuffixArray((SuffixArrayHandle*)handle, (char*)cQuery, (int)strlen(cQuery));
    (*env)->ReleaseStringUTFChars(env, query, cQuery);
    if (result == NULL) {
        return NULL;
    }
    int len = result->length;
    jintArray ret = (*env)->NewIntArray(env, len);
    if (ret == NULL) {
        FreeIntArray(result);
        return NULL;
    }
    // Copy indices from result->data into a temporary jint array.
    int *indices = result->data;
    jint *temp = (jint*)malloc(sizeof(jint) * len);
    for (int i = 0; i < len; i++) {
        temp[i] = indices[i];
    }
    (*env)->SetIntArrayRegion(env, ret, 0, len, temp);
    free(temp);
    FreeIntArray(result);
    return ret;
}

// nativeClose: calls FreeSuffixArray to free the native suffix array handle.
JNIEXPORT void JNICALL Java_com_example_SuffixArrayJNI_nativeClose
  (JNIEnv *env, jobject obj, jlong handle) {
    FreeSuffixArray((SuffixArrayHandle*)handle);
}
