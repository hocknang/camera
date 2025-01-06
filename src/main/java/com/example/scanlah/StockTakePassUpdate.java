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

public class StockTakePassUpdate extends AppCompatActivity {

    TextView passUpdateAssetID;

    TextView passUpdateDescription;

    String strUpdateAssetID;

    String strUpdateDescription;

    Button btnViewUpdateMasterList;

    Switch switchUpdateBtn = null;

    TextView lblCameraPassZoomUpdateStatus = null;

    boolean isUpdateFocus = false;

    boolean isScannerInstalled = false;

    Button btnUpdateScanPassScanStock;

    GmsBarcodeScanner gmsScanner = null;

    GmsBarcodeScannerOptions gmsOption = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_take_pass_update);

        passUpdateAssetID = findViewById(R.id.txtPassAssetID_Update);

        passUpdateDescription = findViewById(R.id.txtUpdatePassDesc);

        if(getIntent().hasExtra("SERIALNO")){
            strUpdateAssetID = getIntent().getExtras().get("SERIALNO").toString();
            passUpdateAssetID.setText(strUpdateAssetID);
        }

        if(getIntent().hasExtra("itemDesc")){
            strUpdateDescription = getIntent().getExtras().get("itemDesc").toString();
            passUpdateDescription.setText(strUpdateDescription);
        }

        btnViewUpdateMasterList = findViewById(R.id.btnPassViewMasterList);

        btnViewUpdateMasterList.setOnClickListener(view ->{

            Intent urlPage;

            if(HTTPClient.getHttpClient().getIsViewReportLogin().equals("TRUE")){
                urlPage = new Intent(StockTakePassUpdate.this, ViewReportCredential.class);
                urlPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                urlPage.putExtra("StockSummaryPage", "MASTER");
                urlPage.putExtra("strAndriodPage", "StocktakePassUpdate");
                urlPage.putExtra("PAGE_REDIRECT", "STOCK_MASTER");
                urlPage.putExtra("SERIALNO", strUpdateAssetID);
                urlPage.putExtra("itemDesc", strUpdateDescription);
                startActivity(urlPage);
            }else{
                urlPage = new Intent(StockTakePassUpdate.this, stockSummary.class);
                urlPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                urlPage.putExtra("StockSummaryPage", "MASTER");
                urlPage.putExtra("strAndriodPage", "StocktakePassUpdate");
                urlPage.putExtra("PAGE_REDIRECT", "STOCK_MASTER");
                urlPage.putExtra("SERIALNO", strUpdateAssetID);
                urlPage.putExtra("itemDesc", strUpdateDescription);
                startActivity(urlPage);
            }
        });

        switchUpdateBtn = findViewById(R.id.switchPassUpdateButton);

        lblCameraPassZoomUpdateStatus = findViewById(R.id.lblCameraUpdatePassZoomStatus);

        switchUpdateBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                lblCameraPassZoomUpdateStatus.setText("Enabled");
                isUpdateFocus = true;
            } else {
                lblCameraPassZoomUpdateStatus.setText("Disabled");
                isUpdateFocus = false;
            }
        });

        //Camera
        installGoogleScanner();

        btnUpdateScanPassScanStock = findViewById(R.id.btnUpdatePassScanStock);

        btnUpdateScanPassScanStock.setOnClickListener(view ->{

            if(isScannerInstalled){

                if(isUpdateFocus){
                    gmsOption = new GmsBarcodeScannerOptions.Builder()
                            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).enableAutoZoom().build();
                }else{
                    gmsOption = new GmsBarcodeScannerOptions.Builder()
                            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build();
                }

                gmsScanner = GmsBarcodeScanning.getClient(StockTakePassUpdate.this, gmsOption);

                startInScanning();

            }else{
                Toast.makeText(StockTakePassUpdate.this, "Scanner is not install", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void startInScanning(){

        gmsScanner
                .startScan()
                .addOnSuccessListener(
                        barcode -> {
                            // Task completed successfully

                            final MediaPlayer beepSound = MediaPlayer.create(StockTakePassUpdate.this, com.google.zxing.client.android.R.raw.zxing_beep);
                            beepSound.start();
                            String stockTakeResult = getBarCodeResult
                                    (barcode.getRawValue(),barcode.getFormat());

                            Intent intent = new Intent(StockTakePassUpdate.this,LoadingPage.class);
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

    public void installGoogleScanner(){

        ModuleInstallClient client =  ModuleInstall.getClient(StockTakePassUpdate.this);

        ModuleInstallRequest request = ModuleInstallRequest.newBuilder()
                .addApi(GmsBarcodeScanning.getClient(StockTakePassUpdate.this))
                .build();

        client.installModules(request).addOnSuccessListener(response->{
            isScannerInstalled = true;
        }).addOnFailureListener(response ->{
            isScannerInstalled = false;
            Toast.makeText(StockTakePassUpdate.this, response.getMessage(), Toast.LENGTH_LONG).show();
        });
    }
}