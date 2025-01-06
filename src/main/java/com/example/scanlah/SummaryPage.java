package com.example.scanlah;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class SummaryPage extends AppCompatActivity {

    WebView databaseView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_page);

        WebView databaseView = (WebView) findViewById(R.id.webView);
        databaseView.setWebChromeClient(new WebChromeClient());
        databaseView.clearCache(true);
        databaseView.clearHistory();
        databaseView.getSettings().setJavaScriptEnabled(true);
        databaseView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        databaseView.loadUrl("https://plumber.gov.sg/tiles/9c5aeb0f-1db2-4a3a-a615-a2ea829ebfee/54f8b9b9-0bf1-4fe1-96e9-53091aab8eb4");
    }

    @Override
    public void onBackPressed() {

        //Toast.makeText(Checkout.this, "Hi HI", Toast.LENGTH_SHORT).show();
        Intent urlPage = new Intent(SummaryPage.this, MainActivity.class);
        startActivity(urlPage);

        //for testing
        finish();
    }
}