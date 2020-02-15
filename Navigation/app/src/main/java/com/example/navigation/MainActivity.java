package com.example.navigation;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    GlobalVariable globalVariable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.CAMERA}, 34);
            while (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {}
        }

        init();

//        Intent intent = new Intent(this, MapActivity.class);
//        startActivity(intent);
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

    }

}
