package org.k;

import android.graphics.Bitmap;

/**
 * Created by Kenny on 18-11-12.
 */
final class JNIUtils {
    private static JNIUtils sJNIUtils = null;
    private static int[] color_block = {0,0,0,0};
    public static JNIUtils getInstance()
    {
        synchronized (JNIUtils.class)
        {
            if (sJNIUtils == null)
                sJNIUtils = new JNIUtils();
        }
        return sJNIUtils;
    }

    public int getMapBitmap(Bitmap bitmap, byte[] in)
    {
        if (bitmap != null && in != null)
        {
            return this.ModifyBitmapMapData(bitmap,in);
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

    public int cleanWholeBitmap(Bitmap bitmap)
    {
        if (bitmap != null) {
            return this.cleanBitmap(bitmap,0,0,999,999);
        }
        else
            return -1;
    }

    public int cleanPartBitmap(Bitmap bitmap,int start_x,int start_y,int end_x,int end_y)
    {
        if (bitmap != null) {
            return this.cleanBitmap(bitmap,start_x,start_y,end_x,end_y);
        }
        else
            return -1;
    }

    static
    {
        System.loadLibrary("toBitmap");
    }

    /*
     * 全量地图
     * @param bitmap 传入bitmap对象, 该对象需在java层创建完毕
     * @param in 传入byte[]数组的地图信息
     * @return 错误码
     * -88 是指bitmap的文件头信息无法获取
     * -99 是指无法锁定bitmap的像素指针
     */
    private native int ModifyBitmapMapData(Bitmap bitmap, byte[] in);

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
