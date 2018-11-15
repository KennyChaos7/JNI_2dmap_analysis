package org.k;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
/**
 * Created by Kenny on 18-11-12.
 */
public class BitmapView extends View implements View.OnTouchListener, GestureDetector.OnDoubleTapListener, GestureDetector.OnGestureListener, ScaleGestureDetector.OnScaleGestureListener {
    private Bitmap mBitmap = null;
    private Paint mPaint = new Paint();

    private final float MAX_ZOOM_SCALE = 7f;                            //放大的比例
    private final float MIN_NARROW_SCALE = .5f;                         //缩小的比例
    public Matrix matrix_translate = new Matrix();                             //偏移矩阵
    public Matrix matrix_scale = new Matrix();                                 //缩放矩阵
    private GestureDetector gestureDetector = null;                     //检测单手指手势类型
    private ScaleGestureDetector scaleGestureDetector = null;           //检测双手指手势类型
    private float scale_after_matrix = 1.0f;                            //矩阵得出的比列

    public BitmapView(Context context) {
        super(context);
        setOnTouchListener(this);
        gestureDetector = new GestureDetector(context, this);
        scaleGestureDetector = new ScaleGestureDetector(context, this);
    }

    public BitmapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
        gestureDetector = new GestureDetector(context, this);
        scaleGestureDetector = new ScaleGestureDetector(context, this);
    }

    public BitmapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnTouchListener(this);
        gestureDetector = new GestureDetector(context, this);
        scaleGestureDetector = new ScaleGestureDetector(context, this);
    }

    public BitmapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setOnTouchListener(this);
        gestureDetector = new GestureDetector(context, this);
        scaleGestureDetector = new ScaleGestureDetector(context, this);
    }

    public void addBitmap(Bitmap bitmap){
        if (bitmap != null) {
            mBitmap = bitmap;
            postInvalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.concat(matrix_translate);
        canvas.concat(matrix_scale);
        if (mBitmap != null)
            canvas.drawBitmap(mBitmap,0,0,mPaint);
        super.onDraw(canvas);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        matrix_translate.reset();
        matrix_scale.reset();
        postInvalidate();
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent event_before, MotionEvent event_current, float distanceX, float distanceY) {
        matrix_translate.postTranslate(-distanceX, -distanceY);
        postInvalidate();
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float[] values = new float[9];
        matrix_scale.getValues(values);
        scale_after_matrix = values[Matrix.MSCALE_X];

        float scale = detector.getScaleFactor() * scale_after_matrix;
        if (scale > MAX_ZOOM_SCALE) {
            scale = MAX_ZOOM_SCALE;
        }
        if (scale < MIN_NARROW_SCALE) {
            scale = MIN_NARROW_SCALE;
        }
        matrix_scale.setScale(scale, scale, detector.getFocusX(), detector.getFocusY());
        postInvalidate();
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        return gestureDetector.onTouchEvent(event);
    }
}
