package com.example.scanlah;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CheckoutError extends AppCompatActivity {

    Button btnOUtHome = null;

    TextView txtName = null;

    TextView txtBorrowOn = null;

    TextView txtSerialNo = null;

    String errorMSG = null;

    String errorSerialNo = null;

    String errorRequestOfficer = null;

    String errorRequestTimestamp = null;

    TextView txtErrorMessage = null;

    TextView textView21 = null;

    TextView textView23 = null;

    TextView textView25 = null;

    TextView txtItemDesc = null;

    TextView textView3 = null;

    String errorItemDesc = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_error);

        btnOUtHome = findViewById(R.id.btnCheckOutErrorHome);

        txtErrorMessage = findViewById(R.id.txtDefaultCheckOutError2);

        txtName = findViewById(R.id.textView22);

        txtName.setText("");

        txtBorrowOn = findViewById(R.id.textView24);

        txtBorrowOn.setText("");

        txtSerialNo = findViewById(R.id.textView26);

        txtSerialNo.setText("");

        txtItemDesc = findViewById(R.id.textView4);

        txtItemDesc.setText("");

        textView21 = findViewById(R.id.textView21);

        textView23 = findViewById(R.id.textView23);

        textView25 = findViewById(R.id.textView25);

        textView3 = findViewById(R.id.textView3);

        //Added
        HTTPClient.getHttpClient().setContext(CheckoutError.this);

        btnOUtHome.setOnClickListener(view -> {
            Intent checkOutPage = new Intent(CheckoutError.this, MainActivity.class);
            startActivity(checkOutPage);
        });

        if(getIntent().hasExtra("OUT_ERROR")){

            try{

                Object testErrorMsg = getIntent().getExtras().get("OUT_ERROR");

                if(testErrorMsg != null){

                    errorMSG = getIntent().getExtras().get("OUT_ERROR").toString();

                    //OUT_ERROR_SERIAL_NO
                    if(errorMSG.equals("409")) {

                        errorSerialNo = getIntent().getExtras().get("OUT_ERROR_SERIAL_NO").toString();

                        errorRequestOfficer =  getIntent().getExtras().get("OUT_ERROR_REQUEST_OFFICER").toString();

                        errorRequestTimestamp = getIntent().getExtras().get("OUT_ERROR_REQUEST_TIMESTAMP").toString();

                        errorItemDesc = getIntent().getExtras().get("OUT_ERROR_ITEM_DESCRIPTION").toString();

                        txtSerialNo.setText(errorSerialNo);
                        txtName.setText(errorRequestOfficer);
                        txtBorrowOn.setText(errorRequestTimestamp);
                        txtItemDesc.setText(errorItemDesc);

                        txtSerialNo.setVisibility(View.VISIBLE);
                        txtName.setVisibility(View.VISIBLE);
                        txtBorrowOn.setVisibility(View.VISIBLE);
                        txtItemDesc.setVisibility(View.VISIBLE);

                        textView21.setVisibility(View.VISIBLE);
                        textView23.setVisibility(View.VISIBLE);
                        textView25.setVisibility(View.VISIBLE);
                        textView3.setVisibility(View.VISIBLE);

                    }else{

                        if(errorMSG.equals("404")) {
                            errorSerialNo = getIntent().getExtras().get("OUT_ERROR_SERIAL_NO").toString();

                            txtSerialNo.setText(errorSerialNo);
                            txtErrorMessage.setText("Item not found (Master List)");

                            //Original
                            /*
                            txtSerialNo.setVisibility(View.VISIBLE);
                            txtName.setVisibility(View.VISIBLE);
                            txtBorrowOn.setVisibility(View.GONE);
                            txtErrorMessage.setText("Item not found (Master List)");

                            textView21.setVisibility(View.GONE);
                            textView23.setVisibility(View.GONE);
                            textView25.setVisibility(View.VISIBLE);

                            */

                            txtSerialNo.setVisibility(View.VISIBLE);
                            txtName.setVisibility(View.VISIBLE);
                            txtBorrowOn.setVisibility(View.VISIBLE);
                            txtItemDesc.setVisibility(View.VISIBLE);

                            textView21.setVisibility(View.VISIBLE);
                            textView23.setVisibility(View.VISIBLE);
                            textView25.setVisibility(View.VISIBLE);
                            textView3.setVisibility(View.VISIBLE);
                        }


                    }
                }

            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(CheckoutError.this, "Check out Error issuse", Toast.LENGTH_SHORT).show();
            }

        }

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {

            @Override
            public void run() {

                if(getIntent().hasExtra("initialUID")){
                    String initialUUID = getIntent().getExtras().get("initialUID").toString();

                    HTTPClient.getHttpClient().deleteROw(initialUUID);
                }

            }
        };
    }

    @Override
    public void onBackPressed() {

        //Toast.makeText(Checkout.this, "Hi HI", Toast.LENGTH_SHORT).show();
        Intent urlPage = new Intent(CheckoutError.this, MainActivity.class);
        startActivity(urlPage);

        //for testing
        finish();
    }
}