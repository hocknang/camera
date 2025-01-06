package com.example.scanlah;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ViewReportOTP extends AppCompatActivity {

    TextView txtDisplayRepEmail;

    TextView txtErrorRepOTP;

    String otpEmailAddress;

    Button btnOTPReport;

    String otpReportUUID = null;

    EditText txtInputRepOTP;

    String webhooOTP = "https://plumber.gov.sg/webhooks/ebe17209-98f5-4283-9d89-d9ea7dc4dc21?";

    String appScriptLogin = "https://script.google.com/macros/s/AKfycbwwFD0NAOa5IECcUOeNVca4odZrWM2QU0P7Uvi9rGCCBJm4dx60bxhUL2xLQAhpC2udqA/exec?";

    String str64RepOTP = null;

    boolean isRepOTP = false;

    String COMPLETED = "COMPLETED";

    Button btnResendRepOTP;

    String redirectPage = null;

    /*Stock Take*/
    String StockSummaryPage = "StockSummaryPage";
    String strAndriodPage = "strAndriodPage";
    String SERIALNO = null;
    String itemDesc = null;

    /*Stock Take Value*/
    String StockSummaryPagevValue = null;
    String strAndriodPageValue = null;
    String SERIALNOvalue = "";
    String itemDescValue = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report_otp);

        txtDisplayRepEmail = findViewById(R.id.txtDisplayReportOTPEmail);

        txtErrorRepOTP = findViewById(R.id.txtReportErrorOTPValid);

        txtErrorRepOTP.setVisibility(View.GONE);

        HTTPClient.getHttpClient().setContext(ViewReportOTP.this);

        btnOTPReport = findViewById(R.id.btnReportOTPLogin);

        txtInputRepOTP = findViewById(R.id.editReportInputOTP);

        btnResendRepOTP = findViewById(R.id.BtnReportResendOTP);

        if(getIntent().hasExtra("OTPEmail")){

            otpEmailAddress = getIntent().getExtras().get("OTPEmail").toString();
            txtDisplayRepEmail.setText("Enter OTP Send to " + otpEmailAddress);
        }

        if(getIntent().hasExtra("PAGE_REDIRECT")){
            redirectPage = getIntent().getExtras().get("PAGE_REDIRECT").toString();
        }

        /*StockTake*/
        if(getIntent().hasExtra(StockSummaryPage)){
            StockSummaryPagevValue = getIntent().getExtras().get(StockSummaryPage).toString();
        }

        if(getIntent().hasExtra(strAndriodPage)){
            strAndriodPageValue = getIntent().getExtras().get(strAndriodPage).toString();
        }

        if(getIntent().hasExtra(SERIALNO)){
            SERIALNOvalue = getIntent().getExtras().get(SERIALNO).toString();
        }

        if(getIntent().hasExtra(itemDesc)){
            itemDescValue = getIntent().getExtras().get(itemDesc).toString();
        }

        btnOTPReport.setOnClickListener(view -> {

            btnOTPReport.setEnabled(false);
            txtErrorRepOTP.setVisibility(View.GONE);
            callRepWebHook();
        });

        btnResendRepOTP.setOnClickListener(view ->{
            callResendOTP();
            btnOTPReport.setEnabled(true);
            txtErrorRepOTP.setVisibility(View.GONE);
            txtInputRepOTP.setText("");
        });

    }

    public void callResendOTP(){

        //https://script.google.com/macros/s/AKfycbwwFD0NAOa5IECcUOeNVca4odZrWM2QU0P7Uvi9rGCCBJm4dx60bxhUL2xLQAhpC2udqA/exec?pageAction=generateOTP&inputUserName=fang_peiyuan@mom.gov.sg

        String url = appScriptLogin +
                "pageAction=generateOTP&inputUserName=" + otpEmailAddress.trim() + "&isEmailNotify=YES";
        try{

            Request requestScan = new Request.Builder().url(url).build();

            new OkHttpClient().newCall(requestScan).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    //Get the status
                    response.body().close();
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

    public void callRepWebHook(){
        otpReportUUID = UUID.randomUUID().toString();

        str64RepOTP = encodeRepToBase64(txtInputRepOTP.getText().toString().trim());

        isRepOTP = false;

        String url = webhooOTP +
                "username=" + otpEmailAddress.trim() + "&otp=" +
                str64RepOTP + "&uniqueId=" + otpReportUUID;
        try{

            Request requestScan = new Request.Builder().url(url).build();

            new OkHttpClient().newCall(requestScan).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    //Get the status
                    response.body().close();
                    getOTPRepStatusThread();
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

    public void getOTPRepStatusThread(){

        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {

            @Override
            public void run() {

                if(!isRepOTP){
                    getRepOTPStatus();
                    handler.postDelayed(this, 2 * 1000);
                }
            }
        };
        handler.postDelayed(runnable, 1 * 1000);

    }

    public void getRepOTPStatus(){

        try{

            String url = appScriptLogin +
                    "pageAction=getUniqueStatus&uniqueId=" + otpReportUUID;

            Request requestScan = new Request.Builder().url(url).build();

            new OkHttpClient().newCall(requestScan).enqueue(new Callback() {

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    if(!isRepOTP){
                        Gson gson = new Gson();
                        ResponseBody responseBody =response.body();
                        SummaryInitialModel entity = gson.fromJson(responseBody.string(), SummaryInitialModel.class);

                        if(entity != null){
                            if(entity.getStatus().equals(COMPLETED)) {
                                //setIsCompleted(true);
                                response.body().close();
                                getOTPResult();

                            }
                        }
                    }
                }
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void getOTPResult(){

        try{
            String url = appScriptLogin +
                    "pageAction=getInitalConditon&uniqueId=" + otpReportUUID;
            Request requestScan = new Request.Builder().url(url).build();

            new OkHttpClient().newCall(requestScan).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    if(!isRepOTP){
                        //Get the status
                        Gson gson = new Gson();
                        ResponseBody responseBody =response.body();
                        LoginResultModel entity = gson.fromJson(responseBody.string(), LoginResultModel.class);

                        if(entity != null) {
                            if (entity.getIsAvailable().equals("TRUE")){
                                String statusCode = entity.getStatusCode();
                                response.body().close();

                                if(!isRepOTP){
                                    if (statusCode.equals("200")){
                                        isRepOTP = true;
                                        if(redirectPage.equals("INVENTORY_MASTER")){
                                            Intent intent = new Intent(ViewReportOTP.
                                                    this,SummaryPage.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }else {
                                            if(redirectPage.equals("STOCK_MASTER")){
                                                Intent urlPage =
                                                        new Intent(ViewReportOTP.this, stockSummary.class);
                                                urlPage.putExtra(StockSummaryPage, StockSummaryPagevValue);
                                                urlPage.putExtra(strAndriodPage, strAndriodPageValue);
                                                urlPage.putExtra(SERIALNO, SERIALNOvalue);
                                                urlPage.putExtra(itemDesc, itemDescValue);
                                                urlPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(urlPage);
                                                finish();
                                                deleteStickUUID(otpReportUUID);
                                            }
                                        }
                                        deleteStickUUID(otpReportUUID);
                                    }else if (statusCode.equals("401")){
                                        isRepOTP = true;
                                        showUI();
                                        deleteStickUUID(otpReportUUID);
                                    }
                                }
                            }
                        }
                    }
                }
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                    e.printStackTrace();

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void showUI(){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                // Update your UI here
                txtErrorRepOTP.setVisibility(View.VISIBLE);
                btnOTPReport.setEnabled(true);
            }
        });
    }

    public void deleteStickUUID (String strUUID){

        String url = appScriptLogin +
                "pageAction=deleteUUID&uniqueId=" + strUUID;

        //very important
        isRepOTP = true;

        try{

            Request requestScan = new Request.Builder().url(url).build();

            new OkHttpClient().newCall(requestScan).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    //Get the status
                    response.body().close();

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


    public String encodeRepToBase64(String input) {
        // Convert the input string to bytes and encode to Base64
        return Base64.encodeToString(input.getBytes(), Base64.DEFAULT);
    }

    @Override
    public void onBackPressed() {

        if(redirectPage.equals("INVENTORY_MASTER")){
            Intent intent = new Intent(ViewReportOTP.
                    this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

    }

}