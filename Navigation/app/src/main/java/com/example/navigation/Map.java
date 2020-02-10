package com.example.navigation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TextureView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;

public class Map extends TextureView {

    private GlobalVariable globalVariable;
    private ArrayList<MapEvent> mapEvents;

    private Matrix transformMatrix;
    private Point lastPivot;

    public Map(Context context) {
        this(context, null);
    }

    public Map(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Map(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        globalVariable = (GlobalVariable) context.getApplicationContext();
        loadEvents();

        transformMatrix = new Matrix();

    }

    private MapEvent checkOnTouchEvent() {

        return null;

    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

//        Log.i("TAG", motionEvent.toString());

        Point pivot = getPosFromMotionEvent(motionEvent);
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            lastPivot = getPosFromMotionEvent(motionEvent);
        }

        if (motionEvent.getPointerCount() == 1) {
//            Log.i("TAG", String.valueOf(motionEvent == lastMotion));
            transformMatrix.preTranslate(
                    pivot.x - lastPivot.x,
                    pivot.y - lastPivot.y
            );
        }

        draw();
        lastPivot = getPosFromMotionEvent(motionEvent);

        return true;
    }

    private Point getPosFromMotionEvent(MotionEvent motionEvent) {

        if (motionEvent.getPointerCount() == 1) {
            return new Point(
                    (int)motionEvent.getX(),
                    (int)motionEvent.getY()
            );
        } else {
            return new Point(
                    (int)(motionEvent.getX(0) + motionEvent.getX(1)) / 2,
                    (int)(motionEvent.getY(0) + motionEvent.getY(1)) / 2
            );
        }

    }

    private void draw() {
        Canvas canvas = lockCanvas(new Rect(0, 0, getWidth(), getHeight()));

        float[] point = {300, 300};
        Log.i("TAG", transformMatrix.toString());
        transformMatrix.mapPoints(point, point);

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(20);
        canvas.drawPoint(point[0], point[1], paint);

        unlockCanvasAndPost(canvas);
    }

    private void loadEvents() {

        globalVariable.mapEvents = new ArrayList<>();
        mapEvents = globalVariable.mapEvents;

        String jsonString = getJsonString(R.raw.map);
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonEvents = jsonObject.getJSONArray("events");

            for (int i = 0; i < jsonEvents.length(); i ++) {
                JSONObject jsonEvent = (JSONObject) jsonEvents.get(i);
                if (jsonEvent.getString("type").equals("format")) {
                    continue;
                }

                MapEvent mapEvent = new MapEvent(
                        jsonEvent.getInt("id"),
                        jsonEvent.getString("type"),
                        jsonEvent.getString("name"),
                        jsonEvent.getString("description"),
                        jsonEvent.getJSONArray("items"),
                        jsonEvent.getDouble("x"),
                        jsonEvent.getDouble("y"),
                        jsonEvent.getDouble("width"),
                        jsonEvent.getDouble("height")
                );

                mapEvents.add(mapEvent);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        Log.i("TAG", globalVariable.mapEvents.toString());
    }

    public String getJsonString(int id) {

        InputStream is = getResources().openRawResource(id);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return writer.toString();
    }

}
