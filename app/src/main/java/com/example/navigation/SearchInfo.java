package com.example.navigation;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

public class SearchInfo extends LinearLayout {

    GlobalVariable globalVariable;

    private JSONObject item;

    private TextView itemName;
    private TextView itemPrice;
    private Button confirm;

    private int key;

    private Context context;

    public SearchInfo(Context cxt) {
        this(cxt, null);
    }

    public SearchInfo(Context cxt, @Nullable AttributeSet attrs) {
        this(cxt, attrs, 0);
    }

    public SearchInfo(Context cxt, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(cxt, attrs, defStyleAttr);

        context = cxt;

        View.inflate(context, R.layout.view_searchinfo, this);
        globalVariable = (GlobalVariable) context.getApplicationContext();

        itemName = (TextView) findViewById(R.id.item_name);
        itemPrice = (TextView) findViewById(R.id.item_price);
        confirm = (Button) findViewById(R.id.confirm_add);

        confirm.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("TAG", (String) itemName.getText());
                        if (globalVariable.selectedItem.containsKey(key)) {
                            globalVariable.selectedItem.put(
                                    key, new Pair<>(
                                            globalVariable.idToItem.get(key),
                                            globalVariable.selectedItem.get(key).second + 1
                                    )
                            );
                        }
                        else {
                            globalVariable.selectedItem.put(
                                    key, new Pair<>(
                                            globalVariable.idToItem.get(key), 1
                                    )
                            );
                        }
                    }
                }
        );

    }

    public void setItem(JSONObject _item, int _key) {
        item = _item;
        key = _key;
        try {
            itemName.setText(item.getString("name"));
            itemPrice.setText(item.getString("price"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
