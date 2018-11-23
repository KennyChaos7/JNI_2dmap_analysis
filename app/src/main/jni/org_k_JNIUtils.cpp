//
// Created by root on 18-11-12.
//

//#define DEBUG
#include <android/bitmap.h>
#include "org_k_JNIUtils.h"

#ifdef __cplusplus
extern "C" {
#endif


JNIEXPORT jint JNICALL
Java_org_k_JNIUtils_ModifyBitmapMapData(JNIEnv *env, jobject instance, jobject obj_bitmap,
                                        jbyteArray last_time_history_id_list,jintArray color_block,jintArray color_cleaned,jbyteArray in) {

    AndroidBitmapInfo bitmapInfo = {ANDROID_BITMAP_FORMAT_RGBA_8888};
    if (AndroidBitmap_getInfo(env, obj_bitmap, &bitmapInfo) != ANDROID_BITMAP_RESULT_SUCCESS) {
        return -88;
    }
#ifdef DEBUG
    LOGI("width = %d , height = %d , format = %d", bitmapInfo.width, bitmapInfo.height,
         bitmapInfo.format);
#endif
    void *point_pixels;
    if (AndroidBitmap_lockPixels(env, obj_bitmap, &point_pixels) != ANDROID_BITMAP_RESULT_SUCCESS) {
        return -99;
    }
    jint *ip_block = env->GetIntArrayElements(color_block,0);
    jint *ip_clean = env->GetIntArrayElements(color_cleaned,0);
    K *k = new K();
    k->multiple = bitmapInfo.width / 1000;
    k->last_history_id = last_time_history_id_list;
    k->analysisMap(env, in, (int32_t *) point_pixels,ip_block,ip_clean);
    AndroidBitmap_unlockPixels(env, obj_bitmap);
    last_time_history_id_list = k->last_history_id;
    delete(k);
    env->ReleaseIntArrayElements(color_block,ip_block,0);
    env->ReleaseIntArrayElements(color_cleaned,ip_clean,0);
    return 0;
}

JNIEXPORT jint JNICALL
Java_org_k_JNIUtils_ModifyBitmapTrackData(JNIEnv *env, jobject instance, jobject obj_bitmap,
                                        jbyteArray in) {

    AndroidBitmapInfo bitmapInfo = {ANDROID_BITMAP_FORMAT_RGBA_8888};
    if (AndroidBitmap_getInfo(env, obj_bitmap, &bitmapInfo) != ANDROID_BITMAP_RESULT_SUCCESS) {
        return -88;
    }
#ifdef DEBUG
    LOGI("width = %d , height = %d , format = %d", bitmapInfo.width, bitmapInfo.height,
         bitmapInfo.format);
#endif
    void *point_pixels;
    if (AndroidBitmap_lockPixels(env, obj_bitmap, &point_pixels) != ANDROID_BITMAP_RESULT_SUCCESS) {
        return -99;
    }
    K *k = new K();
    k->analysisTrack(env, in, (int32_t *) point_pixels);
    AndroidBitmap_unlockPixels(env, obj_bitmap);
    delete (k);
    return 0;
}

JNIEXPORT jint JNICALL
Java_org_k_JNIUtils_updateTrack(JNIEnv *env, jobject instance, jobject bitmap, jbyteArray o,
                                jbyteArray n) {
    return 0;
}

JNIEXPORT jint JNICALL
Java_org_k_JNIUtils_updateMap(JNIEnv *env, jobject instance, jobject bitmap, jbyteArray o,
                              jbyteArray n) {
    return 0;
}


JNIEXPORT jint JNICALL
Java_org_k_JNIUtils_cleanBitmap(JNIEnv *env, jobject instance, jobject obj_bitmap, jint start_x,
                                jint start_y, jint end_x, jint end_y) {

    AndroidBitmapInfo bitmapInfo = {ANDROID_BITMAP_FORMAT_RGBA_8888};
    if (AndroidBitmap_getInfo(env, obj_bitmap, &bitmapInfo) != ANDROID_BITMAP_RESULT_SUCCESS) {
        return -88;
    }
#ifdef DEBUG
    LOGI("width = %d , height = %d , format = %d", bitmapInfo.width, bitmapInfo.height,
         bitmapInfo.format);
#endif
    void *point_pixels;
    if (AndroidBitmap_lockPixels(env, obj_bitmap, &point_pixels) != ANDROID_BITMAP_RESULT_SUCCESS) {
        return -99;
    }

    return 0;
}

uint16_t K::toUint16(uint8_t u1, uint8_t u2) {
    return u1 + (u2 << 8);
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
void K::ToTYPE(jbyte bp_in, int *type) {
    type[0] = (bp_in >> 6) & 0x3;
    type[1] = (bp_in >> 4) & 0x3;
    type[2] = (bp_in >> 2) & 0x3;
    type[3] = (bp_in >> 0) & 0x3;
}

void K::drawPoint(int32_t *point_pixels, int index, int alpha, int red, int green, int blue) {
    point_pixels[index] = alpha << 24 | blue << 16 | green << 8 | red ;
}

void K::drawLine(int32_t *point_pixels, int x1,int y1,int x2,int y2, int alpha, int red, int green,
                 int blue) {
    if (y1 != -1 && x1 != -1) {
        // TODO 计算两个点之间的间隔的所有点的坐标
        // URL https://www.cnblogs.com/pheye/archive/2010/08/14/1799803.html
        int dx = x2 - x1;
        int dy = y2 - y1;
        int ux = ((dx > 0) << 1) - 1;//x的增量方向，取或-1
        int uy = ((dy > 0) << 1) - 1;//y的增量方向，取或-1
        int x = x1, y = y1, eps;//eps为累加误差

        eps = 0;
        dx = abs(dx);
        dy = abs(dy);
        if (dx > dy) {
            for (x = x1; x != x2; x += ux) {
                drawPoint(point_pixels, x + y * 1000 * multiple, alpha, red, green, blue);
                eps += dy;
                if ((eps << 1) >= dx) {
                    y += uy;
                    eps -= dx;
                }
            }
        } else {
            for (y = y1; y != y2; y += uy) {
                drawPoint(point_pixels, x + y * 1000 * multiple, alpha, red, green, blue);
                eps += dx;
                if ((eps << 1) >= dy) {
                    x += ux;
                    eps -= dy;
                }
            }
        }
    }
}

int K::map_decompress(jbyte *compress, jbyte *uncompress, int len) {
    int i = 0;
    int repeat_len = 0;
    int index = 0;
    for (i = 0; i < len; i++) {
        if ((compress[i] & (0x3 << 6)) == (0x3 << 6)) {
            repeat_len <<= 6;
            repeat_len += compress[i] & 0x3F;
        } else {
            if (repeat_len) {
                int j = 0;
                for (j = 0; j < repeat_len; j++) {
                    uncompress[index++] = compress[i];
                }
                repeat_len = 0;
            } else {
                uncompress[index++] = compress[i];
            }
        }
    }
    return index;
}


void K::analysisMap(JNIEnv *env, jbyteArray in, int32_t *point_pixels,jint* argb_block,jint* argb_cleaned) {
    jbyte *bp_in = env->GetByteArrayElements(in, 0);
    jbyte *bp_last_histort_id = env->GetByteArrayElements(last_history_id,0);
    /*
     * 协议相关
     */
    uint8_t map_type = bp_in[0];
    uint8_t nouse = bp_in[1];
    uint8_t update_num = toUint16(bp_in[2], bp_in[3]);
    int interval = 0;
    int x_begin = 0;
    int y_begin = 0;
    int alpha = 0;
    int red = 0;
    int green = 0;
    int blue = 0;
    uint16_t block_id = 0;
    uint16_t history_id = 0;
    uint16_t data_size = 0;

    for (int i = 0; i < 100; ++i) {

        block_id = toUint16(bp_in[4 + interval], bp_in[5 + interval]);
        history_id = toUint16(bp_in[6 + interval], bp_in[7 + interval]);
        data_size = toUint16(bp_in[8 + interval], bp_in[9 + interval]);

        if (block_id < 1 || block_id > 100) {
            LOGI("JNI -- block id is error");
            break;
        }
#ifdef DEBUG
            LOGI("i = %d , block_id = %d , history_id = %d , data_size = %d", i, block_id, history_id,
                 data_size);
             LOGI("history_id = %d, bp_last_histort_id = %d ,i = %d",history_id,bp_last_histort_id[i],i);
#endif
        if (data_size > 0 && bp_last_histort_id[i] < history_id) {

            bp_last_histort_id[i] = history_id;
            x_begin = (block_id - 1) % 10 * 100;
            y_begin = (block_id - 1) / 10 * 100;
#ifdef DEBUG
            LOGI("x_begin = %d, y_begin = %d, index = %d", x_begin, y_begin,
                     x_begin + y_begin * 100);
#endif
            jbyteArray compress_buf = env->NewByteArray(data_size);
            jbyte *p_compress = env->GetByteArrayElements(compress_buf, 0);
            env->GetByteArrayRegion(in, 10 + interval, data_size, p_compress); // 复制数据

            jbyteArray uncompress_buf = env->NewByteArray(2500);
            jbyte *p_uncompress = env->GetByteArrayElements(uncompress_buf, 0);

            map_decompress(p_compress, p_uncompress, data_size); // 解压数据
            env->ReleaseByteArrayElements(compress_buf, p_compress, 0);
            env->DeleteLocalRef(compress_buf);

            int x = 0;
            int y = 0;
            for (int j = 0; j < 2500; ++j) {
                if (j > 0 && j % 25 == 0) {
                    y++;
                    x = 0;
                }
                jintArray pointTypes = env->NewIntArray(4);
                jint *point_type = env->GetIntArrayElements(pointTypes, 0);
                this->ToTYPE(p_uncompress[j], point_type);
                for (int q = 0; q < 4; ++q) {
#ifdef DEBUG
                    LOGI("j = %d, point_type[q] = %d", j, point_type[q]);
#endif
                    if (point_type[q] == TYPE_BLOCK) {
#ifdef DEBUG
                        LOGI("block -- block_id = %d , x = %d, y = %d", block_id, x + x_begin,
                                 (y + y_begin));
                            LOGI("block -- alpha = %d , red = %d , green = %d , blue = %d",argb_block[0],argb_block[1],argb_block[2],argb_block[3]);
#endif
                        alpha = argb_block[0];
                        red =  argb_block[1];
                        green =  argb_block[2];
                        blue =  argb_block[3];
                        drawPoint(point_pixels, x + x_begin + (y + y_begin) * 1000 * multiple, alpha, red,
                                  green, blue);
                        if (multiple == 2)
                        {
                            drawPoint(point_pixels, x + x_begin + 1 + (y + y_begin) * 1000 * multiple, alpha, red,
                                      green, blue);
                            drawPoint(point_pixels, x + x_begin + (y + y_begin + 1) * 1000 * multiple, alpha, red,
                                      green, blue);
                            drawPoint(point_pixels, x + x_begin + 1 + (y + y_begin + 1) * 1000 * multiple, alpha, red,
                                      green, blue);
                        } else if (multiple == 3)
                        {
                            drawPoint(point_pixels, x + x_begin + 1 + (y + y_begin) * 1000 * multiple, alpha, red,
                                      green, blue);
                            drawPoint(point_pixels, x + x_begin + 2 + (y + y_begin) * 1000 * multiple, alpha, red,
                                      green, blue);
                            drawPoint(point_pixels, x + x_begin + 1 + (y + y_begin + 1) * 1000 * multiple, alpha, red,
                                      green, blue);
                            drawPoint(point_pixels, x + x_begin + 2 + (y + y_begin + 1) * 1000 * multiple, alpha, red,
                                      green, blue);
                            drawPoint(point_pixels, x + x_begin + 1 + (y + y_begin + 2) * 1000 * multiple, alpha, red,
                                      green, blue);
                            drawPoint(point_pixels, x + x_begin + 2 + (y + y_begin + 2) * 1000 * multiple, alpha, red,
                                      green, blue);
                            drawPoint(point_pixels, x + x_begin + (y + y_begin + 2) * 1000 * multiple, alpha, red,
                                      green, blue);
                            drawPoint(point_pixels, x + x_begin + (y + y_begin + 2) * 1000 * multiple, alpha, red,
                                      green, blue);

                        }
                    } else if (point_type[q] == TYPE_CLEANED) {
#ifdef DEBUG
                        LOGI("cleaned -- block_id = %d , x = %d, y = %d", block_id, x + x_begin,
                                 (y + y_begin));
                            LOGI("cleaned -- alpha = %d , red = %d , green = %d , blue = %d",argb_cleaned[0],argb_cleaned[1],argb_cleaned[2],argb_cleaned[3]);
#endif
                        alpha = argb_cleaned[0];
                        red = argb_cleaned[1];
                        green = argb_cleaned[2];
                        blue = argb_cleaned[3];
                            drawPoint(point_pixels, x + x_begin + (y + y_begin) * 1000 * multiple, alpha, red,
                                      green, blue);
                            if (multiple == 2)
                            {
                                drawPoint(point_pixels, x + x_begin + 1 + (y + y_begin) * 1000 * multiple, alpha, red,
                                          green, blue);
                                drawPoint(point_pixels, x + x_begin + (y + y_begin + 1) * 1000 * multiple, alpha, red,
                                          green, blue);
                                drawPoint(point_pixels, x + x_begin + 1 + (y + y_begin + 1) * 1000 * multiple, alpha, red,
                                          green, blue);
                            } else if (multiple == 3)
                            {
                                drawPoint(point_pixels, x + x_begin + 1 + (y + y_begin) * 1000 * multiple, alpha, red,
                                          green, blue);
                                drawPoint(point_pixels, x + x_begin + 2 + (y + y_begin) * 1000 * multiple, alpha, red,
                                          green, blue);
                                drawPoint(point_pixels, x + x_begin + 1 + (y + y_begin + 1) * 1000 * multiple, alpha, red,
                                          green, blue);
                                drawPoint(point_pixels, x + x_begin + 2 + (y + y_begin + 1) * 1000 * multiple, alpha, red,
                                          green, blue);
                                drawPoint(point_pixels, x + x_begin + 1 + (y + y_begin + 2) * 1000 * multiple, alpha, red,
                                          green, blue);
                                drawPoint(point_pixels, x + x_begin + 2 + (y + y_begin + 2) * 1000 * multiple, alpha, red,
                                          green, blue);
                                drawPoint(point_pixels, x + x_begin + (y + y_begin + 2) * 1000 * multiple, alpha, red,
                                          green, blue);
                                drawPoint(point_pixels, x + x_begin + (y + y_begin + 2) * 1000 * multiple, alpha, red,
                                          green, blue);
                            }
                    }
                    x++;
                }
                env->ReleaseIntArrayElements(pointTypes, point_type, 0);
                env->DeleteLocalRef(pointTypes);
            }

            env->ReleaseByteArrayElements(uncompress_buf, p_uncompress, 0);
            env->DeleteLocalRef(uncompress_buf);

        }
        interval = data_size + 6 + interval;
    }
    env->ReleaseByteArrayElements(last_history_id,bp_last_histort_id,0);
    env->ReleaseByteArrayElements(in, bp_in, 0);
}

void K::analysisTrack(JNIEnv *env, jbyteArray in, int32_t *point_pixels) {
    jbyte *bp_in = env->GetByteArrayElements(in, 0);

    uint8_t track_type = bp_in[0];
    uint8_t track_bits = bp_in[1];
    uint16_t track_clean_area = toUint16(bp_in[2], bp_in[3]);
    uint16_t track_begin_index = toUint16(bp_in[4], bp_in[5]);
    uint16_t track_end_index = toUint16(bp_in[6], bp_in[7]);
    jsize track_data_size = env->GetArrayLength(in) - 8;
#ifdef DEBUG
    LOGI("clean area = %d , begin_index = %d , end_index = %d",track_clean_area,track_begin_index,track_end_index);
#endif
    // 该解析只解析当2个byte代表一个x或y的情况
    uint16_t x, y;
    int alpha = 255;
    int red = 255;
    int green = 255;
    int blue = 255;
    int __before_x = -1;
    int __before_y = -1;
    for (int i = 0; i + 3 < track_data_size; i += 4) {
        x = toUint16(bp_in[8 + i], bp_in[8 + i + 1]);
        y = toUint16(bp_in[8 + i + 2], bp_in[8 + i + 3]);
        // TODO drawLine
        red = 0;
        drawPoint(point_pixels, x + y * 1000 * multiple, alpha, red, green, blue);
        red = 125;
        drawLine(point_pixels, __before_x, __before_y, x, y, alpha, red, green, blue);
        __before_x = x;
        __before_y = y;
#ifdef DEBUG
        LOGI("track - x = %d , y = %d",x,y);
#endif
    }
    env->ReleaseByteArrayElements(in, bp_in, 0);
}

#ifdef __cplusplus
}
#endif