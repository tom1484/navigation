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

    }

    public void search(View v) {

        SearchInfo searchInfo = new SearchInfo(this);
        if (globalVariable.selectedItem.containsKey(3))
            searchInfo.setItem(globalVariable.idToItem.get(5));
        else
            searchInfo.setItem(globalVariable.idToItem.get(3));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        searchInfo.setLayoutParams(params);

        itemInfoList.addView(searchInfo);

    }

    public void addSelected(View v) {
        if (globalVariable.selectedItem.containsKey(3)) {
            globalVariable.selectedItem.put(
                    5, new Pair<>(
                            globalVariable.idToItem.get(5), 0
                    )
            );
        } else {
            globalVariable.selectedItem.put(
                    3, new Pair<>(
                            globalVariable.idToItem.get(3), 0
                    )
            );
        }
    }

}
