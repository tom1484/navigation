package com.example.navigation;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity {

    private Map map;
    private PointF person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        map = (Map) findViewById(R.id.map);
        map.loadEvents(R.raw.map);

        person = map.setPerson(6.5f, 3.5f, 50);

        final View layout = (View) findViewById(R.id.map);
        ViewTreeObserver viewTreeObserver = layout.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                centerPosition(null);
            }
        });

    }

    public void centerPosition(View v) {
        map.centerPosition(6.5f, 3.5f, 6f);
    }

    public void changeToMap(View v) {}

    public void changeToAddItem(View v) {
        Log.i("TAG", "change to addItem");
        startActivity(new Intent(this, AddItemActivity.class));
    }

    public void changeToCart(View v) {
        Log.i("TAG", "change to cart");
        startActivity(new Intent(this, CartActivity.class));
    }

}
