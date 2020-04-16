package com.example.navigation;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

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

    private Queue<Float> rssiValues;
    private int averageNum = 5;
    private float rssiSum = 0.0f, rssiAverage = 0.0f;

    Float[] position;

    ArrayList<Float[]> beaconPosition;
    ArrayList<Float> beaconDistance;

    private int REQUEST_ENABLE_BT = 2;

    public BLEPositioning(Context ctx) {

        super();
        this.m_ctx = ctx;
        initParam();

        position = new Float[] {0f, 0f, 0f};

        try {

            JSONObject beacons = (JSONObject) new JSONObject(getJsonString(R.raw.beacon));
            Iterator<String> keys = beacons.keys();

            while (keys.hasNext()) {

                String key = keys.next();
                Float[] pos = new Float[] {0f, 0f, 0f};

                pos[0] = (float) beacons.getJSONObject(key).getDouble("x");
                pos[1] = (float) beacons.getJSONObject(key).getDouble("y");
                pos[2] = (float) beacons.getJSONObject(key).getDouble("z");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        beaconPosition = new ArrayList<>();
        beaconDistance = new ArrayList<>();

    }

    public void setPosition(float _x, float _y) {
        x = _x;
        y = _y;
    }

    private void initParam() {

        handler = new Handler();
        mapBltScanResult = new HashMap<String, List<IBeaconRecord>>();
        rssiValues = new LinkedList<Float>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            bluetoothManager = (BluetoothManager) this.m_ctx.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }

    }

    public boolean isEnabled() {
        return mBluetoothAdapter.isEnabled();
    }

    public void startScan() {

        Log.i("tag", "start");

        mapBltScanResult.clear();
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.startLeScan(bltScanCallback);
        }

    }

    private BluetoothAdapter.LeScanCallback bltScanCallback = new BluetoothAdapter.LeScanCallback() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

            int startByte = 2;
            boolean patternFound = false;

            Log.i("tag", "got" + String.valueOf(device.toString()));
            Log.i("tag", "got" + String.valueOf(rssi));
            Log.i("tag", "got");

            while (startByte <= 5) {
                if (((int) scanRecord[startByte + 2] & 0xff) == 0x02 && ((int) scanRecord[startByte + 3] & 0xff) == 0x15) {
                    patternFound = true;
                    break;
                }
                startByte++;
            }

//            Log.i("tag", "got1");

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

//                if (major < 4660 || major > 4665)
//                    return ;

                Log.i("tag", "got2");

                int minor = (scanRecord[startByte + 22] & 0xff) * 0x100
                        + (scanRecord[startByte + 23] & 0xff);

                String mac = device.getAddress();

                int txPower = (scanRecord[startByte + 24]);
                double distance = calculateAccuracy(txPower, rssi);

                Log.i("tag", "Name：" + "\nMac：" + mac
                        + " \nUUID：" + uuid + "\nMajor：" + major + "\nMinor："
                        + minor + "\nTxPower：" + txPower + "\nrssi：" + rssi);

                Log.i("tag","distance：" + calculateAccuracy(txPower, rssi));

//                if (globalVariable.distance.containsKey(uuid))
//                Log.i("tag", globalVariable.distance.toString());

            }
        }
    };

    public float calculateAccuracy(int txPower, float rssi) {
        rssiValues.offer(rssi);
        rssiSum += rssi;
        if (rssiValues.size() > averageNum) {
            rssiSum -= rssiValues.peek();
            rssiValues.poll();
        }
        rssiAverage = rssiSum / rssiValues.size();

        if (rssiAverage == 0) {
            return -1.0f;
        }

        float ratio = rssiAverage * 1.0f / txPower;

        if (ratio < 1.0)  {
            return (float) Math.pow(ratio, 10);
        }
        else {
            float accuracy = (0.89976f) * (float) Math.pow(ratio, 7.7095f) + 0.111f;
            return accuracy;
        }
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