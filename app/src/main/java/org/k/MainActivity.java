package org.k;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;
import org.k.jni_2dmap_analysis.BuildConfig;
import org.k.jni_2dmap_analysis.R;

import java.io.IOException;

/**
 * Created by Kenny on 18-11-12.
 */
public class MainActivity extends AppCompatActivity {
    private Button mBtn_first,mBtn_secord;
    private BitmapView mBitmapView;
    private Bitmap mBitmap;
    private Bitmap.Config mConfig;
    private JNIUtils mJNIUtils = null;

    private int size = 1000;
    private String str_color_block = "#FF000000";
    private String str_color_cleaned = "#ff000066";
    String test_file_name = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtn_first = findViewById(R.id.btn_first);
        mBtn_first.setOnClickListener((v)->{
            test_file_name = "testdata10";
            readAssetsFileToGetMap();
            readAssetsFileToGetTrack();
        });
        mBtn_secord = findViewById(R.id.btn_secord);
        mBtn_secord.setOnClickListener((v -> {
            test_file_name = "testdata8";
            readAssetsFileToGetMap();
            readAssetsFileToGetTrack();
        }));
        mBitmapView = findViewById(R.id.bitmap_view);
        mBitmapView.setBackgroundColor(R.color.mapViewBg);
        mConfig = Bitmap.Config.ARGB_8888;
        mBitmap = Bitmap.createBitmap(size,size,mConfig);
        mJNIUtils = new JNIUtils(str_color_block,str_color_cleaned);
    }

    private void readAssetsFileToGetMap() {
        StringBuilder __data = new StringBuilder();
        int length = -1, a_length = 0;
        try {
            int t_length = getAssets().open(test_file_name).available();
            byte[] bytes = new byte[t_length];
            length = getAssets().open(test_file_name).read(bytes);
            __data.append(new String(bytes));
            String _ = __data.toString();
            _ = _.substring(0, t_length).trim();
            if (BuildConfig.DEBUG) {
                Log.e("as", _);
                Log.e("as", "length = " + length + " _.length = " + _.length() + "\n" + _.substring(t_length - 10));
            }
            JSONObject data = new JSONObject(_);
            String str_map = data.getString("map");
            byte[] map_data_bytes = Base64.decode(str_map,Base64.NO_WRAP);
            int result = mJNIUtils.getMapBitmap(mBitmap,map_data_bytes);
            if (BuildConfig.DEBUG) {
                Log.e("toBitmap", "" + result);
            }
            mBitmapView.addBitmap(mBitmap);
            if (BuildConfig.DEBUG) {
                Log.e("toBitmap", "finish");
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void readAssetsFileToGetTrack()
    {
        StringBuilder __data = new StringBuilder();
        int length = -1, a_length = 0;
        try {
            int t_length = getAssets().open(test_file_name).available();
            byte[] bytes = new byte[t_length];
            length = getAssets().open(test_file_name).read(bytes);
            __data.append(new String(bytes));
            String _ = __data.toString();
            _ = _.substring(0, t_length).trim();
            if (BuildConfig.DEBUG) {
                Log.e("as", _);
                Log.e("as", "length = " + length + " _.length = " + _.length() + "\n" + _.substring(t_length - 10));
            }
            JSONObject data = new JSONObject(_);
            String str_track = data.getString("track");
            byte[] track_data_bytes = Base64.decode(str_track,Base64.NO_WRAP);
            int result = mJNIUtils.getTrackBitmap(mBitmap,track_data_bytes);
            if (BuildConfig.DEBUG) {
                Log.e("toBitmap", "" + result);
            }
            mBitmapView.addBitmap(mBitmap);
            if (BuildConfig.DEBUG) {
                Log.e("toBitmap", "finish");
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}
