package com.example.navigation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

        loadEvents();

    }

    private void loadEvents() {

        String fileName = "data/map.json";

        ClassLoader classLoader = ClassLoader.getSystemClassLoader();

        File file = new File(classLoader.getResource(fileName).getFile());

        System.out.println("File Found : " + file.exists());

        String content = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                content = new String(Files.readAllBytes(file.toPath()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(content);

    }

    private void draw() {
        Canvas canvas = lockCanvas(new Rect(0, 0, getWidth(), getHeight()));



        unlockCanvasAndPost(canvas);
    }

}
