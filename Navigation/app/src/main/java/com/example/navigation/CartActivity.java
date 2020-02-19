package com.example.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

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

    @Override
    protected void onResume() {
        super.onResume();
        redraw();
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

        for (Pair<JSONObject, Integer> val: globalVariable.addedItem) {

            JSONObject jsonObject = val.first;
            int amount = val.second;

            CartItem item = new CartItem(this);
            try {
                item.setItemName(
                        jsonObject.getString("name")
                );
                item.setItemAmount(String.valueOf(amount));
                item.setTotalPrice(String.valueOf(
                        jsonObject.getInt("price")
                ));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            selected_list.addView(item, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));

        }

        for (Pair<JSONObject, Integer> val: globalVariable.selectedItem) {

            JSONObject jsonObject = val.first;

            CartItem item = new CartItem(this);
            try {
                item.setItemName(
                        jsonObject.getString("name")
                );
                item.setItemAmount("0");
                item.setTotalPrice(String.valueOf(
                        jsonObject.getInt("price")
                ));
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
