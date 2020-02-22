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
            searchInfo.setItem((globalVariable.idToItem.get(key)));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            searchInfo.setLayoutParams(params);

            itemInfoList.addView(searchInfo);
        }

    }

    public void addSelected(View v) {
        if (globalVariable.addedItem.containsKey(6)) {
            globalVariable.addedItem.put(
                    4, new Pair<>(
                            globalVariable.idToItem.get(4), 1
                    )
            );
        } else {
            globalVariable.addedItem.put(
                    6, new Pair<>(
                            globalVariable.idToItem.get(6), 1
                    )
            );
        }
        finish();
    }

}
