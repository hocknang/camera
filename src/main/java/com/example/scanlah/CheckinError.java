package com.example.scanlah;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CheckinError extends AppCompatActivity {

    TextView txtInError = null;

    String errorMSG = null;

    Button btnHomePage = null;

    TextView txtSerialNo = null;

    TextView txtItemDesc = null;

    String errorItemDesc = null;

    TextView textView12 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin_error);

        //Added
        HTTPClient.getHttpClient().setContext(CheckinError.this);

        txtItemDesc = findViewById(R.id.textView14);

        txtItemDesc.setVisibility(View.GONE);

        textView12 = findViewById(R.id.textView12);

        textView12.setVisibility(View.GONE);

        txtSerialNo = findViewById(R.id.textView16);

        try{

            txtInError = findViewById(R.id.txtDefaultCheckInErrorCommon);

            if(getIntent().hasExtra("OUT_ERROR")){

                Object testErrorMsg = getIntent().getExtras().get("OUT_ERROR");



                if(testErrorMsg !=  null) {
                    errorMSG = getIntent().getExtras().get("OUT_ERROR").toString();

                    //Common
                    if(getIntent().hasExtra("SERIALNO")){
                        String strSerial = getIntent().getExtras().get("SERIALNO").toString();

                        txtSerialNo.setText(strSerial);
                    }

                    if (errorMSG.equals("409")) {

                        errorItemDesc = getIntent().getExtras().get("ERROR_IN_ITEM_DESC").toString();

                        txtItemDesc.setText(errorItemDesc);

                        txtItemDesc.setVisibility(View.VISIBLE);

                        textView12.setVisibility(View.VISIBLE);

                        txtInError.setText("Item has already been returned");
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }


        btnHomePage = findViewById(R.id.btnCheckIn6ErrorHome);

        btnHomePage.setOnClickListener(view -> {
            Intent checkOutPage = new Intent(CheckinError.this, MainActivity.class);
            startActivity(checkOutPage);

            //for testing
            finish();
        });
    }

    @Override
    public void onBackPressed() {

        //Toast.makeText(Checkout.this, "Hi HI", Toast.LENGTH_SHORT).show();
        Intent urlPage = new Intent(CheckinError.this, MainActivity.class);
        startActivity(urlPage);

        //for testing
        finish();
    }
}