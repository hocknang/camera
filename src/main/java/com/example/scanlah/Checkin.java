package com.example.scanlah;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Checkin extends AppCompatActivity {

    TextView inDate = null;

    TextView inSerial = null;

    String displayDateTime = null;

    String eSerialNo = null;

    Button btnINSubmit = null;

    String strDateTime = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);

        inSerial = findViewById(R.id.txtInSerial);

        inDate = findViewById(R.id.txtInDate);

        btnINSubmit = findViewById(R.id.btnInSubmit);

        btnINSubmit.setVisibility(View.VISIBLE);

        //Added
        HTTPClient.getHttpClient().setContext(Checkin.this);

        if(getIntent().hasExtra(getString(R.string.serialNo))){

            eSerialNo = getIntent().getExtras().get(getString(R.string.serialNo)).toString();

            //showMessage("Serial No: " + eSerialNo);

            inSerial.setText(eSerialNo);
        }

        displayDateTime = HTTPClient.getHttpClient().getTodayDate();

        inDate.setText(displayDateTime);

        //Need to investigate
        btnINSubmit.setOnClickListener(view ->{
            try{

                checkInSubmit();

            }catch(Exception e){
                e.printStackTrace();
                Toast.makeText(Checkin.this, "Check IN - " + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onBackPressed() {

        //Toast.makeText(Checkout.this, "Hi HI", Toast.LENGTH_SHORT).show();
        Intent urlPage = new Intent(Checkin.this, MainActivity.class);
        startActivity(urlPage);

        //for testing
        finish();
    }

    public void checkInSubmit(){
        String[] itimeDate = inDate.getText().toString().trim().split("\\,");

        strDateTime = itimeDate[0] + "%2c%20" + itimeDate[1];

        Intent intent = new Intent(Checkin.this,LoadingPage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("EXTRACT_SERIAL", inSerial.getText().toString().trim());
        intent.putExtra("EXTRACT_TYPE", "CHECKIN");
        intent.putExtra("EXTRACT__SUB_TYPE", "RETURN");
        intent.putExtra("CHECKIN_DATETIME", strDateTime);
        startActivity(intent);
        //btnINSubmit.setVisibility(View.GONE);

        //for testing
        finish();
    }
}

