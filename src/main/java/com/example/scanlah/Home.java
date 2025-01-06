package com.example.scanlah;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class Home extends AppCompatActivity {

    Button btnInventory = null;
    Button btnStock = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnInventory = findViewById(R.id.btnIInventory);

        btnStock = findViewById(R.id.btnStock);

        btnInventory.setOnClickListener(view -> {
            Intent intent = new Intent(Home.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        btnStock.setOnClickListener(view -> {
            Intent intent = new Intent(Home.this,stocktakemain.class);
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