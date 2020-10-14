package com.example.navigation;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class SearchItemActivity extends AppCompatActivity {

    private GlobalVariable globalVariable;

    private LinearLayout itemInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchitem);
        globalVariable = (GlobalVariable) getApplicationContext();

        itemInfoList = (LinearLayout) findViewById(R.id.item_info_list);

        for (Integer key: globalVariable.idToItem.keySet()) {
            Log.i("TAG", key.toString());
            SearchInfo searchInfo = new SearchInfo(this);
            searchInfo.setItem(globalVariable.idToItem.get(key), key);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            searchInfo.setLayoutParams(params);

            itemInfoList.addView(searchInfo);
        }

    }

}
