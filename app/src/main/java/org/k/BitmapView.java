package org.k;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
/**
 * Created by Kenny on 18-11-12.
 */
public class BitmapView extends View {
    private Bitmap mBitmap = null;
    private Paint mPaint = new Paint();

    public BitmapView(Context context) {
        super(context);
    }

    public BitmapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BitmapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BitmapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void addByteArrayToBitmap(byte[] bytes){
//        BitmapFactory.Options options = new BitmapFactory.Options();
        mBitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        if (mBitmap != null)
            canvas.drawBitmap(mBitmap,0,0,mPaint);
    }
}
