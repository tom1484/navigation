package com.example.navigation;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.util.Base64Utils;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity {

    private Map map;
    private PointF person;

    private Button  up;
    private Button  down;
    private Button  left;
    private Button  right;

    private float speed = 0.03f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        map = (Map) findViewById(R.id.map);
        map.loadEvents(R.raw.map);

        person = map.setPerson(1f, 2f, 50);

        final View layout = (View) findViewById(R.id.map);
        ViewTreeObserver viewTreeObserver = layout.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                centerPosition(null);
            }
        });

        up = (Button) findViewById(R.id.up);
        down = (Button) findViewById(R.id.down);
        left = (Button) findViewById(R.id.left);
        right = (Button) findViewById(R.id.right);

        up.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                person = map.setPerson(person.x, person.y - 0.03f, 50);
                person.set(person.x, person.y - speed);
                map.centerPosition(person.x, person.y, 6);
                map.invalidate();
                return false;
            }
        });

        down.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                person.set(person.x, person.y + speed);
                map.centerPosition(person.x, person.y, 6);
                map.invalidate();
                return false;
            }
        });

        left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                person.set(person.x - speed, person.y);
                map.centerPosition(person.x, person.y, 6);
                map.invalidate();
                return false;
            }
        });

        right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                person.set(person.x + speed, person.y);
                map.centerPosition(person.x, person.y, 6);
                map.invalidate();
                return false;
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
