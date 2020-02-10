package com.example.navigation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapEvent {

    public int id;
    public String type;
    public String name;
    public String description;
    public ArrayList<Integer> items;
    public double x;
    public double y;
    public double width;
    public double height;

    public MapEvent() {}

    public MapEvent(int _id, String _type, String _name, String _description, JSONArray _items, double _x, double _y, double _width, double _height) {

        id = _id;
        type = _type;
        name = _name;
        description = _description;
        items = new ArrayList<>();
        for (int i = 0; i < _items.length(); i ++) {
            try {
                items.add(_items.getInt(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        x = _x;
        y = _y;
        width = _width;
        height = _height;

    }

    public String toString() {

        String string = "{";
        string += "id: " + String.valueOf(id) + ", ";
        string += "type: " + String.valueOf(type) + ", ";
        string += "name: " + String.valueOf(name) + ", ";
        string += "description: " + String.valueOf(description) + ", ";
        string += "items: " + String.valueOf(items) + ", ";
        string += "x: " + String.valueOf(x) + ", ";
        string += "y: " + String.valueOf(y) + ", ";
        string += "width: " + String.valueOf(width) + ", ";
        string += "height: " + String.valueOf(height) + "}";

        return string;

    }

}
