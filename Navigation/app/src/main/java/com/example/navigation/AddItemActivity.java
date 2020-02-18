package com.example.navigation;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.JsonReader;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONObject;

import java.util.ArrayList;

import edu.counterview.CounterView;

public class AddItemActivity extends AppCompatActivity {

    private GlobalVariable globalVariable;

    private CameraPreview mCameraPreview;

    private BarcodeDetector mBarcodeDetector;
    private Barcode barcode;
    private Thread mThread;
    private Handler mHandler;
    private Runnable detect;
    private boolean dialogDisplaying;

    private View dialogView;
    private AlertDialog.Builder dialogBuilder;
    private CounterView dialogCounterView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additem);

        globalVariable = (GlobalVariable) getApplicationContext();
        globalVariable.selectedItem = new ArrayList<>();

        mCameraPreview = (CameraPreview) findViewById(R.id.cameraPreview);
        mBarcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.EAN_13)
                .build();

        dialogView = LayoutInflater.from(this).inflate(R.layout.activity_addinfo, null, false);
        dialogCounterView = (CounterView) dialogView.findViewById(R.id.add_counter);

        dialogBuilder = new AlertDialog.Builder(this)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        dialogDisplaying = false;
                    }
                }).setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        globalVariable.selectedItem.add(new Pair<JSONObject, Integer>(
                                globalVariable.barcodeToItem.get(barcode.displayValue),
                                Integer.valueOf(dialogCounterView.getCounterValue())
                        ));
                        Log.i("TAG", globalVariable.selectedItem.toString());
                    }
                }).setNegativeButton("cancel",null);


        detect = new Runnable() {
            @Override
            public void run() {
                while(true) {
                    if (dialogDisplaying)
                        continue;
                    Bitmap bitmap = mCameraPreview.getBitmap();
                    if (bitmap != null) {
                        Frame frameToProcess = new Frame.Builder().setBitmap(bitmap).build();
                        SparseArray<Barcode> detection = mBarcodeDetector.detect(frameToProcess);

                        if (detection != null && detection.size() > 0) {
                            barcode = detection.valueAt(0);
                            dialogDisplaying = true;
                            mHandler.sendEmptyMessage(0);
                        }
                    }
                }
            }
        };
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                showItemInfo(null);
//                Log.i("TAG", barcode.displayValue);
            }
        };
        mThread = new Thread(detect);
        dialogDisplaying = false;
        mThread.start();

    }

    private void showItemInfo(JSONObject item) {

        dialogBuilder.setView(dialogView);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

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
