package com.example.navigation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;

public class Map extends View {

    private Canvas canvas;

    private float mapWidth;
    private float mapHeight;
    private float realToScreenScalar;

    private GlobalVariable globalVariable;
    private ArrayList<MapEvent> mapEvents;

    private Matrix transformMatrix;
    private int motionPointerCount = -1;

    private float[] lastPivot;
    private float lastPointerDis;

    private float PERSON_STROKE_WIDTH = 50;
    public PointF person;
    private Paint personPaint;

    public Map(Context context, AttributeSet attrs) {
        super(context, attrs);
        globalVariable = (GlobalVariable) context.getApplicationContext();

        globalVariable.mapEvents = new ArrayList<>();
        mapEvents = globalVariable.mapEvents;

        transformMatrix = new Matrix();
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        this.canvas = canvas;
        drawEvents();

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(15);
        paint.setColor(Color.argb(255, 187, 255, 0));
        Path path = new Path();
        path.moveTo(1f, 2f);
        path.rLineTo(0f, 7f);
        path.rLineTo(8.5f, 0f);
        path.rLineTo(0f, -2f);
        path.transform(transformMatrix);
        canvas.drawPath(path, paint);

        if (person != null) {
            float[] _person = new float[]{person.x, person.y};
            transformMatrix.mapPoints(_person);
            canvas.drawCircle(_person[0], _person[1], PERSON_STROKE_WIDTH / 2, personPaint);
        }
    }

    public void centerPosition(float x, float y, float width) {

        mapWidth = getWidth();
        mapHeight = getHeight();

        realToScreenScalar = Math.min(
                mapWidth / width,
                mapHeight / width
        );

        transformMatrix = new Matrix();
        transformMatrix.postTranslate(
                mapWidth / 2f - x * realToScreenScalar,
                mapHeight / 2f - y * realToScreenScalar
        );
        transformMatrix.postScale(
                realToScreenScalar, realToScreenScalar,
                mapWidth / 2f - x * realToScreenScalar,
                mapHeight / 2f - y * realToScreenScalar
        );

        invalidate();
    }

    public PointF setPerson(float x, float y, float strokeWidth) {
        person = new PointF(x, y);
        personPaint = new Paint();
        personPaint.setColor(Color.argb(255, 0, 187, 255));
        personPaint.setStrokeWidth(strokeWidth);

        return person;
    }

    public void setBorder(String path, float strokeWidth) {
        mapEvents.add(new MapEvent("path", path, strokeWidth));
    }

    private void drawEvents() {
        for (MapEvent mapEvent: mapEvents) {
            mapEvent.draw(canvas, transformMatrix);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        if (motionEvent.getPointerCount() == motionPointerCount) {

            float[] nowPivot = getPivotFromMotion(motionEvent);
            float nowPointerDis = getPointerDisFromMotion(motionEvent);

            transformMatrix.postTranslate(
                    nowPivot[0] - lastPivot[0],
                    nowPivot[1] - lastPivot[1]
            );

            if (motionEvent.getPointerCount() == 2) {
                float scalar = nowPointerDis / lastPointerDis;
                transformMatrix.postScale(
                        scalar, scalar,
                        nowPivot[0], nowPivot[1]
                );
            }

        }

        lastPivot = getPivotFromMotion(motionEvent);
        lastPointerDis = getPointerDisFromMotion(motionEvent);
        motionPointerCount = motionEvent.getPointerCount();

        // no pointer touching
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            motionPointerCount = 0;
        }

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
            return new float[] {
                    (motionEvent.getX(0) + motionEvent.getX(1)) / 2,
                    (motionEvent.getY(0) + motionEvent.getY(1)) / 2
            };
        }

    }

    private float getPointerDisFromMotion(MotionEvent motionEvent) {

        if (motionEvent.getPointerCount() == 2) {
            return (float)Math.sqrt(
                    Math.pow(motionEvent.getX(0) - motionEvent.getX(1), 2.0) +
                    Math.pow(motionEvent.getY(0) - motionEvent.getY(1), 2.0)
            );
        } else {
            return 1;
        }

    }

    public void loadEvents(int file_id) {

        String jsonString = getJsonString(file_id);
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonEvents = jsonObject.getJSONArray("events");

            String borderPath = jsonObject.getString("border");
            setBorder(borderPath, 5f);

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
                        jsonEvent.getString("path")
                );

                mapEvents.add(mapEvent);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private String getJsonString(int id) {

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
