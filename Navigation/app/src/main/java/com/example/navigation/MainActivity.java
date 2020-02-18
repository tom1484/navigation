package com.example.navigation;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private GlobalVariable globalVariable;

    private EditText accountInput;
    private EditText passwordInput;

    private TextView anonymousLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.CAMERA}, 34);
            while (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {}
        }
        init();

        Intent intent = new Intent(this, AddItemActivity.class);
        startActivity(intent);
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

    private void loadItems(String path) {

    }

    public void login(String userAccount, String userPassword) {

        globalVariable.userAccount = userAccount;

        Intent intent = new Intent(this, StartupActivity.class);
        startActivity(intent);
    }

}
