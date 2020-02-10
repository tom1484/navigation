package com.example.navigation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;

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

public class Map extends View {

    private GlobalVariable globalVariable;
    private ArrayList<MapEvent> mapEvents;

    private Matrix matrixOp;
    private float[][] transformMatrix;

    private float[] lastPivot;
    private float lastPointerDis;

    public Map(Context context, AttributeSet attrs) {
        super(context, attrs);

        globalVariable = (GlobalVariable) context.getApplicationContext();
        loadEvents();

        matrixOp = new Matrix();
        transformMatrix = matrixOp.identity(3);

//        String mes = "";
//        for (int i = 0; i < transformMatrix.length; i ++)
//            for (int j = 0; j < transformMatrix[0].length; j ++)
//                mes += String.valueOf(transformMatrix[i][j]) + ' ';
//        Log.i("TAG", mes);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float[] point = {300, 300, 1};
        point = matrixOp.multiply(transformMatrix, point);
//        Log.i("TAG", String.valueOf(point[0]) + " " + String.valueOf(point[1]));

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(20);
        canvas.drawPoint(point[0], point[1], paint);
    }

    private MapEvent checkOnTouchEvent() {

        return null;

    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        if (motionEvent.getAction() != MotionEvent.ACTION_DOWN) {

            float[] nowPivot = getPivotFromMotion(motionEvent);
            float nowPointerDis = getPointerDisFromMotion(motionEvent);

            transformMatrix = matrixOp.translate(
                    transformMatrix,
                    nowPivot[0] - lastPivot[0],
                    nowPivot[1] - lastPivot[1]
            );
            Log.i("TAG", matrixOp.toString(transformMatrix));

            if (motionEvent.getPointerCount() == 2) {
                float scalar = nowPointerDis / lastPointerDis;

                transformMatrix = matrixOp.translate(
                        transformMatrix,
                        -nowPivot[0], -nowPivot[1]
                );
//                Log.i("TAG", matrixOp.toString(transformMatrix));
                transformMatrix = matrixOp.scale(
                        transformMatrix,
                        scalar, scalar
                );
                transformMatrix = matrixOp.translate(
                        transformMatrix,
                        nowPivot[0], nowPivot[1]
                );

            }

        }

        lastPivot = getPivotFromMotion(motionEvent);
        lastPointerDis = getPointerDisFromMotion(motionEvent);

        invalidate();
        return true;
    }

    private float[] getPivotFromMotion(MotionEvent motionEvent) {

        if (motionEvent.getPointerCount() == 1) {
            return new float[] {
                    motionEvent.getX(),
                    motionEvent.getY()
            };
        } else {
            return new float[]{
                    (motionEvent.getX(0) + motionEvent.getX(1)) / 2,
                    (motionEvent.getY(0) + motionEvent.getY(1)) / 2
            };
        }

    }

    private float getPointerDisFromMotion(MotionEvent motionEvent) {

        if (motionEvent.getPointerCount() == 2) {
            return (float)Math.sqrt(
                    (double)Math.pow(motionEvent.getX(0) - motionEvent.getX(1), (double)2.0) +
                    (double)Math.pow(motionEvent.getY(0) - motionEvent.getY(1), (double)2.0)
            );
        } else {
            return 1;
        }

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
