package com.example.navigation;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private String[] permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private GlobalVariable globalVariable;

    private EditText accountInput;
    private EditText passwordInput;

    private TextView anonymousLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestAllPermissions();
        init();

        loadItems(R.raw.item);

//        bleScanner = new BLEPositioning(this, R.raw.beacon);
//        if (!bleScanner.isEnabled()) {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//        }
//        bleScanner.startScan((TextView)findViewById(R.id.dis));

        Log.i("tag", "ho");

        SheetsQuickstart sheet = new SheetsQuickstart();
        try {
            sheet.test(this);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

    }

    private void requestAllPermissions() {

        for (String perm: permissions) {
            if (checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {perm}, 34);
            }
        }

    }

    private void init() {

        globalVariable = (GlobalVariable) getApplicationContext();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int windowHeight = displayMetrics.heightPixels;
        int windowWidth = displayMetrics.widthPixels;

        globalVariable.windowHeight = windowHeight;
        globalVariable.windowWidth = windowWidth;

        globalVariable.activityBarHeight = (int)(windowHeight * 0.12);
        globalVariable.activityBarButtonSize = (int)(globalVariable.activityBarHeight * 0.6);

        accountInput = (EditText) findViewById(R.id.login_account);
        passwordInput = (EditText) findViewById(R.id.login_password);

        anonymousLogin = (TextView) findViewById(R.id.anonymous_login);
        anonymousLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login("訪客", "");
            }
        });

    }

    private void loadItems(int file_id) {

        String jsonString = getJsonString(file_id);
        try {
            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i ++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                globalVariable.items.add(jsonObject);
                globalVariable.idToItem.put(jsonObject.getInt("id"), jsonObject);
                globalVariable.barcodeToItem.put(jsonObject.getString("barcode"), jsonObject);
//                Log.i("TAG", String.valueOf(jsonEvent.getInt("id")));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getJsonString(int id) {

        InputStream is = getResources().openRawResource(id);
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

    public void login(String userAccount, String userPassword) {

        globalVariable.userAccount = userAccount;
        loadItems(R.raw.item);

        Intent intent = new Intent(this, StartupActivity.class);
        startActivity(intent);
    }

}
