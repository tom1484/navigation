package com.example.navigation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import android.view.ViewGroup;

public class ActivityBar extends LinearLayout {

    GlobalVariable globalVariable;

    private Button map;
    private Button addItem;
    private Button cart;

    public ActivityBar(Context context) {
        this(context, null);
    }

    public ActivityBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActivityBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        globalVariable = (GlobalVariable) context.getApplicationContext();
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.view_activitybar, this);

        map = (Button) findViewById(R.id.map);
        addItem = (Button) findViewById(R.id.addItem);
        cart = (Button) findViewById(R.id.cart);

        post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams selfParams = getLayoutParams();
                selfParams.height = globalVariable.activityBarHeight;
                setLayoutParams(selfParams);

                ViewGroup.LayoutParams mapParams = map.getLayoutParams();
                mapParams.width = globalVariable.activityBarButtonSize;
                mapParams.height = globalVariable.activityBarButtonSize;
                map.setLayoutParams(mapParams);

                ViewGroup.LayoutParams addItemParams = map.getLayoutParams();
                addItemParams.width = globalVariable.activityBarButtonSize;
                addItemParams.height = globalVariable.activityBarButtonSize;
                addItem.setLayoutParams(addItemParams);

                ViewGroup.LayoutParams cartParams = map.getLayoutParams();
                cartParams.width = globalVariable.activityBarButtonSize;
                cartParams.height = globalVariable.activityBarButtonSize;
                cart.setLayoutParams(cartParams);
            }
        });	

	}

}
