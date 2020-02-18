package com.example.navigation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONObject;

import java.util.ArrayList;

public class AddItemActivity extends AppCompatActivity {

    private GlobalVariable globalVariable;

    private CameraPreview mCameraPreview;

    private BarcodeDetector mBarcodeDetector;
    private SparseArray<Barcode> detection;
    public Thread detect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additem);

        mCameraPreview = (CameraPreview) findViewById(R.id.cameraPreview);
        mBarcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.EAN_13)
                .build();

        globalVariable = (GlobalVariable) getApplicationContext();
        globalVariable.selectedItem = new ArrayList<>();



        detect = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    Bitmap bitmap = mCameraPreview.getBitmap();
                    if (bitmap != null) {
                        Frame frameToProcess = new Frame.Builder().setBitmap(bitmap).build();
                        detection = mBarcodeDetector.detect(frameToProcess);

                        if (detection != null) {
                            for (int i = 0; i < detection.size(); i ++) {
                                String result = detection.valueAt(i).displayValue;
                                JSONObject item = globalVariable.barcodeToItem.get(result);
                                globalVariable.selectedItem.add(item);
                                popupInfo(item);
                            }
                        }
                    }
                }
            }
        });
        detect.start();
    }

    private void popupInfo(JSONObject item) {

//        View view = LayoutInflater.from(this).inflate(R.layout.activity_addinfo, null, false);
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.activity_addinfo, null);
        PopupWindow AddInfoWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        Log.i("TAG", AddInfoWindow.toString());
        AddInfoWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
//        AddInfoWindow.setOutsideTouchable(true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraPreview.onResume(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    public void changeToMap(View v) {
        Log.i("TAG", "change to map");
        startActivity(new Intent(this, MapActivity.class));
    }

    public void changeToAddItem(View v) {}

    public void changeToCart(View v) {
        Log.i("TAG", "change to cart");
        startActivity(new Intent(this, CartActivity.class));
    }

}
