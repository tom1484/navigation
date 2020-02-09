package com.example.navigation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;

import java.util.ArrayList;

public class Map extends TextureView {

    GlobalVariable globalVariable;
    ArrayList<MapEvent> mapEvents;

    public Map(Context context) {
        this(context, null);
    }

    public Map(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Map(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        globalVariable = (GlobalVariable) context.getApplicationContext();
        globalVariable.mapEvents = new ArrayList<>();
        mapEvents = globalVariable.mapEvents;

    }

    private void loadEvents() {

    }

    private void draw() {
        Canvas canvas = lockCanvas(new Rect(0, 0, getWidth(), getHeight()));



        unlockCanvasAndPost(canvas);
    }

}
