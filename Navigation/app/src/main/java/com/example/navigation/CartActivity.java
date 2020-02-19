package com.example.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class CartActivity extends AppCompatActivity {

    private GlobalVariable globalVariable;

    private CartItem title;
    private LinearLayout selected_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        globalVariable = (GlobalVariable) getApplicationContext();

        init();

    }

    private void init() {
        selected_list = (LinearLayout) findViewById(R.id.selected_list);
        Log.i("TAG", selected_list.toString());

        title = (CartItem) findViewById(R.id.title);
        title.setItemName("商品名稱");
        title.setItemAmount("數量");
        title.setTotalPrice("總計");

        drawItems();
    }

    private void redraw() {

        selected_list.removeAllViews();
        selected_list.addView(title);

        drawItems();

    }

    private void drawItems() {

        for (Integer key: globalVariable.addedItem.keySet()) {

            JSONObject jsonObject = globalVariable.addedItem.get(key).first;
            int amount = globalVariable.addedItem.get(key).second;

            CartItem item = new CartItem(this);
            try {
                item.setItemName(
                        jsonObject.getString("name")
                );
                item.setItemAmount(String.valueOf(amount));
                item.setTotalPrice(String.valueOf(
                        jsonObject.getInt("price") * amount
                ));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            selected_list.addView(item, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));

        }

        for (Integer key: globalVariable.selectedItem.keySet()) {

            JSONObject jsonObject = globalVariable.selectedItem.get(key).first;

            CartItem item = new CartItem(this);
            try {
                item.setItemName(
                        jsonObject.getString("name")
                );
                item.setItemAmount("0");
                item.setTotalPrice("0");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            selected_list.addView(item, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));

        }

    }

    public void changeToMap(View v) {
        Log.i("TAG", "change to map");
        startActivity(new Intent(this, MapActivity.class));
    }

    public void changeToAddItem(View v) {
        Log.i("TAG", "change to addItem");
        startActivity(new Intent(this, AddItemActivity.class));
    }

    public void changeToCart(View v) {}

}
