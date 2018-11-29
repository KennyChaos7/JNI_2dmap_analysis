package org.k;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by Kenny on 18-11-12.
 * Version : 0.0.1
 * 调用步骤如下：
 * 1,{@link #JNIUtils(String, String)}
 * 2,{@link #ModifyBitmapMapData(Bitmap, int[], int[], int[], byte[])}
 */
final class JNIUtils {
    private int[] color_block = {0,0,0,0};
    private int[] color_cleaned = {0,0,0,0};
    /*
        每次操作完地图数据后, 此处history_id会更新为刚刚解析完后正副地图的history_id列表
     */
    public int[] last_time_history_id_list = new int[100];

    /**
     * 传入色值并在此类中保存
     * @param str_color_block 障碍色值的ARGB十六进制string
     * @param str_color_cleaned 已清扫色值的ARGB十六进制string
     */
    JNIUtils(String str_color_block,String str_color_cleaned)
    {
        this.color_block = parseColor(str_color_block);
        this.color_cleaned = parseColor(str_color_cleaned);
        for (int history_id:last_time_history_id_list)
            history_id = 1;
    }

    /**
     * 自带增量校验
     * @param bitmap 1000x1000, Bitmap.Config.ARGB_8888（即ARGB）
     * @param in 纯地图数据, 已经经过base64解码的
     * @return {{@link #ModifyBitmapMapData(Bitmap, int[], int[], int[], byte[])}}
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
    private native int ModifyBitmapMapData(Bitmap bitmap,int[] last_time_history_id_list,
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
    @Deprecated
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