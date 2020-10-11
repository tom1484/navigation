package com.example.navigation;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.util.Pair;

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
    public Path path;
    public float strokeWidth;
    public boolean clickable;

    public MapEvent(int _id, String _type, String _name, String _description, JSONArray _items, String _path) {

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
        path = getPathFromString(_path);

        clickable = true;

    }

    public MapEvent(String _type, String _path, float _strokeWidth) {
        type = _type;
        path = getPathFromString(_path);
        strokeWidth = _strokeWidth;
        clickable = false;
    }

    public void draw(Canvas canvas, Matrix transformMatrix) {

        Path _path = new Path(path);
        Log.i("TAG", path.toString());
        _path.transform(transformMatrix);
        canvas.drawPath(_path, getPaint());

    }

    private Path getPathFromString(String pathStr) {
        Path path = new Path();
        for (int i = 0; i < pathStr.length();) {
            Pair<ArrayList<Float>, Integer> res = readNumbers(pathStr, i);
            ArrayList<Float> numbers = res.first;
            switch (pathStr.charAt(i)) {
                case 'M':
                    path.moveTo(numbers.get(0), numbers.get(1));
                    break;
                case 'm':
                    path.rMoveTo(numbers.get(0), numbers.get(1));
                    break;
                case 'L':
                    path.lineTo(numbers.get(0), numbers.get(1));
                    break;
                case 'l':
                    path.rLineTo(numbers.get(0), numbers.get(1));
                    break;
                case 'A':
                    path.addArc(numbers.get(0), numbers.get(1), numbers.get(2), numbers.get(3), numbers.get(4), numbers.get(5));
                    break;
                case 'z':
                    path.close();
                    break;
            }
            i = res.second;
        }

        return path;
    }

    private Pair<ArrayList<Float>, Integer> readNumbers(String path, int start) {
        int j = start + 1;
        while (j < path.length() && ((path.charAt(j) >= '0' && path.charAt(j) <= '9') || path.charAt(j) == '.' || path.charAt(j) == ',' || path.charAt(j) == '-'))
            j ++;
        ArrayList<Float> numbers = new ArrayList<>();

        String num = "";
        for (int i = start + 1; i < j; i ++) {
            if (path.charAt(i) != ',') {
                num += path.charAt(i);
            } else {
                numbers.add(Float.valueOf(num));
                num = "";
            }
        }
        if (num.length() != 0)
            numbers.add(Float.valueOf(num));

        return new Pair<>(numbers, j);
    }

    private Paint getPaint() {

        Paint paint = new Paint();
        switch (type) {
            case "shelf":
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.argb(255, 255, 88, 9));
                break;

            case "person":
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.argb(255, 0, 128, 255));
                break;

            case "path":
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(strokeWidth);
                paint.setColor(Color.argb(255, 0, 0, 0));
                break;
        }

        return paint;
    }

}
