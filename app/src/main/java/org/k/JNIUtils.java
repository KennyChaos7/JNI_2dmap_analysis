package org.k;

/**
 * Created by Kenny on 18-11-12.
 */
public class JNIUtils {

    static
    {
        System.loadLibrary("toBitmap");
    }

    public native byte[] toBitmapByteArray(byte[] in);
}
