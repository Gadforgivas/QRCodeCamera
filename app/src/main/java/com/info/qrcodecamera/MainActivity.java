package com.info.qrcodecamera;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    SurfaceView cameraPreview;
    TextView txtResult;
    CameraSource cameraSource;
    BarcodeDetector barcodeDetector;
    final int RequestCameraPermissionID = 1001;
    private LinearLayout qrLayout;
    private LinearLayout resultLayout;
    private RecyclerView resultRecyclerView;
    private ResultsAdapter resultsAdapter;

    private Button startBtn;
    private Button stopBtn;
    private TextView statusTv;

    private HashSet<String> decodedSet;

    private int totalRead = 0;

    private DecodedQRRecord decodedQRRecord = null;

    private boolean isRunning = false;

    private List<QrResult> qrResults = null;

    private int currentPage = 0;

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

        startBtn = (Button) findViewById(R.id.start_btn);
        stopBtn = (Button) findViewById(R.id.stop_btn);
        statusTv = (TextView) findViewById(R.id.status_tv);

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE).build();
        cameraSource = new CameraSource.Builder(this, barcodeDetector).build();

        qrLayout = (LinearLayout) findViewById(R.id.qr_layout);
        resultLayout = (LinearLayout) findViewById(R.id.results_layout);
        resultRecyclerView = (RecyclerView) findViewById(R.id.result_recyclerview);


        initRecyclerView();
        //Add Event

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(decodedSet != null) {
                    decodedSet.clear();
                }
                totalRead = 0;
                updateStatusTv();
                decodedQRRecord = new DecodedQRRecord();
                decodedQRRecord.setDate(new Date().getTime());
                isRunning = true;
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPage(1);
                isRunning = false;
                qrResults = generateQRResultArray();
                resultsAdapter.update(qrResults);
            }
        });

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

//                Set<String> uniqueCodesList = new HashSet<>();


                if(!isRunning){
                    return;
                }
                final DecodedQR decodedQR = getDecodedQRFromReader(detections);

                if(decodedQR == null){
                    return;
                }

                boolean hasReadBefore = hasReadedBefore(decodedQR);

                if(!hasReadBefore){
                    txtResult.post(new Runnable() {
                        @Override
                        public void run() {
                            //Vibrate
                            Vibrator vibrator= (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(500);
                            txtResult.setText(decodedQR.getRawCode());
                            totalRead++;
                            updateStatusTv();
                            decodedQRRecord.getRecords().add(decodedQR);
                        }
                    });
                }
            }
        });
    }

    private void initRecyclerView(){
        resultsAdapter = new ResultsAdapter(this,new LinkedList<QrResult>());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        resultRecyclerView.setItemAnimator(new DefaultItemAnimator());
        resultRecyclerView.setLayoutManager(mLayoutManager);

        // Adapter might be null if object created via constructor without adapter parameter.
        if(resultsAdapter != null) {
            resultRecyclerView.setAdapter(resultsAdapter);
        }
    }

    private List<QrResult> generateQRResultArray(){
        List<DecodedQR> records = decodedQRRecord.getRecords();

        Set<String> qrResultsSet = new HashSet<>();
        Map<String,QrResult> qrResultsMap = new HashMap<>();
        List<QrResult> qrResultsList = new LinkedList<>();

        for(DecodedQR decodedQR : records){
            String productCode = decodedQR.getProductCode();

            if(qrResultsSet.contains(productCode)){
                // Daha önce eklendi
                QrResult qrResult = qrResultsMap.get(productCode);
                qrResult.setQuantity(qrResult.getQuantity()+Integer.parseInt(decodedQR.getNumberofPiece()));
                qrResultsMap.put(productCode,qrResult);
            }else{
                // Daha önce eklenmedi
                qrResultsSet.add(productCode);
                QrResult qrResult = new QrResult();
                qrResult.setName(productCode);
                qrResult.setQuantity(Integer.parseInt(decodedQR.getNumberofPiece()));
                qrResultsMap.put(productCode,qrResult);
            }
        }

        for(Object o : qrResultsSet.toArray()){
            String s = (String) o;
            qrResultsList.add(qrResultsMap.get(s));
        }

        return qrResultsList;
    }

    private DecodedQR getDecodedQRFromReader(Detector.Detections<Barcode> detections){

        final DecodedQR decodedQR = new DecodedQR();

        final SparseArray<Barcode> qrCodes = detections.getDetectedItems();
        if(qrCodes.size() <= 0){
            return null;
        }
        String s = qrCodes.valueAt(0).displayValue;
        final String splitCodes[] = s.split("%");
        if(splitCodes.length < 5){
            return null;
        }

        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("dd MM EEE",Locale.US);

        String todayStr = simpleDateFormat.format(today);
        long epochToday = today.getTime();

        String uniqueCode    = splitCodes[0];
        String companyCode   = splitCodes[1];
        String productCode   = splitCodes[2];
        String numberofPiece = splitCodes[3];
        String staticCode    = splitCodes[4];

        decodedQR.setUniqueCode(uniqueCode);
        decodedQR.setCompanyCode(companyCode);
        decodedQR.setProductCode(productCode);
        decodedQR.setNumberofPiece(numberofPiece);
        decodedQR.setStaticCode(staticCode);
        decodedQR.setRawCode(s);
        decodedQR.setTimestamp(epochToday);
        return decodedQR;
    }

    private boolean hasReadedBefore(DecodedQR decodedQR){
        return hasReadedBefore(decodedQR.getUniqueCode());
    }

    private boolean hasReadedBefore(String uniqueId){
        if(decodedSet == null){
            decodedSet = new HashSet<>();
        }

        if(decodedSet.contains(uniqueId)){
            return true;
        }else{
            decodedSet.add(uniqueId);
            return false;
        }
    }

    private void updateStatusTv(){
        statusTv.setText(String.format("S : %d", totalRead));
    }

    private void setPage(int page){
        currentPage = page;
        if(page == 0){
            resultLayout.setVisibility(View.GONE);
            qrLayout.setVisibility(View.VISIBLE);
        }else if(page == 1){
            resultLayout.setVisibility(View.VISIBLE);
            qrLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if(currentPage == 1){
            setPage(0);
        }
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