//
// Created by root on 18-11-12.
//
#include "org_k_JNIUtils.h"

#ifdef __cplusplus
extern "C" {
#endif

    /**
     * 返回地图数据长度
     */
JNIEXPORT jint JNICALL
Java_org_k_JNIUtils_ModifyBitmapData(JNIEnv *env, jobject instance, jobject obj_bitmap,
                                     jbyteArray in) {

    AndroidBitmapInfo bitmapInfo = {ANDROID_BITMAP_FORMAT_RGBA_8888};
    if (AndroidBitmap_getInfo(env, obj_bitmap, &bitmapInfo) != ANDROID_BITMAP_RESULT_SUCCESS) {
        return -88;
    }
    LOGI("width = %d , height = %d , format = %d", bitmapInfo.width,bitmapInfo.height,bitmapInfo.format);

    void* point_pixels;
    if (AndroidBitmap_lockPixels(env,obj_bitmap,&point_pixels) != ANDROID_BITMAP_RESULT_SUCCESS)
    {
        return -99;
    }
    K *k = new K();
    k->analysis(env,in,(int32_t*)point_pixels);
    AndroidBitmap_unlockPixels(env, obj_bitmap);

    return 0;
}

uint16_t K::toUINT16(uint8_t u1, uint8_t u2) {
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
void K::ToTYPE(uint8_t bp_in, int *type) {
    type[0] = (bp_in >> 6) & 0x3;
    type[1] = (bp_in >> 4) & 0x3;
    type[2] = (bp_in >> 2) & 0x3;
    type[3] = (bp_in >> 0) & 0x3;
}



int K::map_decompress(jbyte *compress,jbyte *uncompress, int len) {
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


void K::analysis(JNIEnv *env, jbyteArray in,int32_t *point_pixels){
    jbyte *bp_in = env->GetByteArrayElements(in, 0);
    /*
     * 协议相关
     */
    uint8_t map_type = bp_in[0];
    uint8_t nouse = bp_in[1];
    uint8_t update_num = toUINT16(bp_in[2],bp_in[3]);
    int interval = 0;
    int INDEX = 0;
    for (int i = 0; i < 100; ++i) {

        uint16_t block_id = toUINT16(bp_in[4 + interval], bp_in[5 + interval]);
        uint16_t history_id = toUINT16(bp_in[6 + interval], bp_in[7 + interval]);
        uint16_t data_size = toUINT16(bp_in[8 + interval], bp_in[9 + interval]);
        LOGI("i = %d , block_id = %d , history_id = %d , data_size = %d", i, block_id, history_id,
             data_size);

        if (data_size > 0) {
            jbyteArray compress_buf = env->NewByteArray(data_size);
            jbyte *p_compress = env->GetByteArrayElements(compress_buf, 0);
            env->GetByteArrayRegion(in, 10 + interval, data_size, p_compress); // 复制数据

            jbyteArray uncompress_buf = env->NewByteArray(2500);
            jbyte *p_uncompress = env->GetByteArrayElements(uncompress_buf, 0);

            map_decompress(p_compress, p_uncompress, data_size); // 解压数据

            for (int j = 0; j < 2500; ++j) {
                auto *point_type = new int();
                this->ToTYPE((const uint8_t) p_compress[j], point_type);
                for (int q = 0; q < 4; ++q) {
                    if (point_type[q] == TYPE_BLOCK) {
                        int color = 0;
                        int alpha = 0xFF << 24;
                        int red = 0;
                        int green = 0;
                        int blue = 0;
//                        LOGI("point type = %d", point_type[q]);
                        // TODO 现阶段只画第一块 block_id = 1
//                        color = point_pixels[j * q + i * j * q];
                        color = point_pixels[INDEX];
                        red = ((color & 0x00FF0000) >> 16);
                        green = ((color & 0x0000FF00) >> 8);
                        blue = color & 0x000000FF;
                        color = (red + green + blue) / 3;
                        color = alpha | (color << 16) | (color << 8) | color;
//                        point_pixels[j * q + i * j * q] = color;
                        point_pixels[INDEX] = color;
                    }
                    INDEX ++ ;
                }
                free(point_type);
            }

            interval = data_size + 6 + interval;

            env->ReleaseByteArrayElements(compress_buf, p_compress, 0);
            env->ReleaseByteArrayElements(uncompress_buf, p_uncompress, 0);

        }

    }

    env->ReleaseByteArrayElements(in, bp_in, 0);
}


#ifdef __cplusplus
}
#endif