package com.example.navigation;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

public class SearchInfo extends LinearLayout {

    GlobalVariable globalVariable;

    private JSONObject item;

    private ImageView imageView;
    private TextView textView;

    public SearchInfo(Context context) {
        this(context, null);
    }

    public SearchInfo(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchInfo(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.view_searchinfo, this);
        globalVariable = (GlobalVariable) context.getApplicationContext();

        imageView = (ImageView) findViewById(R.id.item_image);
        textView = (TextView) findViewById(R.id.item_name);

    }

    public void setItem(JSONObject _item) {
        item = _item;
        try {
            if (globalVariable.selectedItem.containsKey(3)) {
                imageView.setImageResource(R.drawable.im_item0);
            } else {
                imageView.setImageResource(R.drawable.im_item3);
            }
            textView.setText(item.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
