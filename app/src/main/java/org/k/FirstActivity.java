package org.k;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.k.jni_2dmap_analysis.R;

/**
 * Created by Kenny on 18-12-10.
 */
public class FirstActivity extends AppCompatActivity {
    private EditText mET_ip = null;
    private Button btn_next = null;

    @Override
    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.activity_first);

        mET_ip = findViewById(R.id.et_ip);
        btn_next = findViewById(R.id.btn_intent);
        btn_next.setOnClickListener(v -> {
            if (!mET_ip.getText().toString().trim().equals(""))
            {
                startActivity(new Intent(FirstActivity.this,MainActivity.class).putExtra("ip",mET_ip.getText().toString().trim()));
                finish();
            }else
            {
                Snackbar.make(btn_next,"need IP",Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
