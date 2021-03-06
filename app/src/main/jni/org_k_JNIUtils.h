#include <jni.h>
#include <android/log.h>
#include <string.h>
#include <cstdio>
#include <android/bitmap.h>
#include <cstdlib>


#ifdef __cplusplus
extern "C" {
#endif

#define TAG "toBitmap" // 这个是自定义的LOG的标识
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__) // 定义LOGD类型
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG ,__VA_ARGS__) // 定义LOGI类型
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,TAG ,__VA_ARGS__) // 定义LOGW类型
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG ,__VA_ARGS__) // 定义LOGE类型
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,TAG ,__VA_ARGS__) // 定义LOGF类型
#define BigtoLittle16(A) ((((uint16_t)(A) & 0xff00) >> 8) | (((uint16_t)(A) & 0x00ff) << 8))

typedef struct {
    uint8_t alpha;
    uint8_t red;
    uint8_t green;
    uint8_t blue;
}ARGB;
void isException(JNIEnv* env,int errorCode);

JNIEXPORT jint JNICALL
Java_org_k_JNIUtils_ModifyBitmapMapData(JNIEnv *env, jobject instance, jobject obj_bitmap,
                                     jintArray last_time_history_id_list,jintArray color_block,jintArray color_cleaned,jbyteArray in);

JNIEXPORT jint JNICALL
Java_org_k_JNIUtils_ModifyBitmapTrackData(JNIEnv *env, jobject instance, jobject obj_bitmap,
                                          jbyteArray in);

JNIEXPORT jint JNICALL
Java_org_k_JNIUtils_updateTrack(JNIEnv *env, jobject instance, jobject bitmap, jbyteArray o,
                                jbyteArray n);

JNIEXPORT jint JNICALL
Java_org_k_JNIUtils_updateMap(JNIEnv *env, jobject instance, jobject bitmap, jbyteArray o,
                              jbyteArray n);

JNIEXPORT jint JNICALL
Java_org_k_JNIUtils_cleanBitmap(JNIEnv *env, jobject instance, jobject obj_bitmap, jint start_x,
        jint start_y, jint end_x, jint end_y);

class K {
public:
    uint8_t Track_data_bits_2 = 2;
    uint8_t Track_data_bits_4 = 4;
    uint8_t TYPE_BLOCK = 1;
    uint8_t TYPE_EMPTY = 0;
    uint8_t TYPE_UNUSE = 3;
    uint8_t TYPE_CLEANED = 2;
    jbyteArray last_history_id = NULL;
    int multiple = 1;

    void ToTYPE(jbyte bp_in, int *type);
    void analysisMap(JNIEnv *env,  jbyteArray in,jint *point_history_id_list,int32_t *point_pixels,jint* argb_block,jint* argb_cleaned);
    void analysisTrack(JNIEnv* env, jbyteArray in, int32_t *point_pixels);
    uint16_t toUint16(uint8_t u1, uint8_t u2);
    int map_decompress(jbyte *compress,jbyte *uncompress, int len);
    void drawPoint(int32_t *point_pixels, int index,int alpha,int red,int green,int blue);
    void drawLine(int32_t *point_pixels,int before_x,int before_y,int x,int y,int alpha,int red,int green,int blue);
    void clean(int32_t *point_pixels,int before_x,int before_y,int x,int y);
};

#ifdef __cplusplus
}
#endif
