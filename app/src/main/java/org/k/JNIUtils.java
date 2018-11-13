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

    public native int ModifyBitmapData(Bitmap bitmap, byte[] in);
}
