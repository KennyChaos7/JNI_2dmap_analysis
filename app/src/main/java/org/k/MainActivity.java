package org.k;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kenny on 18-11-12.
 */
public class MainActivity extends AppCompatActivity implements TCPListener {
    private Button mBtn_first,mBtn_secord;
    private BitmapView mBitmapView;
    private Bitmap mBitmap;
    private Bitmap.Config mConfig;
    private JNIUtils mJNIUtils = null;
    private TCPUtil tcpUtil = null;
    private int size = 1000;
    private String str_color_block = "#FF000000";
    private String str_color_cleaned = "#ff000066";
    String test_file_name = "";
    ScheduledExecutorService scheduledThread = Executors.newScheduledThreadPool(1);
    int ssss = 13;
    String __IP = "";
//    String __IP = "192.168.233.121";
//    String __IP = "192.168.199.224";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        __IP = getIntent().getStringExtra("ip");
        mBtn_first = findViewById(R.id.btn_first);
        mBtn_first.setOnClickListener((v)->{
            tcpUtil.conn();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.e("addBitmap","start ---- " + System.currentTimeMillis());
            String _ = Base64.encodeToString(__intToByteArrayLittle(0,4),Base64.NO_WRAP);
            JSONObject jo = new JSONObject();
            try {
                jo.put("map",_);
                jo.put("track",_);
                int jo_length = jo.toString().getBytes().length;
                byte[] bytes_send = new byte[jo_length + 4];
                byte[] temp = __intToByteArrayLittle(jo_length,4);
                System.arraycopy(temp,0,bytes_send,0,4);
                System.arraycopy(jo.toString().getBytes(),0,bytes_send,4,jo_length);
                tcpUtil.send(bytes_send);
                bytes_send = null;
                temp = null;
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            test_file_name = "testdata";
//            readAssetsFileToGetMap();
        });
        mBtn_secord = findViewById(R.id.btn_secord);
        mBtn_secord.setOnClickListener((v -> {
//            test_file_name = "testdata";
//            readAssetsFileToGetMap();
            scheduledThread.scheduleAtFixedRate(() -> {
                byte[] historyIdsToBytes = new byte[100 * 2];
                for (int index = 0; index < 100; index++)
                {
                    int historyId = mJNIUtils.last_time_history_id_list[index];
                    byte[] temp = __intToByteArrayLittle(historyId,2);
                    System.arraycopy(temp,0,historyIdsToBytes,index * 2 , 2);
                }
                String _map = Base64.encodeToString(historyIdsToBytes,Base64.NO_WRAP);
                String _track = Base64.encodeToString(__intToByteArrayLittle(0,4),Base64.NO_WRAP);
                JSONObject jo = new JSONObject();
                try {
                    jo.put("map",_map);
                    jo.put("track",_track);
                    int jo_length = jo.toString().getBytes().length;
                    byte[] bytes_send = new byte[jo_length + 4];
                    byte[] temp = __intToByteArrayLittle(jo_length,4);
                    System.arraycopy(temp,0,bytes_send,0,4);
                    System.arraycopy(jo.toString().getBytes(),0,bytes_send,4,jo_length);
                    tcpUtil.send(bytes_send);
                    bytes_send = null;
                    temp = null;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            },0,2 * 1000, TimeUnit.MILLISECONDS);
        }));
        mBitmapView = findViewById(R.id.bitmap_view);
        mBitmapView.setBackgroundColor(getResources().getColor(R.color.mapViewBg));
        mConfig = Bitmap.Config.ARGB_8888;
        mBitmap = Bitmap.createBitmap(size,size,mConfig);
        mJNIUtils = new JNIUtils(str_color_block,str_color_cleaned);

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager != null ? wifiManager.getConnectionInfo() : null;
        try {
            String local_ip = String.valueOf(InetAddress.getByName(__formatString(wifiInfo != null ? wifiInfo.getIpAddress() : 0))).substring(1);
            tcpUtil = new TCPUtil(8088, local_ip);
            tcpUtil.registerListener(this);
            tcpUtil.setRoombaIP(__IP);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
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
                for (int index = 0; index < 100; index ++) {
                    Log.e("toBitmap","history_id = " + mJNIUtils.last_time_history_id_list[index] + " index = " + index);
                }
            }
            mBitmapView.addBitmap(mBitmap);
            if (BuildConfig.DEBUG) {
                Log.e("toBitmap", "finish ---- " + System.currentTimeMillis());
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void readAssetsFileToGetTrack() {
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
                Log.e("toBitmap", "finish ---- " + System.currentTimeMillis());
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private String __formatString(int value) {
        StringBuilder strValue = new StringBuilder();
        byte[] ary = __intToByteArrayLittle(value , 4);
        for (int i = ary.length - 1; i >= 0; i--) {
            strValue.append(ary[i] & 0xFF);
            if (i > 0) {
                strValue.append(".");
            }
        }
        return strValue.toString();
    }

    private byte[] __intToByteArrayLittle(int value, int length) {
        byte[] b = new byte[length];
        for (int i = 0; i < length; i++) {
            int offset = (b.length - 1 - i) * 8;
            b[i] = (byte) ((value >>> offset) & 0xFF);
        }
        return b;
    }

    @Override
    public void onReceive(byte[] bytes, int length) {
        try {
            Log.e("toBitmap",System.currentTimeMillis() + " - " + length);
            JSONObject data = new JSONObject(new String(bytes));
            // map
            String str_map = data.getString("map");
            byte[] map_data_bytes = Base64.decode(str_map, Base64.NO_WRAP);
            mJNIUtils.getMapBitmap(mBitmap, map_data_bytes);
            String str_track = data.getString("track");
            byte[] track_data_bytes = Base64.decode(str_track,Base64.NO_WRAP);
            mJNIUtils.getTrackBitmap(mBitmap,track_data_bytes);
            mBitmapView.addBitmap(mBitmap);
            if (BuildConfig.DEBUG) {
                Log.e("addBitmap", "finish ---- " + System.currentTimeMillis());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSend(byte[] bytes) {

    }

    @Override
    public void onError(int errorCode, String errorMessage) {

    }

    @Override
    public void onState(String stateMessage) {

    }

}
