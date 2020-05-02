package com.example.marketnavigation;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
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
import java.security.GeneralSecurityException;

public class MainActivity extends AppCompatActivity {

    private String[] permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestAllPermissions();

    }

    private void requestAllPermissions() {

        for (String perm: permissions) {
            if (checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {perm}, 34);
            }
        }

    }

//    private void loadItems(int file_id) {
//
//        String jsonString = getJsonString(file_id);
//        try {
//            JSONArray jsonArray = new JSONArray(jsonString);
//
//            for (int i = 0; i < jsonArray.length(); i ++) {
//                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
//                globalVariable.items.add(jsonObject);
//                globalVariable.idToItem.put(jsonObject.getInt("id"), jsonObject);
//                globalVariable.barcodeToItem.put(jsonObject.getString("barcode"), jsonObject);
////                Log.i("TAG", String.valueOf(jsonEvent.getInt("id")));
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

//    private String getJsonString(int id) {
//
//        InputStream is = getResources().openRawResource(id);
//        Writer writer = new StringWriter();
//        char[] buffer = new char[1024];
//        try {
//            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
//            int n;
//            while ((n = reader.read(buffer)) != -1) {
//                writer.write(buffer, 0, n);
//            }
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                is.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return writer.toString();
//    }

}
