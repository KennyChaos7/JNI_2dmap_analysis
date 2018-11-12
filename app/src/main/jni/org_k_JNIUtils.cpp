//
// Created by root on 18-11-12.
//
#include <cstdio>
#include "org_k_JNIUtils.h"

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jbyteArray JNICALL
Java_org_k_JNIUtils_toBitmapByteArray(JNIEnv *env, jobject instance, jbyteArray in) {

    LOGD("%d",(int)sizeof(in));
    jbyteArray out = env->NewByteArray(sizeof(in) * 4);
    jbyte* __out = env->GetByteArrayElements(out,0);
    jbyte* __in = env->GetByteArrayElements(in,0);

    // TODO
    Analysis* analysis = new Analysis();
    analysis->out_byteArray(__in,__out);

    env->ReleaseByteArrayElements(in, __in, 0);
    env->ReleaseByteArrayElements(out, __out, 0);

    return out;
}


void Analysis::out_byteArray(jbyte *in, jbyte *out) {
    out[0] = in[1];
    out[1] = in[1];
    out[2] = in[2];
    out[3] = in[3];
}

/**
    *
    * @param in
    * @param type
    * type : 0 => 空白
    * type : 1 => 障碍
    * type : 2 => 已清扫
    * type : 3 => 空白
    */
void Analysis::ToTYPE(jbyte in, int *type) {
    type[0] = (in >> 6) & 0x3;
    type[1] = (in >> 4) & 0x3;
    type[2] = (in >> 2) & 0x3;
    type[3] = (in >> 0) & 0x3;
}

#ifdef __cplusplus
}
#endif