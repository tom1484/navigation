package com.example.navigation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class CartItem extends LinearLayout {

    GlobalVariable globalVariable;

    private LinearLayout linearLayout;

    private TextView itemName;
    private TextView itemAmount;
    private TextView totalPrice;
    public Button confirm_remove;

    private int key;

    private Context context;

    public CartItem(Context cxt) {
        this(cxt, null);
    }

    public CartItem(Context cxt, @Nullable AttributeSet attrs) {
        this(cxt, attrs, 0);
    }

    public CartItem(Context cxt, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(cxt, attrs, defStyleAttr);

        context = cxt;
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.view_cartitem, this);
        globalVariable = (GlobalVariable) context.getApplicationContext();

        linearLayout = (LinearLayout) findViewById(R.id.cartitem_linearlayout);

        itemName = (TextView) findViewById(R.id.item_name);
        itemAmount = (TextView) findViewById(R.id.item_amount);
        totalPrice = (TextView) findViewById(R.id.total_price);
        confirm_remove = (Button) findViewById(R.id.confirm_remove);

        itemName.setText("");
        itemAmount.setText("0");
        totalPrice.setText("0");

//        confirm_remove.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                globalVariable.addedItem.remove(key);
//            }
//        });

    }

    public void setBackground(int color) {
        linearLayout.setBackgroundColor(color);
    }

    public void setItemName(String name) {
        itemName.setText(name);
    }

    public void setItemAmount(String amount) {
        itemAmount.setText(amount);
    }

    public void setTotalPrice(String price) {
        totalPrice.setText(price);
    }

    public void setKey(int _key) {
        key = _key;
    }


}
