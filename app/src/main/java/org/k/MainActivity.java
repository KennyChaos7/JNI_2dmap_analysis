package org.k;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.k.jni_2dmap_analysis.R;

/**
 * Created by Kenny on 18-11-12.
 */
public class MainActivity extends AppCompatActivity {
    private Button mButton;
    private BitmapView mBitmapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO test jni
                byte[] in = new byte[]{1,2,3,4};
                JNIUtils jni = new JNIUtils();
                byte[] out = jni.toBitmapByteArray(in);
                Log.e("toBitmap",""+out[1]);
                mBitmapView.addByteArrayToBitmap(out);
            }
        });
        mBitmapView = findViewById(R.id.bitmap_view);

    }
}
