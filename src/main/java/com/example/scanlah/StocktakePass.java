package com.example.scanlah;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StocktakePass extends AppCompatActivity {

    TextView txtPassStockTake = null;

    TextView txtPassDescStockTake = null;

    String strSERIALNO = null;

    String strItemDesc = null;

    Button btnPassURLMaster = null;

    Switch switchBtn = null;

    boolean isFocus = false;

    TextView lblCameraPassZoomStatus = null;

    boolean isScannerInstalled = false;

    Button btnResumePassScanStock = null;

    GmsBarcodeScanner gmsScanner = null;

    GmsBarcodeScannerOptions gmsOption = null;

    String updatePlumber = "https://plumber.gov.sg/webhooks/0ce8b6e0-9b8e-4f84-860c-fbd1945c4b18?";

    Button btnUpdatePassRemark;

    EditText edPassRemark;

    String strPassRemark;

    String displayDateTime = null;

    TextView textDisplayErrorRemark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stocktake_pass);

        txtPassStockTake = findViewById(R.id.txtPassStockTake);

        txtPassDescStockTake = findViewById(R.id.txtPassDescStockTake);

        if(getIntent().hasExtra("SERIALNO")){
            strSERIALNO = getIntent().getExtras().get("SERIALNO").toString();
            txtPassStockTake.setText(strSERIALNO);
        }

        if(getIntent().hasExtra("itemDesc")){
            strItemDesc = getIntent().getExtras().get("itemDesc").toString();
            txtPassDescStockTake.setText(strItemDesc);
        }

        //
        btnPassURLMaster = findViewById(R.id.btnPassURLMaster);

        btnPassURLMaster.setPaintFlags(btnPassURLMaster.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        btnPassURLMaster.setOnClickListener(view ->{

            Intent urlPage;

            if(HTTPClient.getHttpClient().getIsViewReportLogin().equals("TRUE")){
                urlPage = new Intent(StocktakePass.this, ViewReportCredential.class);
                urlPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                urlPage.putExtra("StockSummaryPage", "MASTER");
                urlPage.putExtra("strAndriodPage", "StocktakePass");
                urlPage.putExtra("PAGE_REDIRECT", "STOCK_MASTER");
                urlPage.putExtra("SERIALNO", strSERIALNO);
                urlPage.putExtra("itemDesc", strItemDesc);
                startActivity(urlPage);
            }else{
                urlPage = new Intent(StocktakePass.this, stockSummary.class);
                urlPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                urlPage.putExtra("StockSummaryPage", "MASTER");
                urlPage.putExtra("strAndriodPage", "StocktakePass");
                urlPage.putExtra("PAGE_REDIRECT", "STOCK_MASTER");
                urlPage.putExtra("SERIALNO", strSERIALNO);
                urlPage.putExtra("itemDesc", strItemDesc);
                startActivity(urlPage);
            }

            //for testing
            finish();
        });

        switchBtn = findViewById(R.id.switchPassButton);

        lblCameraPassZoomStatus = findViewById(R.id.lblCameraPassZoomStatus);

        switchBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                lblCameraPassZoomStatus.setText("Enabled");
                isFocus = true;
            } else {
                lblCameraPassZoomStatus.setText("Disabled");
                isFocus = false;
            }
        });

        btnUpdatePassRemark = findViewById(R.id.btnPassUpdate);

        edPassRemark = findViewById(R.id.editPassRemark);

        HTTPClient.getHttpClient().setContext(StocktakePass.this);

        displayDateTime = HTTPClient.getHttpClient().getTodayDate();

        btnUpdatePassRemark.setOnClickListener(view ->{

            strPassRemark = edPassRemark.getText().toString().trim();

            callUpdate();

        });

        //Camera
        installGoogleScanner();

        btnUpdatePassRemark.setVisibility(View.GONE);

        textDisplayErrorRemark = findViewById(R.id.textView44);

        textDisplayErrorRemark.setVisibility(View.GONE);

        edPassRemark.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                validateRemark(s.toString().trim());

            }
        });

        btnResumePassScanStock = findViewById(R.id.btnResumePassScanStock);

        btnResumePassScanStock.setOnClickListener(view ->{

            if(isScannerInstalled){

                if(isFocus){
                    gmsOption = new GmsBarcodeScannerOptions.Builder()
                            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).enableAutoZoom().build();
                }else{
                    gmsOption = new GmsBarcodeScannerOptions.Builder()
                            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build();
                }

                gmsScanner = GmsBarcodeScanning.getClient(StocktakePass.this, gmsOption);

                startInScanning();

            }else{
                Toast.makeText(StocktakePass.this, "Scanner is not install", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void validateRemark(String strRemark){

        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(strRemark.toString().trim());
        boolean b = m.find();

        if (b) {
            //Toast.makeText(Checkout.this, "Error Detected", Toast.LENGTH_LONG).show();
            textDisplayErrorRemark.setText(getString(R.string.Remark_error_space));
            textDisplayErrorRemark.setVisibility(View.VISIBLE);
            btnUpdatePassRemark.setVisibility(View.GONE);
        }else{
            textDisplayErrorRemark.setVisibility(View.GONE);
            btnUpdatePassRemark.setVisibility(View.VISIBLE);
        }

        if(edPassRemark.getText().toString().trim().equals("")) {
            btnUpdatePassRemark.setVisibility(View.GONE);
        }
    }

    public void callUpdate(){

        String url = updatePlumber +
                "serialNo=" + strSERIALNO + "&remark=" + strPassRemark + "&updateTime=" + displayDateTime;
        try{

            Request requestScan = new Request.Builder().url(url).build();

            new OkHttpClient().newCall(requestScan).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    //Get the status
                    response.body().close();

                    Intent urlPage = new Intent(StocktakePass.this, StockTakePassUpdate.class);
                    urlPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    urlPage.putExtra("StockSummaryPage", "MASTER");
                    urlPage.putExtra("strAndriodPage", "StocktakePass");
                    urlPage.putExtra("PAGE_REDIRECT", "STOCK_MASTER");
                    urlPage.putExtra("SERIALNO", strSERIALNO);
                    urlPage.putExtra("itemDesc", strItemDesc);
                    startActivity(urlPage);
                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                    e.printStackTrace();

                }
            });

        }catch(Exception e){

            e.printStackTrace();
        }
    }

    public void startInScanning(){

        gmsScanner
                .startScan()
                .addOnSuccessListener(
                        barcode -> {
                            // Task completed successfully

                            final MediaPlayer beepSound = MediaPlayer.create(StocktakePass.this, com.google.zxing.client.android.R.raw.zxing_beep);
                            beepSound.start();
                            String stockTakeResult = getBarCodeResult
                                    (barcode.getRawValue(),barcode.getFormat());

                            Intent intent = new Intent(StocktakePass.this,LoadingPage.class);
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

        ModuleInstallClient client =  ModuleInstall.getClient(StocktakePass.this);

        ModuleInstallRequest request = ModuleInstallRequest.newBuilder()
                .addApi(GmsBarcodeScanning.getClient(StocktakePass.this))
                .build();

        client.installModules(request).addOnSuccessListener(response->{
            isScannerInstalled = true;
        }).addOnFailureListener(response ->{
            isScannerInstalled = false;
            Toast.makeText(StocktakePass.this, response.getMessage(), Toast.LENGTH_LONG).show();
        });
    }



    @Override
    public void onBackPressed() {

        //Toast.makeText(Checkout.this, "Hi HI", Toast.LENGTH_SHORT).show();
        Intent urlPage = new Intent(StocktakePass.this, stocktakemain.class);
        startActivity(urlPage);

        //for testing
        finish();
    }
}