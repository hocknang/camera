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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StockTakeError extends AppCompatActivity {

    TextView lblErrorSerial = null;

    Switch switchBtn = null;

    boolean isFocus = false;

    Button btnErrorURLUntracked = null;

    Button btnStockErrorURLMaster = null;

    TextView switchStatus = null;

    String strStockPage = "StockSummaryPage";

    String strSerial = null;

    boolean isScannerInstalled = false;

    GmsBarcodeScanner gmsScanner = null;

    GmsBarcodeScannerOptions gmsOption = null;

    Button btnResumeScanStock = null;

    EditText editFailErrorLocation;

    EditText editFailErrorDescription;

    EditText editFailErrorRemark;

    Button btnFailViewUpdate;

    String updatePlumber = "https://plumber.gov.sg/webhooks/0ce8b6e0-9b8e-4f84-860c-fbd1945c4b18?";

    String strFailRemark;

    String strFailLocation;

    String strFailDescription;

    String displayDateTime = null;

    boolean isFailLocationEnter = false;

    TextView displayLocationReq;

    TextView displayDescriptionReq;

    boolean isFailDescriptionEnter = false;

    TextView displayRemarkReq;

    boolean isFailRemark = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_take_error);

        lblErrorSerial = findViewById(R.id.lblStockErrorNo);

        if(getIntent().hasExtra("SERIALNO")){
            strSerial = getIntent().getExtras().get("SERIALNO").toString();

            lblErrorSerial.setText(strSerial);
        }


        btnErrorURLUntracked = findViewById(R.id.btnStockErrorURLUntracked);

        btnErrorURLUntracked.setPaintFlags(btnErrorURLUntracked.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        btnErrorURLUntracked.setOnClickListener(view ->{

            Intent urlPage;

            if(HTTPClient.getHttpClient().getIsViewReportLogin().equals("TRUE")){
                urlPage = new Intent(StockTakeError.this, ViewReportCredential.class);
                urlPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                urlPage.putExtra("StockSummaryPage", "UNTRACKED");
                urlPage.putExtra("strAndriodPage", "StockTakeError");
                urlPage.putExtra("PAGE_REDIRECT", "STOCK_MASTER");
                urlPage.putExtra("SERIALNO", strSerial);
                startActivity(urlPage);
            }else{
                urlPage = new Intent(StockTakeError.this, stockSummary.class);
                urlPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                urlPage.putExtra("StockSummaryPage", "UNTRACKED");
                urlPage.putExtra("strAndriodPage", "StockTakeError");
                urlPage.putExtra("PAGE_REDIRECT", "STOCK_MASTER");
                urlPage.putExtra("SERIALNO", strSerial);
                startActivity(urlPage);
            }

        });

        btnStockErrorURLMaster = findViewById(R.id.btnStockErrorURLMaster);

        btnStockErrorURLMaster.setPaintFlags(btnStockErrorURLMaster.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        btnStockErrorURLMaster.setOnClickListener(view ->{

            Intent urlPage;

            if(HTTPClient.getHttpClient().getIsViewReportLogin().equals("TRUE")){
                urlPage = new Intent(StockTakeError.this, ViewReportCredential.class);
                urlPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                urlPage.putExtra("StockSummaryPage", "MASTER");
                urlPage.putExtra("strAndriodPage", "StockTakeError");
                urlPage.putExtra("PAGE_REDIRECT", "STOCK_MASTER");
                urlPage.putExtra("SERIALNO", strSerial);
                startActivity(urlPage);
            }else{
                urlPage = new Intent(StockTakeError.this, stockSummary.class);
                urlPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                urlPage.putExtra("StockSummaryPage", "MASTER");
                urlPage.putExtra("strAndriodPage", "StockTakeError");
                urlPage.putExtra("PAGE_REDIRECT", "STOCK_MASTER");
                urlPage.putExtra("SERIALNO", strSerial);
                startActivity(urlPage);
            }
        });

        editFailErrorLocation = findViewById(R.id.editFailLocation);

        editFailErrorDescription = findViewById(R.id.editFailDescription);

        editFailErrorRemark = findViewById(R.id.editFailRemark);

        btnFailViewUpdate = findViewById(R.id.btnFailUpdate);

        btnFailViewUpdate.setVisibility(View.GONE);

        HTTPClient.getHttpClient().setContext(StockTakeError.this);

        displayDateTime = HTTPClient.getHttpClient().getTodayDate();



        //Location
        editFailErrorLocation.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(s.toString().trim());
                boolean b = m.find();

                if (b) {
                    //Toast.makeText(Checkout.this, "Error Detected", Toast.LENGTH_LONG).show();
                    displayLocationReq.setText(getString(R.string.Location_error_space));
                    displayLocationReq.setVisibility(View.VISIBLE);
                    isFailLocationEnter = false;
                }else{
                    isFailLocationEnter = true;
                    displayLocationReq.setVisibility(View.GONE);
                }

                if(editFailErrorLocation.getText().toString().trim().equals("")) {
                    isFailLocationEnter = false;
                    displayLocationReq.setText(R.string.stocktakeerror_location_required);
                    displayLocationReq.setVisibility(View.VISIBLE);
                }
            }

        });

        //Description
        editFailErrorDescription.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(s.toString().trim());
                boolean b = m.find();

                if (b) {
                    //Toast.makeText(Checkout.this, "Error Detected", Toast.LENGTH_LONG).show();
                    displayDescriptionReq.setText(getString(R.string.Description_error_space));
                    displayDescriptionReq.setVisibility(View.VISIBLE);
                    isFailDescriptionEnter = false;
                }else{
                    isFailDescriptionEnter = true;
                    displayDescriptionReq.setVisibility(View.GONE);
                }

                if(editFailErrorDescription.getText().toString().trim().equals("")) {
                    isFailDescriptionEnter = false;
                    displayDescriptionReq.setText(R.string.stocktakeerror_description_required);
                    displayDescriptionReq.setVisibility(View.VISIBLE);
                }

                validaBtn();
            }

        });

        //Remark
        editFailErrorRemark.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(s.toString().trim());
                boolean b = m.find();

                if (b) {
                    //Toast.makeText(Checkout.this, "Error Detected", Toast.LENGTH_LONG).show();
                    displayRemarkReq.setText(R.string.Remark_error_space);
                    displayRemarkReq.setVisibility(View.VISIBLE);
                    isFailRemark = false;
                }else{
                    isFailRemark = true;
                    displayRemarkReq.setVisibility(View.GONE);
                }

                if(editFailErrorRemark.getText().toString().trim().equals("")) {
                    isFailRemark = false;
                    displayRemarkReq.setText(R.string.stocktakeerror_remark_required);
                    displayRemarkReq.setVisibility(View.VISIBLE);
                }

                //isFailLocation
                /*
                if(editFailErrorRemark.getText().length() > 0){
                    isFailRemark = true;
                    displayRemarkReq.setVisibility(View.GONE);
                }else{
                    isFailRemark = false;
                    displayRemarkReq.setVisibility(View.VISIBLE);
                }
                */
                validaBtn();
            }

        });

        btnFailViewUpdate.setOnClickListener(view->{
            callUpdate();
        });

        /*Label requirement*/
        displayLocationReq = findViewById(R.id.displayLocationRequired);

        displayDescriptionReq = findViewById(R.id.displayDescriptionRequired);

        displayRemarkReq = findViewById(R.id.displayRemarkRequired);
    }

    public void validaBtn(){

        if(isFailDescriptionEnter && isFailRemark && isFailLocationEnter){
            btnFailViewUpdate.setVisibility(View.VISIBLE);
        }else{
            btnFailViewUpdate.setVisibility(View.GONE);
        }
    }

    public void callUpdate() {

        strFailRemark = editFailErrorRemark.getText().toString().trim();

        strFailLocation = editFailErrorLocation.getText().toString().trim();

        strFailDescription = editFailErrorDescription.getText().toString().trim();

        String url = updatePlumber +
                "serialNo=" + strSerial + "&remark=" + strFailRemark + "&updateTime=" + displayDateTime +
                "&location=" + strFailLocation + "&description=" + strFailDescription;
        try{

            Request requestScan = new Request.Builder().url(url).build();

            new OkHttpClient().newCall(requestScan).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    //Get the status
                    response.body().close();

                    Intent urlPage = new Intent(StockTakeError.this, StockTakeErrorUpdate.class);
                    urlPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    urlPage.putExtra("StockSummaryPage", "UNTRACKED");
                    urlPage.putExtra("strAndriodPage", "StockTakeError");
                    urlPage.putExtra("PAGE_REDIRECT", "STOCK_MASTER");
                    urlPage.putExtra("SERIALNO", strSerial);
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

    @Override
    public void onBackPressed() {

        //Toast.makeText(Checkout.this, "Hi HI", Toast.LENGTH_SHORT).show();
        Intent urlPage = new Intent(StockTakeError.this, stocktakemain.class);
        startActivity(urlPage);

        //for testing
        finish();
    }

}