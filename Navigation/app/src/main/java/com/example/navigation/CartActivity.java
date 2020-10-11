package com.example.navigation;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class CartActivity extends AppCompatActivity {

    private GlobalVariable globalVariable;

    private CartItem title;
    private LinearLayout selected_list;
    private TextView total_text;

    private AlertDialog checkoutDialog;

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
        total_text = (TextView)  findViewById(R.id.total);

//        globalVariable.addedItem.clear();
//        globalVariable.addedItem.put(
//                3, new Pair<>(
//                        globalVariable.idToItem.get(3), 1
//                )
//        );
//        globalVariable.addedItem.put(
//                5, new Pair<>(
//                        globalVariable.idToItem.get(5), 1
//                )
//        );
        drawItems();
    }

    private void redraw() {

        selected_list.removeAllViews();
        drawItems();

    }

    private void drawItems() {

        int total = 0;

        for (Integer key: globalVariable.addedItem.keySet()) {

            JSONObject jsonObject = globalVariable.addedItem.get(key).first;
            int amount = globalVariable.addedItem.get(key).second;

            CartItem item = new CartItem(this);
            try {
                item.setItemName(
                        jsonObject.getString("name")
                );
                item.setItemAmount(String.valueOf(amount));
                item.setTotalPrice("$" + String.valueOf(
                        jsonObject.getInt("price") * amount
                ));

                total += jsonObject.getInt("price") * amount;
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
                item.setTotalPrice("$0");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            selected_list.addView(item, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));

        }

        total_text.setText("$" + String.valueOf(total));
        Log.i("TAG", String.valueOf(globalVariable.selectedItem));

    }

    public void searchItem(View v) {

        Intent intent = new Intent(this, SearchItemActivity.class);
        startActivity(intent);

    }

    public void checkout(View v) {

        View dialogView = LayoutInflater.from(this).inflate(R.layout.activity_checkout, null, false);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(dialogView);
        checkoutDialog = dialogBuilder.create();
        checkoutDialog.show();

    }

    public void close(View v) {

        globalVariable.addedItem = new HashMap<>();
        globalVariable.selectedItem = new HashMap<>();

        redraw();

    }

    public void selectVISA(View v) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("結帳成功");
        dialogBuilder.setPositiveButton("關閉", null);
        dialogBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                checkoutDialog.dismiss();
            }
        });
        dialogBuilder.show();

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
