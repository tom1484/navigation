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
    private MapEvent person;
    private MapEvent border;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        map = (Map) findViewById(R.id.map);

        person = new MapEvent("person", 6.5f, 3.5f, 50f);
        map.addEvent(person);

        ArrayList<Pair<PointF, PointF>> paths = new ArrayList<>();
        paths.add(new Pair<>(new PointF(0f, 0f), new PointF(13f, 0f)));
        paths.add(new Pair<>(new PointF(13f, 0f), new PointF(13f, 8f)));
        paths.add(new Pair<>(new PointF(13f, 8f), new PointF(0f, 8f)));
        paths.add(new Pair<>(new PointF(0f, 8f), new PointF(0f, 0f)));
        border = new MapEvent("path", paths, 5f);
        map.addEvent(border);

        final View layout = (View) findViewById(R.id.map);
        ViewTreeObserver viewTreeObserver = layout.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                map.centerPosition(person.x, person.y, 6f);
            }
        });

    }

    public void centerPosition(View v) {
        map.centerPosition(person.x, person.y, 6f);
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
