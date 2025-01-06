package com.example.scanlah;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class LoginErrorPage extends AppCompatActivity {

    Button btnErrorReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_error_page);

        btnErrorReturn = findViewById(R.id.btnErrorLoginReturn);

        btnErrorReturn.setOnClickListener(view ->{

            Intent intent = new Intent(LoginErrorPage.this,LoginPage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        });
    }

    //Very useful
    @Override
    public void onBackPressed() {

    }
}