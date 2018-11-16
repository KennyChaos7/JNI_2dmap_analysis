package org.k;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
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
    private Button mButton;
    private BitmapView mBitmapView;
    private Bitmap mBitmap;
    private Bitmap.Config mConfig;


    String test_file_name = "testdata7";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readAssetsFileToGetMap();
            }
        });
        mBitmapView = findViewById(R.id.bitmap_view);
        mBitmapView.setBackgroundColor(R.color.mapViewBg);
        mConfig = Bitmap.Config.ARGB_8888;
        mBitmap = Bitmap.createBitmap(1000,1000,mConfig);
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
            JNIUtils jni = new JNIUtils();
            int result = jni.ModifyBitmapData(mBitmap,map_data_bytes);
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
