package org.k;

import android.graphics.Bitmap;

/**
 * Created by Kenny on 18-11-12.
 */
public class JNIUtils {

    static
    {
        System.loadLibrary("toBitmap");
    }

    /**
     * 全量地图和轨迹
     * @param bitmap 传入bitmap对象, 该对象需在java层创建完毕
     * @param in 传入byte[]数组的地图信息
     * @return 错误码
     * -88 是指bitmap的文件头信息无法获取
     * -99 是指无法锁定bitmap的像素指针
     */
    public native int ModifyBitmapData(Bitmap bitmap, byte[] in);

    /**
     * 增量地图
     * @param bitmap 传入bitmap对象, 该对象需在java层创建完毕或为上次操作后存下来的对象
     * @param o old旧数据
     * @param n new新数据
     * @return
     */
    public native int updateMap(Bitmap bitmap,byte[] o,byte[] n);

    /**
     * 增量轨迹
     * @param bitmap 传入bitmap对象, 该对象需在java层创建完毕或为上次操作后存下来的对象
     * @param o old旧数据
     * @param n new新数据
     * @return
     */
    public native int updateTrack(Bitmap bitmap,byte[] o,byte[] n);
}
