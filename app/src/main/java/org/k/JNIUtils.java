package org.k;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import org.k.jni_2dmap_analysis.BuildConfig;

/**
 * Created by Kenny on 18-11-12.
 */
final class JNIUtils {
    private int[] color_block = {0,0,0,0};
    private int[] color_cleaned = {0,0,0,0};
    private byte[] last_time_history_id_list = new byte[100];

    JNIUtils(String str_color_block,String str_color_cleaned)
    {
        this.color_block = parseColor(str_color_block);
        this.color_cleaned = parseColor(str_color_cleaned);
        for (byte b:last_time_history_id_list)
            b = 1;
    }

    /**
     * 自带增量校验
     * @param bitmap 1000x1000, Bitmap.Config.ARGB_8888（即ARGB）
     * @param in 纯地图数据, 已经经过base64解码的
     * @return {{@link #ModifyBitmapMapData(Bitmap, byte[], int[], int[], byte[])}}
     */
    public int getMapBitmap(Bitmap bitmap, byte[] in)
    {
        if (bitmap != null && in != null)
        {
            return this.ModifyBitmapMapData(bitmap,last_time_history_id_list,color_block,color_cleaned,in);
        }
        else
            return -1;
    }

    public int getTrackBitmap(Bitmap bitmap, byte[] in)
    {

        if (bitmap != null && in != null)
        {
            return this.ModifyBitmapTrackData(bitmap,in);
        }
        else
            return -1;
    }


    /**
     * 清除Bitmap中的时候全部像素
     * @param bitmap
     * @return
     */
    @Deprecated
    public int cleanWholeBitmap(Bitmap bitmap)
    {
        if (bitmap != null) {
            return this.cleanBitmap(bitmap,0,0,999,999);
        }
        else
            return -1;
    }

    @Deprecated
    public int cleanPartBitmap(Bitmap bitmap,int start_x,int start_y,int end_x,int end_y)
    {
        if (bitmap != null) {
            return this.cleanBitmap(bitmap,start_x,start_y,end_x,end_y);
        }
        else
            return -1;
    }

    private int[] parseColor(String str_color)
    {
         int _color = Color.parseColor(str_color);
         return new int[]{Color.alpha(_color),Color.red(_color),Color.green(_color),Color.blue(_color)};
    }

    static
    {
        System.loadLibrary("toBitmap");
    }

    /*
     * 全量地图
     * @param bitmap 传入bitmap对象, 该对象需在java层创建完毕
     * @param in 传入byte[]数组的地图信息
     * @param last_time_history_id_list 上次解析的话得出的历史id
     * @return 错误码
     * -88 是指bitmap的文件头信息无法获取
     * -99 是指无法锁定bitmap的像素指针
     */
    private native int ModifyBitmapMapData(Bitmap bitmap,byte[] last_time_history_id_list,
                                           int[] color_block,int[] color_cleaned, byte[] in);

    /*
     * 全量轨迹
     * @param bitmap 传入bitmap对象, 该对象需在java层创建完毕
     * @param in 传入byte[]数组的地图信息
     * @return 错误码
     * -88 是指bitmap的文件头信息无法获取
     * -99 是指无法锁定bitmap的像素指针
     */
    private native int ModifyBitmapTrackData(Bitmap bitmap, byte[] in);


    /**
     * 清除指定x,y区间内的Bitmap像素
     * x,y的范围到 0～999
     * @param bitmap
     * @param start_x
     * @param start_y
     * @param end_x
     * @param end_y
     * @return
     */
    private native int cleanBitmap(Bitmap bitmap,int start_x,int start_y,int end_x,int end_y);

    /**
     * 增量地图
     * @param bitmap 传入bitmap对象, 该对象需在java层创建完毕或为上次操作后存下来的对象
     * @param o old旧数据
     * @param n new新数据
     * @return
     */
    @Deprecated
    private native int updateMap(Bitmap bitmap,byte[] o,byte[] n);

    /**
     * 增量轨迹
     * @param bitmap 传入bitmap对象, 该对象需在java层创建完毕或为上次操作后存下来的对象
     * @param o old旧数据
     * @param n new新数据
     * @return
     */
    @Deprecated
    private native int updateTrack(Bitmap bitmap,byte[] o,byte[] n);

}
