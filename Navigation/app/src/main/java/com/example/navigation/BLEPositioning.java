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

import Jama.Matrix;

public class BLEPositioning {

    private Context m_ctx;
    private Handler handler;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private Map<String, List<IBeaconRecord>> mapBltScanResult;

    private float x, y;

    private Map<String, Queue<Float>> distanceValues;
    private int averageNum = 10;
    private Map<String, Float> distanceSum;

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
        distanceValues = new HashMap<>();
        distanceSum = new HashMap<>();
        try {

            JSONObject beacons = (JSONObject) new JSONObject(getJsonString(beaconFile));
            Iterator<String> keys = beacons.keys();
            position = new Float[] {0f, 0f, 0f};

            while (keys.hasNext()) {

                String key = keys.next();
                Float[] pos = new Float[3];

                distanceValues.put(key, new LinkedList<Float>());
                distanceSum.put(key, 0f);

                pos[0] = (float) beacons.getJSONObject(key).getDouble("x");
                pos[1] = (float) beacons.getJSONObject(key).getDouble("y");
                pos[2] = (float) beacons.getJSONObject(key).getDouble("z");

                beaconPosition.put(key, new Vector3D(pos[0], pos[1], pos[2]));
//                beaconDistance.put(key, 1e-2f);
                beaconDistance.put(key, 1.717f);
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

                int minor = (scanRecord[startByte + 22] & 0xff) * 0x100
                        + (scanRecord[startByte + 23] & 0xff);

                String mac = device.getAddress();

                int txPower = (scanRecord[startByte + 24]);
                float distance = calculateAccuracy(txPower, rssi, String.valueOf(major));

//                Log.i("tag", "\nName：" + "\nMac：" + mac
//                        + " \nUUID：" + uuid + "\nMajor：" + major + "\nMinor："
//                        + minor + "\nTxPower：" + txPower + "\nrssi：" + rssi);
//
//                Log.i("tag","distance：" + distance);

                if (distance < 5.0f)
                    beaconDistance.replace(String.valueOf(major), distance);

            }
        }
    };

    float X = 0; // 狀態推算值
    float P = 2; // 狀態推算值的共變異數
    float F = 1; // 狀態轉換
    float Q = 5; // 狀態預估模型的共變異數
    float H = 1; // 測量值
    float R = 10; // 測量值的共變異數矩陣
    float I = 1;
    float K = 0; // Kalman Gain

    @RequiresApi(api = Build.VERSION_CODES.N)
    public float calculateAccuracy(int txPower, float rssi, String major) {

        X = X;
        P = P + Q;
        K = P / (P + R);
        X = X + K * (rssi - X);
        P = (I - K) * P;

//        float ratio = X * 1.0f / txPower;
        float ratio = rssi * 1.0f / txPower;
        float distance =  (0.89976f) * (float) Math.pow(ratio, 7.7095) + 0.111f;
//        float distance = (float)Math.pow(10, (double)(txPower - X) / (10f * 2f));
//        distance = (float) Math.pow(10, (double)(txPower - rssi) / (10f * 2f));

        Queue<Float> distances = distanceValues.get(major);
        distances.add(distance);
        distanceSum.replace(major, distanceSum.get(major) + distance);
        if (distances.size() > averageNum) {
            distanceSum.replace(major, distanceSum.get(major) - distanceValues.get(major).peek());
            distances.poll();
        }
        distance = distanceSum.get(major) / distances.size();
        distanceValues.replace(major, distances);

//        Log.i("tmp", beaconDistance.toString());
//        return distance;
        return 1.717f;
    }

    public PointF gradientDescent() {

        int N = beaconPosition.size();
        Matrix X = new Matrix(N - 1, 3);
        Matrix Y = new Matrix(N - 1, 1);

        Vector3D bposN = beaconPosition.get(String.valueOf(N));
        float dn = beaconDistance.get(String.valueOf(N));
        for (int i = 0; i < N - 1; i ++) {
            String I = String.valueOf(i + 1);
            Vector3D bpos = beaconPosition.get(I);

            X.set(i, 0, bposN.get(0) - bpos.get(0) + 1e-2);
            X.set(i, 1, bposN.get(1) - bpos.get(1) + 1e-2);
            X.set(i, 2, bposN.get(2) - bpos.get(2) + 1e-2);

            float d = beaconDistance.get(I);
            float posSqSum = 0;
            for (int j = 0; j < 3; j ++)
                posSqSum += bposN.get(i) * bposN.get(i) - bpos.get(i) * bpos.get(i);
            Y.set(i, 0, (d * d - dn * dn + posSqSum) / 2);
        }

        Matrix pos = X.transpose().times(X).inverse().times(X.transpose()).times(Y);
//        Matrix a = X.transpose().times(X);
//        a = a.inverse();
//        Log.i("tmp", String.valueOf(a.getRowDimension()));
//        Log.i("tmp", String.valueOf(a.getColumnDimension()));
//        String s = "";
//        for (int i = 0; i < N - 1; i ++) {
//            for (int j = 0; j < 3; j ++)
//                s += String.valueOf(X.get(i, j)) + " ";
//            s += "\n";
//        }
//        Log.i("tmp", s);

//        Vector3D pos = new Vector3D();
//        Vector3D grad = new Vector3D();
//        float leaningRate = 0.01f;

//        for (int i = 0; i < 100; i ++) {
//
//            float loss = 0;
//
//            for (HashMap.Entry<String, Vector3D> entry: beaconPosition.entrySet()) {
//                String beacon = entry.getKey();
//                Vector3D beaconPos = entry.getValue();
//                float dis = pos.dis(beaconPos) + 1e-2f;
//                float c = (dis - beaconDistance.get(beacon)) / (dis * beaconDistance.size());
//
//                grad = grad.add(pos.minus(beaconPos).mul(c));
//                loss += (dis - beaconDistance.get(beacon)) / (2 * beaconDistance.size());
//            }
//            Log.i("tmp", "p " + pos.toString());
//            Log.i("tmp", "l " + String.valueOf(loss));
//            Log.i("tmp", "g " + grad.toString());
//
//            pos = pos.minus(grad.mul(leaningRate));
//
//        }

//        return new PointF((float) pos.get(0, 0), (float) pos.get(1, 0));
        return new PointF(1.5f, 1.5f);

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