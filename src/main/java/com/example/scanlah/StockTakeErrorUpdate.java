package com.example.scanlah;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
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

public class StockTakeErrorUpdate extends AppCompatActivity {

    TextView AssetFailUpdateText;

    String strSERIALNO = null;

    Switch switchUpdateFailBtn = null;

    TextView switchFailStatus;

    Boolean isFailUpdateFocus = false;

    Button btnErrorUpdateURLUntracked = null;

    Button btnStockErrorUpdateURLMaster = null;

    Button btnResumeUpdateFailScanStock = null;

    boolean isScannerUpdateFailInstalled = false;

    GmsBarcodeScanner gmsScanner = null;

    GmsBarcodeScannerOptions gmsOption = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_take_error_update);

        AssetFailUpdateText = findViewById(R.id.txtAssetFailUpdate);

        if(getIntent().hasExtra("SERIALNO")){
            strSERIALNO = getIntent().getExtras().get("SERIALNO").toString();
            AssetFailUpdateText.setText(strSERIALNO);
        }

        switchUpdateFailBtn = findViewById(R.id.switchErrorUpdateFailButton);

        switchFailStatus = findViewById(R.id.lblCameraErrorZoomUpdateFailStatus);

        switchUpdateFailBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                switchFailStatus.setText("Enabled");
                isFailUpdateFocus = true;
            } else {
                switchFailStatus.setText("Disabled");
                isFailUpdateFocus = false;
            }
        });

        btnErrorUpdateURLUntracked = findViewById(R.id.btnStockErrorUpdateURLUntracked);

        btnErrorUpdateURLUntracked.setPaintFlags(btnErrorUpdateURLUntracked.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        btnErrorUpdateURLUntracked.setOnClickListener(view ->{

            Intent urlPage;

            if(HTTPClient.getHttpClient().getIsViewReportLogin().equals("TRUE")){
                urlPage = new Intent(StockTakeErrorUpdate.this, ViewReportCredential.class);
                urlPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                urlPage.putExtra("StockSummaryPage", "UNTRACKED");
                urlPage.putExtra("strAndriodPage", "StockTakeErrorUpdate");
                urlPage.putExtra("PAGE_REDIRECT", "STOCK_MASTER");
                urlPage.putExtra("SERIALNO", strSERIALNO);
                startActivity(urlPage);
            }else{
                urlPage = new Intent(StockTakeErrorUpdate.this, stockSummary.class);
                urlPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                urlPage.putExtra("StockSummaryPage", "UNTRACKED");
                urlPage.putExtra("strAndriodPage", "StockTakeErrorUpdate");
                urlPage.putExtra("PAGE_REDIRECT", "STOCK_MASTER");
                urlPage.putExtra("SERIALNO", strSERIALNO);
                startActivity(urlPage);
            }
        });

        btnStockErrorUpdateURLMaster = findViewById(R.id.btnStockErrorUpdateURLMaster);

        btnStockErrorUpdateURLMaster.setPaintFlags(btnStockErrorUpdateURLMaster.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        btnStockErrorUpdateURLMaster.setOnClickListener(view ->{

            Intent urlPage;

            if(HTTPClient.getHttpClient().getIsViewReportLogin().equals("TRUE")){
                urlPage = new Intent(StockTakeErrorUpdate.this, ViewReportCredential.class);
                urlPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                urlPage.putExtra("StockSummaryPage", "MASTER");
                urlPage.putExtra("strAndriodPage", "StockTakeErrorUpdate");
                urlPage.putExtra("PAGE_REDIRECT", "STOCK_MASTER");
                urlPage.putExtra("SERIALNO", strSERIALNO);
                startActivity(urlPage);
            }else{
                urlPage = new Intent(StockTakeErrorUpdate.this, stockSummary.class);
                urlPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                urlPage.putExtra("StockSummaryPage", "MASTER");
                urlPage.putExtra("strAndriodPage", "StockTakeErrorUpdate");
                urlPage.putExtra("PAGE_REDIRECT", "STOCK_MASTER");
                urlPage.putExtra("SERIALNO", strSERIALNO);
                startActivity(urlPage);
            }
        });

        installGoogleScanner();

        btnResumeUpdateFailScanStock = findViewById(R.id.btnResumeUpdateScanStock);

        btnResumeUpdateFailScanStock.setOnClickListener(view ->{

            if(isScannerUpdateFailInstalled){

                if(isFailUpdateFocus){
                    gmsOption = new GmsBarcodeScannerOptions.Builder()
                            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).enableAutoZoom().build();
                }else{
                    gmsOption = new GmsBarcodeScannerOptions.Builder()
                            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build();
                }

                gmsScanner = GmsBarcodeScanning.getClient(StockTakeErrorUpdate.this, gmsOption);

                startInScanning();

            }else{
                Toast.makeText(StockTakeErrorUpdate.this, "Scanner is not install", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void installGoogleScanner(){

        ModuleInstallClient client =  ModuleInstall.getClient(StockTakeErrorUpdate.this);

        ModuleInstallRequest request = ModuleInstallRequest.newBuilder()
                .addApi(GmsBarcodeScanning.getClient(StockTakeErrorUpdate.this))
                .build();

        client.installModules(request).addOnSuccessListener(response->{
            isScannerUpdateFailInstalled = true;
        }).addOnFailureListener(response ->{
            isScannerUpdateFailInstalled = false;
            Toast.makeText(StockTakeErrorUpdate.this, response.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    public void startInScanning(){

        gmsScanner
                .startScan()
                .addOnSuccessListener(
                        barcode -> {
                            // Task completed successfully

                            final MediaPlayer beepSound = MediaPlayer.create(StockTakeErrorUpdate.this, com.google.zxing.client.android.R.raw.zxing_beep);
                            beepSound.start();
                            String stockTakeResult = getBarCodeResult
                                    (barcode.getRawValue(),barcode.getFormat());

                            Intent intent = new Intent(StockTakeErrorUpdate.this,LoadingPage.class);
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

    @Override
    public void onBackPressed() {

        //Toast.makeText(Checkout.this, "Hi HI", Toast.LENGTH_SHORT).show();
        Intent urlPage = new Intent(StockTakeErrorUpdate.this, stocktakemain.class);
        startActivity(urlPage);

        //for testing
        finish();
    }
}