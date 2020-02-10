package com.example.navigation;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

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
    public float x;
    public float y;
    public float width;
    public float height;

    public MapEvent(int _id, String _type, String _name, String _description, JSONArray _items, float _x, float _y, float _width, float _height) {

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

    public MapEvent(String _type, float _x, float _y, float _width) {
        id = -1;
        type = _type;
        name = "";
        description = "";
        items = null;
        x = _x;
        y = _y;
        width = _width;
        height = 0f;
    }

    public void draw(Canvas canvas, Matrix transformMatrix) {

        switch (type) {
            case "shelf":
                RectF rectF = new RectF(
                        x - width / 2, y - height / 2,
                        x + width / 2, y + height / 2
                );
                transformMatrix.mapRect(rectF);
                canvas.drawRect(rectF, getPaint());
                break;
            case "person":
                float[] point = {x, y};
                transformMatrix.mapPoints(point);
                canvas.drawCircle(point[0], point[1], width / 2, getPaint());
                break;
        }

    }

    public Paint getPaint() {

        Paint paint = new Paint();
        switch (type) {
            case "shelf":
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.argb(255, 255, 88, 9));
                break;
            case "person":
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.argb(255, 0, 128, 255));
                paint.setStrokeWidth(width);
                break;
        }

        return paint;
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
