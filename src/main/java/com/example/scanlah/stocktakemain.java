package com.example.scanlah;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.moduleinstall.ModuleInstall;
import com.google.android.gms.common.moduleinstall.ModuleInstallClient;
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

public class stocktakemain extends AppCompatActivity {

    Button btnViewStock = null;
    Button btnStartStock = null;

    boolean isScannerInstalled = false;

    GmsBarcodeScanner gmsScanner = null;

    GmsBarcodeScannerOptions gmsOption = null;

    //Camera
    Switch switchBtn = null;

    boolean isFocus = false;

    TextView switchStatus = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stocktakemain);

        btnViewStock = findViewById(R.id.btnViewStock);

        btnStartStock = findViewById(R.id.btnStartStock);

        switchBtn = findViewById(R.id.switchButton);

        switchStatus = findViewById(R.id.lblCameraZoomStatus);
        
        HTTPClient.getHttpClient().setContext(stocktakemain.this);

        switchBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                switchStatus.setText("Enabled");
                isFocus = true;
            } else {
                switchStatus.setText("Disabled");
                isFocus = false;
            }
        });


        btnViewStock.setOnClickListener(view ->{
            //Toast.makeText(Checkout.this, "Hi HI", Toast.LENGTH_SHORT).show();
            Intent urlPage = new Intent(stocktakemain.this, StockTakeReport.class);
            startActivity(urlPage);

            //for testing
            finish();
        });

        installGoogleScanner();

        btnStartStock.setOnClickListener(view ->{
            //Start scanning
            if(isScannerInstalled){

                if(isFocus){
                    gmsOption = new GmsBarcodeScannerOptions.Builder()
                            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).enableAutoZoom().build();
                }else{
                    gmsOption = new GmsBarcodeScannerOptions.Builder()
                            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build();
                }

                gmsScanner = GmsBarcodeScanning.getClient(stocktakemain.this, gmsOption);

                startInScanning();

            }else{
                Toast.makeText(stocktakemain.this, "Scanner is not install", Toast.LENGTH_LONG).show();
            }

        });
    }

    //Goggle Scan
    public void installGoogleScanner(){

        ModuleInstallClient client =  ModuleInstall.getClient(stocktakemain.this);

        ModuleInstallRequest request = ModuleInstallRequest.newBuilder()
                .addApi(GmsBarcodeScanning.getClient(stocktakemain.this))
                .build();

        client.installModules(request).addOnSuccessListener(response->{
            isScannerInstalled = true;
        }).addOnFailureListener(response ->{
            isScannerInstalled = false;
            Toast.makeText(stocktakemain.this, response.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    public void startInScanning(){

        gmsScanner
                .startScan()
                .addOnSuccessListener(
                        barcode -> {
                            // Task completed successfully

                            final MediaPlayer beepSound = MediaPlayer.create(stocktakemain.this, com.google.zxing.client.android.R.raw.zxing_beep);
                            beepSound.start();
                            String stockTakeResult = getBarCodeResult
                                    (barcode.getRawValue(),barcode.getFormat());

                            Intent intent = new Intent(stocktakemain.this,LoadingPage.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("EXTRACT_SERIAL", stockTakeResult);
                            intent.putExtra("EXTRACT_TYPE", "STOCK");
                            startActivity(intent);

                            //for testing
                            finish();

                        })
                .addOnCanceledListener(
                        () -> {
                            // Task canceled

                        })
                .addOnFailureListener(
                        e -> {
                            // Task failed with an exception
                        });

    }

    public String getBarCodeResult(String strResult, int format){

        String getResult = null;

        switch (format){
            case Barcode.FORMAT_CODE_128:
                if(strResult.startsWith("]C1")){
                    getResult=strResult.replace("]C1","");
                }else{
                    getResult = strResult;
                }
                break;
            default:
                getResult = strResult;

                break;
        }

        return getResult;
    }

    //Very useful
    @Override
    public void onBackPressed() {

        //Toast.makeText(Checkout.this, "Hi HI", Toast.LENGTH_SHORT).show();
        Intent urlPage = new Intent(stocktakemain.this, Home.class);
        startActivity(urlPage);

        //for testing
        finish();
    }
}