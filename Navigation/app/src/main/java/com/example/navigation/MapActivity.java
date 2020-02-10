package com.example.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MapActivity extends AppCompatActivity {

    Map map;
    MapEvent person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        map = (Map) findViewById(R.id.map);

        person = new MapEvent("person", 300f, 300f, 30f);
        map.addEvent(person);

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
