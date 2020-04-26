package com.example.navigation;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.graphics.PointF;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class BLEPositioning {

    private Context m_ctx;
    private Handler handler;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private Map<String, List<IBeaconRecord>> mapBltScanResult;

    private float x, y;

    private Queue<Float> distanceValues;
    private int averageNum = 10;
    private float distanceSum = 0.0f, distance = 0.0f;

    Float[] position;

    Map<String, Vector3D> beaconPosition;
    Map<String, Float> beaconDistance;

    private int REQUEST_ENABLE_BT = 2;

    public BLEPositioning(Context ctx, int beaconFile) {

        super();
        this.m_ctx = ctx;
        initParam();

        beaconPosition = new HashMap<>();
        beaconDistance = new HashMap<>();
        try {

            JSONObject beacons = (JSONObject) new JSONObject(getJsonString(beaconFile));
            Iterator<String> keys = beacons.keys();
            position = new Float[] {0f, 0f, 0f};

            while (keys.hasNext()) {

                String key = keys.next();
                Float[] pos = new Float[] {0f, 0f, 0f};

                pos[0] = (float) beacons.getJSONObject(key).getDouble("x");
                pos[1] = (float) beacons.getJSONObject(key).getDouble("y");
                pos[2] = (float) beacons.getJSONObject(key).getDouble("z");

                beaconPosition.put(key, new Vector3D(pos[0], pos[1], pos[2]));
                beaconDistance.put(key, 0f);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("tag", "ho");

    }

    public void setPosition(float _x, float _y) {
        x = _x;
        y = _y;
    }

    private void initParam() {

        handler = new Handler();
        mapBltScanResult = new HashMap<String, List<IBeaconRecord>>();
        distanceValues = new LinkedList<Float>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            bluetoothManager = (BluetoothManager) this.m_ctx.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }

    }

    public boolean isEnabled() {
        return mBluetoothAdapter.isEnabled();
    }

    private TextView textView;

    public void startScan(TextView _textView) {

        textView = _textView;

        mapBltScanResult.clear();
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.startLeScan(bltScanCallback);
        }

    }

    public void startScan() {

        mapBltScanResult.clear();
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.startLeScan(bltScanCallback);
        }

    }

    private BluetoothAdapter.LeScanCallback bltScanCallback = new BluetoothAdapter.LeScanCallback() {
        @SuppressLint("NewApi")
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

            int startByte = 2;
            boolean patternFound = false;

            while (startByte <= 5) {
                if (((int) scanRecord[startByte + 2] & 0xff) == 0x02 && ((int) scanRecord[startByte + 3] & 0xff) == 0x15) {
                    patternFound = true;
                    break;
                }
                startByte++;
            }

            if (patternFound) {

                byte[] uuidBytes = new byte[16];

                System.arraycopy(scanRecord, startByte + 4, uuidBytes, 0, 16);
                String hexString = bytesToHex(uuidBytes);

                String uuid = hexString.substring(0, 8) + "-"
                        + hexString.substring(8, 12) + "-"
                        + hexString.substring(12, 16) + "-"
                        + hexString.substring(16, 20) + "-"
                        + hexString.substring(20, 32);

                int major = (scanRecord[startByte + 20] & 0xff) * 0x100
                        + (scanRecord[startByte + 21] & 0xff);

//                if (major < 4660 || major > 4663)
//                    return ;

                int minor = (scanRecord[startByte + 22] & 0xff) * 0x100
                        + (scanRecord[startByte + 23] & 0xff);

                String mac = device.getAddress();

                int txPower = (scanRecord[startByte + 24]);
                float distance = calculateAccuracy(txPower, rssi);

//                Log.i("tag", "\nName：" + "\nMac：" + mac
//                        + " \nUUID：" + uuid + "\nMajor：" + major + "\nMinor："
//                        + minor + "\nTxPower：" + txPower + "\nrssi：" + rssi);
//
//                Log.i("tag","distance：" + distance);

//                Log.i("tag", beaconDistance.toString());
                beaconDistance.replace(String.valueOf(major), distance);

            }
        }
    };

    float X = 0; // 狀態推算值
    float P = 2; // 狀態推算值的共變異數
    float F = 1; // 狀態轉換
    float Q = 2; // 狀態預估模型的共變異數
    float H = 1; // 測量值
    float R = 5; // 測量值的共變異數矩陣
    float I = 1;
    float K = 0; // Kalman Gain

    public float calculateAccuracy(int txPower, float rssi) {

        X = X;
        P = P + Q;
        K = P / (P + R);
        X = X + K * (rssi - X);
        P = (I - K) * P;

        float ratio = X * 1.0f/txPower;
        float distance =  (0.89976f) * (float) Math.pow(ratio, 7.7095) + 0.111f;
//        float distance = (float)Math.pow(10, (double)(txPower - X) / (10f * 2f));
//        distance = (float)Math.pow(10, (double)(txPower - rssi) / (10f * 2f));

        distanceValues.offer(distance);
        distanceSum += distance;
        if (distanceValues.size() > averageNum) {
            distanceSum -= distanceValues.peek();
            distanceValues.poll();
        }
        distance = distanceSum / distanceValues.size();

        return distance;
    }

    public PointF gradientDescent() {

        Vector3D pos = new Vector3D();
        Vector3D grad = new Vector3D();
        float leaningRate = 0.01f;

        for (int i = 0; i < 100; i ++) {

            for (HashMap.Entry<String, Vector3D> entry: beaconPosition.entrySet()) {
                String beacon = entry.getKey();
                Vector3D beaconPos = entry.getValue();
                float dis = pos.dis(beaconPos);
                float c = (dis - beaconDistance.get(beacon)) / (dis * beaconPosition.size());
                grad = grad.add(pos.minus(beaconPos).mul(c));
            }

            pos = pos.minus(grad.mul(leaningRate));

        }

//        Log.i("tag", pos.toString());
        return new PointF(pos.vector[0], pos.vector[1]);

    }

    private char[] hexArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    private String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j ++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public String getJsonString(int id) {
        InputStream is = m_ctx.getResources().openRawResource(id);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return writer.toString();
    }
}