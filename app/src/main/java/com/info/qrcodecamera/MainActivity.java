package com.info.qrcodecamera;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.tunabaranurut.microdb.base.MicroDB;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.firebase.crash.FirebaseCrash.log;

public class MainActivity extends AppCompatActivity {

    SurfaceView cameraPreview;
    TextView txtResult;
    CameraSource cameraSource;
    BarcodeDetector barcodeDetector;
    final int RequestCameraPermissionID = 1001;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    try {
                        cameraSource.start(cameraPreview.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraPreview = (SurfaceView) findViewById(R.id.cameraPreview);
        txtResult = (TextView) findViewById(R.id.txtResult);


        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE).build();
        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640, 480).build();

        //Add Event

        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //Request permission
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},RequestCameraPermissionID);
                    return;
                }
                try {
                    cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }


            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {

                /*Date today = Calendar.getInstance().getTime();

                //Constructs a SimpleDateFormat
                SimpleDateFormat simpleDateFormat =  new SimpleDateFormat();

                String currentTime = simpleDateFormat.format(today);
                log("Current Time"+ currentTime);

                try {
                    Date date = simpleDateFormat.parse(currentTime);

                    long epochTime = date.getTime();

                    log("Current Time in Epoch." + epochTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }*/

                final SparseArray<Barcode> qrCodes = detections.getDetectedItems();
                final DecodeQR decodeQR = new DecodeQR();
                Set<String> uniqueCodesList = new HashSet<>();

                String s = qrCodes.valueAt(0).displayValue;
                final String splitCodes[] = s.split("%");

                String uniqueCode    = splitCodes[0];
                String companyCode   = splitCodes[1];
                String productCode   = splitCodes[2];
                String numberofPiece = splitCodes[3];
                String staticCode    = splitCodes[4];

                decodeQR.setUniqueCode(uniqueCode);
                decodeQR.setCompanyCode(companyCode);
                decodeQR.setProductCode(productCode);
                decodeQR.setNumberofPiece(numberofPiece);
                decodeQR.setStaticCode(staticCode);

                //uniqueCodesList.add(decodeQR.getUniqueCode());     // Bu code çalışmasını en gelliyor aşşağıdaki "if" statement la alakalı olmalı

                //decodeQR.saveDecodeQRCodes();

                if(!uniqueCodesList.contains(uniqueCode)){

                    if(qrCodes.size()!=0 )
                    {
                        txtResult.post(new Runnable() {
                            @Override
                            public void run() {
                                //Vibrate
                                Vibrator vibrator= (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                                vibrator.vibrate(500);
                                txtResult.setText(qrCodes.valueAt(0).displayValue);
                            }
                        });
                    }
                }

            }
        });
    }
}

//depolama lazım
//POJO yaratırsam local stroge da save
//pojoyu doldur split objeyi doldur.
//Barcode
//current time milisec alıp id ver
//System.
//EPOC time
//DB indir
//SET yarat bir boyutlu dizi array değil,
//enuqiu olan kodu sete at set.contains le bu kod var mı
// String unique id new set Hashset yarat içerine her yeni bir şey geldiğin cantain ile tru ile false verir.