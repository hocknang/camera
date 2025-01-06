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

public class OTPLogin extends AppCompatActivity {

    TextView txtDisplayEmail;

    TextView txtErrorOTP;

    String emailAddress;

    Button btnOTPLogin;

    String otplUUID = null;

    EditText txtInputOTP;

    String webhooOTP = "https://plumber.gov.sg/webhooks/ebe17209-98f5-4283-9d89-d9ea7dc4dc21?";

    String appScriptLogin = "https://script.google.com/macros/s/AKfycbwwFD0NAOa5IECcUOeNVca4odZrWM2QU0P7Uvi9rGCCBJm4dx60bxhUL2xLQAhpC2udqA/exec?";

    String str64OTP = null;

    boolean isOTP = false;

    String COMPLETED = "COMPLETED";

    Button btnResendOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otplogin);

        txtDisplayEmail = findViewById(R.id.txtDisplayOTPEmail);

        txtErrorOTP = findViewById(R.id.txtErrorOTPValid);

        txtErrorOTP.setVisibility(View.GONE);

        HTTPClient.getHttpClient().setContext(OTPLogin.this);

        btnOTPLogin = findViewById(R.id.btnOTPLogin);

        txtInputOTP = findViewById(R.id.editInputOTP);

        btnResendOTP = findViewById(R.id.BtnResendOTP);

        if(getIntent().hasExtra("OTPEmail")){

            emailAddress = getIntent().getExtras().get("OTPEmail").toString();
            txtDisplayEmail.setText("Enter OTP Send to " + emailAddress);
        }

        btnOTPLogin.setOnClickListener(view -> {

            btnOTPLogin.setEnabled(false);
            txtErrorOTP.setVisibility(View.GONE);
            callWebHook();
        });

        btnResendOTP.setOnClickListener(view ->{
            callResendOTP();
            txtErrorOTP.setVisibility(View.GONE);
            btnOTPLogin.setEnabled(true);
            txtInputOTP.setText("");
        });
    }

    public void callResendOTP(){

        //https://script.google.com/macros/s/AKfycbwwFD0NAOa5IECcUOeNVca4odZrWM2QU0P7Uvi9rGCCBJm4dx60bxhUL2xLQAhpC2udqA/exec?pageAction=generateOTP&inputUserName=fang_peiyuan@mom.gov.sg

        String url = appScriptLogin +
                "pageAction=generateOTP&inputUserName=" + emailAddress.trim() + "&isEmailNotify=YES";
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

    public void callWebHook(){
        otplUUID = UUID.randomUUID().toString();

        str64OTP = encodeToBase64(txtInputOTP.getText().toString().trim());

        isOTP = false;

        String url = webhooOTP +
                "username=" + emailAddress.trim() + "&otp=" +
                str64OTP + "&uniqueId=" + otplUUID;
        try{

            Request requestScan = new Request.Builder().url(url).build();

            new OkHttpClient().newCall(requestScan).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    //Get the status
                    response.body().close();
                    getOTPStatusThread();
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

    //continue
    public void getOTPStatusThread(){

        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {

            @Override
            public void run() {

                if(!isOTP){
                    getOTPStatus();
                    handler.postDelayed(this, 2 * 1000);
                }
            }
        };
        handler.postDelayed(runnable, 1 * 1000);

    }

    public void getOTPStatus(){

        try{

            String url = appScriptLogin +
                    "pageAction=getUniqueStatus&uniqueId=" + otplUUID;

            Request requestScan = new Request.Builder().url(url).build();

            new OkHttpClient().newCall(requestScan).enqueue(new Callback() {

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    if(!isOTP){
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
                    "pageAction=getInitalConditon&uniqueId=" + otplUUID;
            Request requestScan = new Request.Builder().url(url).build();

            new OkHttpClient().newCall(requestScan).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    if(!isOTP){
                        //Get the status
                        Gson gson = new Gson();
                        ResponseBody responseBody =response.body();
                        LoginResultModel entity = gson.fromJson(responseBody.string(), LoginResultModel.class);

                        if(entity != null) {
                            if (entity.getIsAvailable().equals("TRUE")){
                                String statusCode = entity.getStatusCode();
                                response.body().close();

                                if(!isOTP){
                                    if (statusCode.equals("200")){
                                        isOTP = true;
                                        Intent intent = new Intent(OTPLogin.this,Home.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                        deleteStickUUID(otplUUID);
                                        HTTPClient.getHttpClient().setStrSendEmailAddress(emailAddress);
                                    }else if (statusCode.equals("401")){
                                        isOTP = true;
                                        showUI();
                                        deleteStickUUID(otplUUID);
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
                txtErrorOTP.setVisibility(View.VISIBLE);
                btnOTPLogin.setEnabled(true);
            }
        });
    }

    public void deleteStickUUID (String strUUID){

        String url = appScriptLogin +
                "pageAction=deleteUUID&uniqueId=" + strUUID;

        //very important
        isOTP = true;

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
}