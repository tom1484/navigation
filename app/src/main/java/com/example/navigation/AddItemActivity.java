package com.example.navigation;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONException;
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
    private ImageView dialogItemImage;
    private CounterView dialogCounterView;
    private TextView dialogItemName;
    private TextView dialogItemPrice;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additem);

        globalVariable = (GlobalVariable) getApplicationContext();

        mCameraPreview = (CameraPreview) findViewById(R.id.cameraPreview);
        mBarcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.EAN_13)
                .build();

        dialogView = LayoutInflater.from(this).inflate(R.layout.activity_addinfo, null, false);
//        dialogItemImage = (ImageView) dialogItemImage.findViewById(R.id.add_item_image);
        dialogCounterView = (CounterView) dialogView.findViewById(R.id.add_counter);
        dialogItemName = (TextView) dialogView.findViewById(R.id.add_item_name);
        dialogItemPrice = (TextView) dialogView.findViewById(R.id.add_item_price);

//        dialogItemImage.setImageResource(R.drawable.im_item0);

        init();
        mThread.start();

    }

    private void setDialogBuilder() {

        dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialogDisplaying = false;
            }
        });
        dialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    JSONObject jsonObject = globalVariable.barcodeToItem.get(barcode.displayValue);
                    int id = jsonObject.getInt("id");
                    if (globalVariable.selectedItem.containsKey(id)) {
                        globalVariable.addedItem.put(
                                id, new Pair<>(
                                        jsonObject, 0
                                )
                        );
                        globalVariable.selectedItem.remove(id);
                    } else if (globalVariable.addedItem.containsKey(id)) {
                        int number = globalVariable.addedItem.get(id).second;
                        globalVariable.addedItem.remove(id);
                        globalVariable.addedItem.put(
                                id, new Pair<>(
                                        jsonObject,
                                        number + Integer.valueOf(dialogCounterView.getCounterValue())
                                )
                        );
                    } else {
                        globalVariable.addedItem.put(
                                id, new Pair<>(
                                        jsonObject,
                                        Integer.valueOf(dialogCounterView.getCounterValue())
                                )
                        );
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        dialogBuilder.setNegativeButton("cancel",null);
    }

    private void init() {

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
                            if (globalVariable.barcodeToItem.containsKey(barcode.displayValue)) {
                                dialogDisplaying = true;
                                mHandler.sendEmptyMessage(0);
                            }
                        }
                    }
                }
            }
        };
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                showItemInfo(globalVariable.barcodeToItem.get(barcode.displayValue));
            }
        };
        dialogDisplaying = false;

        if (mThread != null)
            mThread.destroy();
        mThread = new Thread(detect);
    }

    private void showItemInfo(JSONObject item) {

        setDialogBuilder();

        try {
            dialogItemName.setText(item.getString("name"));
            dialogItemPrice.setText(
                    "$" + String.valueOf(item.getInt("price"))
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
