package com.example.scanlah;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RecoverySystem;
import android.text.Editable;
import android.text.TextWatcher;
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

public class LoginPage extends AppCompatActivity {

    Button btnLogin;

    EditText inputUsername;

    EditText inputCredential;

    String str64Credential = null;

    String webhookLogin = "https://plumber.gov.sg/webhooks/5aa2652c-9071-4dba-a322-06bbebcb8b8a?";


    String appScriptLogin = "https://script.google.com/macros/s/AKfycbwwFD0NAOa5IECcUOeNVca4odZrWM2QU0P7Uvi9rGCCBJm4dx60bxhUL2xLQAhpC2udqA/exec?";

    String credentialUUID = null;

    boolean isCredential = false;

    String COMPLETED = "COMPLETED";

    TextView errorLoginMsg = null;

    boolean isUsername= false;

    boolean isPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        btnLogin = findViewById(R.id.btnLogin);

        inputUsername = findViewById(R.id.editInputUsername);

        inputCredential = findViewById(R.id.editInputUserCredential);

        HTTPClient.getInstance(LoginPage.this);

        errorLoginMsg = findViewById(R.id.errorLogin);

        errorLoginMsg.setVisibility(View.GONE);

        btnLogin.setVisibility(View.GONE);

        btnLogin.setOnClickListener(view -> {

            btnLogin.setEnabled(false);
            errorLoginMsg.setVisibility(View.GONE);
            callWebHook();
        });

        inputUsername.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(inputUsername.getText().length() > 0){
                    isUsername = true;
                }else{
                    isUsername = false;
                }

                validateLoginBtn();
            }

        });

        inputCredential.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(inputCredential.getText().length() > 0){
                    isPassword = true;
                }else{
                    isPassword = false;
                }

                validateLoginBtn();
            }

        });
    }

    public void validateLoginBtn(){

        if(isUsername && isPassword){
            btnLogin.setVisibility(View.VISIBLE);
        }else{
            btnLogin.setVisibility(View.GONE);
        }
    }

    public void callWebHook(){

        credentialUUID = UUID.randomUUID().toString();

        str64Credential = encodeToBase64(inputCredential.getText().toString().trim());

        isCredential = false;

        String url = webhookLogin +
                "username=" + inputUsername.getText().toString().trim() + "&password=" +
                str64Credential + "&uniqueId=" + credentialUUID;
        try{

            Request requestScan = new Request.Builder().url(url).build();

            new OkHttpClient().newCall(requestScan).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                //Get the status
                response.body().close();
                getCredentialStatusThread();

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

    public void getCredentialStatusThread(){

        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {

            @Override
            public void run() {

                if(!isCredential){
                    getCredentialStatus();
                    handler.postDelayed(this, 2 * 1000);
                }
            }
        };
        handler.postDelayed(runnable, 1 * 1000);
    }

    public void getCredentialStatus(){

        try{

            String url = appScriptLogin +
                    "pageAction=getUniqueStatus&uniqueId=" + credentialUUID;

            Request requestScan = new Request.Builder().url(url).build();

            new OkHttpClient().newCall(requestScan).enqueue(new Callback() {

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    if(!isCredential){
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
                    "pageAction=getInitalConditon&uniqueId=" + credentialUUID;
            Request requestScan = new Request.Builder().url(url).build();

            new OkHttpClient().newCall(requestScan).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    if(!isCredential){
                        //Get the status
                        Gson gson = new Gson();
                        ResponseBody responseBody =response.body();
                        LoginResultModel entity = gson.fromJson(responseBody.string(), LoginResultModel.class);

                        if(entity != null) {
                            if (entity.getIsAvailable().equals("TRUE")){
                                String statusCode = entity.getStatusCode();
                                response.body().close();

                                if(!isCredential){

                                    if(statusCode.equals("404")){
                                        isCredential = true;
                                        Intent intent = new Intent(LoginPage.this,LoginErrorPage.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                        deleteStickUUID(credentialUUID);
                                    }else if (statusCode.equals("200")){
                                        isCredential = true;
                                        getCredentialSummary(credentialUUID);
                                    }else if (statusCode.equals("401")){
                                        isCredential = true;
                                        showUI();
                                        deleteStickUUID(credentialUUID);
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
                errorLoginMsg.setVisibility(View.VISIBLE);
                btnLogin.setEnabled(true);
            }
        });
    }

    //Here
    public void getCredentialSummary(String strUUID) {
        String url = appScriptLogin + "uniqueId=" + strUUID + "&pageAction=getCredentialSummary";

        Request requestScan = new Request.Builder().url(url).build();

        new OkHttpClient().newCall(requestScan).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Gson gson = new Gson();
                LoginSummaryModel entity = null;

                try {
                    // Consume the response body once
                    String responseBodyString = response.body().string();

                    // Parse JSON into LoginSummaryModel
                    entity = gson.fromJson(responseBodyString, LoginSummaryModel.class);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    // Always close the response body
                    if (response.body() != null) {
                        response.body().close();
                    }
                }

                if (entity != null) {
                    if ("TRUE".equals(entity.getIsAvailable())) {
                        if ("200".equals(entity.getStatusCode())) {
                            if ("ACTIVE".equals(entity.getIsActive())) {
                                HTTPClient.getHttpClient().setIsViewReportLogin(entity.getIsViewReportCredential());


                                Intent intent = new Intent(LoginPage.this, OTPLogin.class);
                                intent.putExtra("OTPEmail", entity.getUsername());
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();

                                /*
                                if ("TRUE".equals(entity.getIsOTPEnabled())) {
                                    Intent intent = new Intent(LoginPage.this, OTPLogin.class);
                                    intent.putExtra("OTPEmail", entity.getUsername());
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    if (entity.getDepartment().contains(",")) {
                                        String[] aDep = entity.getDepartment().trim().split(",");
                                        if ("Corporate Services".equals(aDep[0])) {
                                            Intent intent = new Intent(LoginPage.this, Home.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }
                                */
                            } else {
                                Intent intent = new Intent(LoginPage.this, LoginErrorPage.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                        // Delete the initialUID
                        deleteStickUUID(credentialUUID);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }
        });
    }

    //Delete
    public void deleteStickUUID (String strUUID){

        String url = appScriptLogin +
                "pageAction=deleteUUID&uniqueId=" + strUUID;

        //very important
        isCredential = true;

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

    }
}