package com.example.navigation;

import android.app.Application;
import android.util.Pair;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class GlobalVariable extends Application {

    public String userAccount;

    public int windowHeight;
    public int windowWidth;

    public int activityBarHeight;
    public int activityBarButtonSize;

    public ArrayList<MapEvent> mapEvents;
    public ArrayList<JSONObject> items;
    public Map<String, JSONObject> barcodeToItem;

    public ArrayList<Pair<JSONObject, Integer>> selectedItem;
}
