package com.example.scanlah;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class LoadingPage extends AppCompatActivity {

    String serialNo = null;
    String extractType = null;

    String extractSubType = null;

    TextView load = null;

    String requestOfficer = null;

    String requestDepartment = null;

    String requestTimeDate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_page);

        //load = findViewById(R.id.textView12);

        //HTTPClient.getHttpClient().getInventoryUrl();

        HTTPClient.getHttpClient().setIsCompletedinitial(false);
        HTTPClient.getHttpClient().setIsCompletedCheckout(false);
        HTTPClient.getHttpClient().setIsCompletedCheckIn(false);
        HTTPClient.getHttpClient().setIsCompletedStockTake(false);

        //Login


        if(getIntent().hasExtra("EXTRACT_SERIAL")){

            serialNo = getIntent().getExtras().get("EXTRACT_SERIAL").toString();
        }

        if(getIntent().hasExtra("EXTRACT__SUB_TYPE")){
            extractSubType = getIntent().getExtras().get("EXTRACT__SUB_TYPE").toString();
        }


        if(getIntent().hasExtra("EXTRACT_TYPE")){
            extractType = getIntent().getExtras().get("EXTRACT_TYPE").toString();

            if(extractType.equals("INITIAL")){
                HTTPClient.getHttpClient().inInitialStatus(serialNo, extractSubType);
            }

            if(extractType.equals("CHECKOUT")){

                requestOfficer = getIntent().getExtras().get("CHECKOUT_OFFICER").toString();
                requestDepartment = getIntent().getExtras().get("CHECKOUT_DEPARTMENT").toString();
                requestTimeDate = getIntent().getExtras().get("CHECKOUT_DATETIME").toString();

                HTTPClient.getHttpClient().checkOUT(requestOfficer,
                        requestDepartment, requestTimeDate,
                        serialNo, getString(R.string.send_sucessful));
            }

            //Original Code
            if(extractType.equals("CHECKIN")){
                requestTimeDate = getIntent().getExtras().get("CHECKIN_DATETIME").toString();

                HTTPClient.getHttpClient().checkIn(serialNo,requestTimeDate,
                        getString(R.string.send_sucessful));
            }

            if(extractType.equals("STOCK")){
                HTTPClient.getHttpClient().stockTakeInitial(serialNo);
            }
        }

        animateLoading();
    }

    void animateLoading(){

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {

            int count = 0;

            @Override
            public void run() {
                /*
                count++;

                if (count == 1)
                {
                    load.setText("L");
                }
                else if (count == 2)
                {
                    load.setText("LO");
                }
                else if (count == 3)
                {
                    load.setText("LOA");
                }
                else if (count == 4)
                {
                    load.setText("LOAD");
                }
                else if (count == 5)
                {
                    load.setText("LOADI");
                }
                else if (count == 6)
                {
                    load.setText("LOADIN");
                }
                else if (count == 7)
                {
                    load.setText("LOADING");
                }
                else if (count == 8)
                {
                    load.setText("LOADING.");
                }
                else if (count == 9)
                {
                    load.setText("LOADING..");
                }
                else if (count == 10)
                {
                    load.setText("LOADING...");
                }

                if (count == 10)
                    count = 0;

                 */

                //Initial
                if(extractType.equals("INITIAL")){
                    if(!HTTPClient.getHttpClient().getIsCompletedInitial()){
                        HTTPClient.getHttpClient().getInitialStatus();
                        handler.postDelayed(this, 2 * 1000);
                    }
                }

                //Polling
                if(extractType.equals("CHECKOUT")){
                    if(!HTTPClient.getHttpClient().getIsCompletedCheckout()){
                        HTTPClient.getHttpClient().getCheckoutStatus();
                        handler.postDelayed(this, 2 * 1000);
                    }
                }

                if(extractType.equals("CHECKIN")){
                    if(!HTTPClient.getHttpClient().getIsCompletedCheckIn()){
                        HTTPClient.getHttpClient().getCheckIntStatus();
                        handler.postDelayed(this, 2 * 1000);
                    }
                }

                if(extractType.equals("STOCK")){
                    if(!HTTPClient.getHttpClient().getIsCompletedStockTake()){
                        HTTPClient.getHttpClient().getStockTakeStatus();
                        handler.postDelayed(this, 2 * 1000);
                    }
                }
            }
        };
        handler.postDelayed(runnable, 1 * 1000);
    }

    @Override
    public void onBackPressed() {

        //Toast.makeText(Checkout.this, "Hi HI", Toast.LENGTH_SHORT).show();
        Intent urlPage = new Intent(LoadingPage.this, MainActivity.class);
        startActivity(urlPage);

        //for testing
        finish();
    }
}