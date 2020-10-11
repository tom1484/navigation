package com.example.navigation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class CartItem extends LinearLayout {

    private LinearLayout linearLayout;

    private TextView itemName;
    private TextView itemAmount;
    private TextView totalPrice;

    public CartItem(Context context) {
        this(context, null);
    }

    public CartItem(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CartItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.view_cartitem, this);

        linearLayout = (LinearLayout) findViewById(R.id.cartitem_linearlayout);

        itemName = (TextView) findViewById(R.id.item_name);
        itemAmount = (TextView) findViewById(R.id.item_amount);
        totalPrice = (TextView) findViewById(R.id.total_price);

        itemName.setText("");
        itemAmount.setText("0");
        totalPrice.setText("0");

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


}
