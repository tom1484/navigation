package com.example.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class StartupActivity extends AppCompatActivity {

    private GlobalVariable globalVariable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

    }

    public void changeToMap(View v) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    public void changeToCart(View v) {
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }

}
