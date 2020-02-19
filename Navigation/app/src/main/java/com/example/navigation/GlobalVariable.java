package com.example.navigation;

import android.app.Application;
import android.util.Pair;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
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
    public Map<Integer, JSONObject> idToItem;

    public Map<Integer, Pair<JSONObject, Integer>> addedItem;
    public Map<Integer, Pair<JSONObject, Integer>> selectedItem;

    public GlobalVariable() {
        userAccount = "";

        windowHeight = 0;
        windowWidth = 0;

        activityBarHeight = 0;
        activityBarButtonSize = 0;

        mapEvents = new ArrayList<>();
        items = new ArrayList<>();
        barcodeToItem = new HashMap<>();
        idToItem = new HashMap<>();

        addedItem = new HashMap<>();
        selectedItem = new HashMap<>();
    }
}
