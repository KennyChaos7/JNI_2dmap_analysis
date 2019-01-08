package org.k;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import org.k.jni_2dmap_analysis.BuildConfig;
import org.k.jni_2dmap_analysis.R;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kenny on 19-1-2.
 */
public class BitmapSurfaceView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener,GestureDetector.OnDoubleTapListener, GestureDetector.OnGestureListener, ScaleGestureDetector.OnScaleGestureListener {
    private Bitmap mBitmap = null;
    private Canvas mCanvas = null;
    private Paint mPaint = new Paint();
    private SurfaceHolder mSurfaceHolder = null;
    private final float MAX_ZOOM_SCALE = 7f;                            //放大的比例
    private final float MIN_NARROW_SCALE = .5f;                         //缩小的比例
    public Matrix matrix_translate = new Matrix();                             //偏移矩阵
    public Matrix matrix_scale = new Matrix();                                 //缩放矩阵
    private GestureDetector gestureDetector = null;                     //检测单手指手势类型
    private ScaleGestureDetector scaleGestureDetector = null;           //检测双手指手势类型
    private float scale_after_matrix = 1.0f;                            //矩阵得出的比列
    private Runnable updateBitmapRunnable = null;

    public BitmapSurfaceView(Context context) {
        super(context);
//        setBackgroundColor(getResources().getColor(R.color.mapViewBg));
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        setFocusable(true);
        setKeepScreenOn(true);
        setFocusableInTouchMode(true);
        setOnTouchListener(this);
        mPaint.setAntiAlias(false);
        gestureDetector = new GestureDetector(context, this);
        scaleGestureDetector = new ScaleGestureDetector(context, this);
    }

    public BitmapSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        setBackgroundColor(getResources().getColor(R.color.mapViewBg));
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        setFocusable(true);
        setKeepScreenOn(true);
        setFocusableInTouchMode(true);
        setOnTouchListener(this);
        mPaint.setAntiAlias(false);
        gestureDetector = new GestureDetector(context, this);
        scaleGestureDetector = new ScaleGestureDetector(context, this);
    }

    public BitmapSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        setBackgroundColor(getResources().getColor(R.color.mapViewBg));
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        setFocusable(true);
        setKeepScreenOn(true);
        setFocusableInTouchMode(true);
        setOnTouchListener(this);
        mPaint.setAntiAlias(false);
        gestureDetector = new GestureDetector(context, this);
        scaleGestureDetector = new ScaleGestureDetector(context, this);
    }

    public BitmapSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
//        setBackgroundColor(getResources().getColor(R.color.mapViewBg));
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        setFocusable(true);
        setKeepScreenOn(true);
        setFocusableInTouchMode(true);
        setOnTouchListener(this);
        mPaint.setAntiAlias(false);
        gestureDetector = new GestureDetector(context, this);
        scaleGestureDetector = new ScaleGestureDetector(context, this);
    }

    public void addBitmap(Bitmap bitmap){
        if (bitmap != null) {
            mBitmap = bitmap;
        }
        if (BuildConfig.DEBUG) {
            Log.e("addBitmap", "postInvalidate ---- " + System.currentTimeMillis());
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        updateBitmapRunnable = ()-> {
            while (true) {
                try {
                    mCanvas = mSurfaceHolder.lockCanvas();
                    mCanvas.concat(matrix_translate);
                    mCanvas.concat(matrix_scale);
                    if (mBitmap != null)
                        mCanvas.drawBitmap(mBitmap, 0, 0, mPaint);
                    if (mCanvas != null)
                        mSurfaceHolder.unlockCanvasAndPost(mCanvas);
                    Thread.sleep(100);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        Executors.newSingleThreadExecutor().execute(updateBitmapRunnable);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

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
        gestureDetector.onTouchEvent(event);
        return true;
    }

}
