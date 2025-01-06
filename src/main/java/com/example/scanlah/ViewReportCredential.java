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
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ViewReportCredential extends AppCompatActivity {

    Button btnReport;

    EditText inputReportUsername;

    EditText inputReportCredential;

    String str64ReportCredential = null;

    String webhookLogin = "https://plumber.gov.sg/webhooks/5aa2652c-9071-4dba-a322-06bbebcb8b8a?";

    String appScriptLogin = "https://script.google.com/macros/s/AKfycbwwFD0NAOa5IECcUOeNVca4odZrWM2QU0P7Uvi9rGCCBJm4dx60bxhUL2xLQAhpC2udqA/exec?";

    String credentiaReportlUUID = null;

    boolean isReportCredential = false;

    String COMPLETED = "COMPLETED";

    TextView errorReportLoginMsg = null;

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
        setContentView(R.layout.activity_view_report_credential);

        btnReport = findViewById(R.id.btnViewReportLogin);

        inputReportUsername = findViewById(R.id.editViewReportInputUsername);

        inputReportCredential = findViewById(R.id.editViewReportInputUserCredential);

        HTTPClient.getHttpClient().setContext(ViewReportCredential.this);

        errorReportLoginMsg = findViewById(R.id.errorViewReportCredential);

        errorReportLoginMsg.setVisibility(View.GONE);

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

        btnReport.setOnClickListener(view -> {

            btnReport.setEnabled(false);
            errorReportLoginMsg.setVisibility(View.GONE);
            callViewReportWebHook();
        });
    }

    public void callViewReportWebHook(){

        credentiaReportlUUID = UUID.randomUUID().toString();

        str64ReportCredential = encodeToBase64(inputReportCredential.getText().toString().trim());

        isReportCredential = false;

        String url = webhookLogin +
                "username=" + inputReportUsername.getText().toString().trim() + "&password=" +
                str64ReportCredential + "&uniqueId=" + credentiaReportlUUID;
        try{

            Request requestScan = new Request.Builder().url(url).build();

            new OkHttpClient().newCall(requestScan).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    //Get the status
                    response.body().close();
                    getReportCredentialStatusThread();

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

    public void getReportCredentialStatusThread(){

        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {

            @Override
            public void run() {

                if(!isReportCredential){
                    getReportCredentialStatus();
                    handler.postDelayed(this, 2 * 1000);
                }
            }
        };
        handler.postDelayed(runnable, 1 * 1000);
    }

    public void getReportCredentialStatus(){

        try{

            String url = appScriptLogin +
                    "pageAction=getUniqueStatus&uniqueId=" + credentiaReportlUUID;

            Request requestScan = new Request.Builder().url(url).build();

            new OkHttpClient().newCall(requestScan).enqueue(new Callback() {

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    if(!isReportCredential){
                        Gson gson = new Gson();
                        ResponseBody responseBody =response.body();
                        SummaryInitialModel entity = gson.fromJson(responseBody.string(), SummaryInitialModel.class);

                        if(entity != null){
                            if(entity.getStatus().equals(COMPLETED)) {
                                //setIsCompleted(true);
                                response.body().close();
                                getCredentialResult();

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

    public void getCredentialResult(){

        try{
            String url = appScriptLogin +
                    "pageAction=getInitalConditon&uniqueId=" + credentiaReportlUUID;
            Request requestScan = new Request.Builder().url(url).build();

            new OkHttpClient().newCall(requestScan).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    if(!isReportCredential){
                        //Get the status
                        Gson gson = new Gson();
                        ResponseBody responseBody =response.body();
                        LoginResultModel entity = gson.fromJson(responseBody.string(), LoginResultModel.class);

                        if(entity != null) {
                            if (entity.getIsAvailable().equals("TRUE")){
                                String statusCode = entity.getStatusCode();
                                response.body().close();

                                if(!isReportCredential){
                                    if (statusCode.equals("200")){
                                        isReportCredential = true;
                                        getCredentialReportSummary(credentiaReportlUUID);
                                    }else if (statusCode.equals("401")){
                                        isReportCredential = true;
                                        showUI();
                                        deleteStickUUID(credentiaReportlUUID);
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
                errorReportLoginMsg.setVisibility(View.VISIBLE);
                btnReport.setEnabled(true);
            }
        });
    }
    public void getCredentialReportSummary(String strUUID) {

        String url = appScriptLogin +
                "uniqueId=" + strUUID + "&pageAction=getCredentialSummary";

        Request requestScan = new Request.Builder().url(url).build();

        new OkHttpClient().newCall(requestScan).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                Gson gson = new Gson();
                ResponseBody responseBody = response.body();
                LoginSummaryModel entity = gson.fromJson(responseBody.string(), LoginSummaryModel.class);

                if (entity != null) {
                    if (entity.getIsAvailable().equals("TRUE")) {
                        response.body().close();

                        if (entity.getStatusCode().equals("200")) {
                            if(entity.getIsActive().equals("ACTIVE")){
                                //OTP

                                //String isOTPEnable = getString(R.string.ENABLE_OTP);

                                if(entity.getIsOTPEnabled().equals("TRUE")){
                                    Intent intent = new Intent(ViewReportCredential.
                                            this,ViewReportOTP.class);
                                    intent.putExtra("OTPEmail", entity.getUsername());
                                    intent.putExtra("PAGE_REDIRECT", redirectPage);
                                    if(redirectPage.equals("STOCK_MASTER")){
                                        intent.putExtra(StockSummaryPage, StockSummaryPagevValue);
                                        intent.putExtra(strAndriodPage, strAndriodPageValue);
                                        intent.putExtra(SERIALNO, SERIALNOvalue);
                                        intent.putExtra(itemDesc, itemDescValue);
                                    }
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    if(redirectPage.equals("INVENTORY_MASTER")){
                                        Intent intent = new Intent(ViewReportCredential.
                                                this,SummaryPage.class);
                                        intent.putExtra("OTPEmail", entity.getUsername());
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        /**/
                                        if(redirectPage.equals("STOCK_MASTER")){
                                            Intent urlPage =
                                                    new Intent(ViewReportCredential.this, stockSummary.class);
                                            urlPage.putExtra(StockSummaryPage, StockSummaryPagevValue);
                                            urlPage.putExtra(strAndriodPage, strAndriodPageValue);
                                            urlPage.putExtra(SERIALNO, SERIALNOvalue);
                                            urlPage.putExtra(itemDesc, itemDescValue);
                                            urlPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(urlPage);
                                            finish();
                                        }
                                    }
                                }
                            }
                        }
                        //Delete the initialUID
                        deleteStickUUID(credentiaReportlUUID);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

                e.printStackTrace();

            }
        });
    }


    public void deleteStickUUID (String strUUID){

        String url = appScriptLogin +
                "pageAction=deleteUUID&uniqueId=" + strUUID;

        //very important
        isReportCredential = true;

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

    public String encodeToBase64(String input) {
        // Convert the input string to bytes and encode to Base64
        return Base64.encodeToString(input.getBytes(), Base64.DEFAULT);
    }

    //Very useful
    @Override
    public void onBackPressed() {

        Toast.makeText(ViewReportCredential.this, redirectPage, Toast.LENGTH_LONG).show();

        if(redirectPage.equals("INVENTORY_MASTER")){
            Intent intent = new Intent(ViewReportCredential.
                    this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else if(redirectPage.equals("STOCK_MASTER")){
            Intent intent = new Intent(ViewReportCredential.
                    this,StockTakeReport.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

    }
}