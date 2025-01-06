package com.example.scanlah;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class stockSummary extends AppCompatActivity {

    WebView databaseView = null;

    String urlAudit = "https://plumber.gov.sg/tiles/aefa9324-ce91-44dd-a467-88325a22072a/7339b326-7bc9-40f4-a9dc-25d05638e2fd";

    String urlUntracked = "https://plumber.gov.sg/tiles/468ae979-8992-4944-8a39-4b24dc3209de/4d46ae11-1502-49ca-8e7f-cadeb3a37b80";

    String urlMaster = "https://plumber.gov.sg/tiles/873d9fb7-e5a3-493e-9c07-b1106ebf6360/43846962-bca2-4606-ae44-95988bd5d85d";

    String strAndriodPage = "strAndriodPage";

    String strUIPage = null;

    String SERIALNO = null;

    String itemDesc = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_summary);

        WebView databaseView = (WebView) findViewById(R.id.webStokSummary);
        databaseView.setWebChromeClient(new WebChromeClient());
        databaseView.clearCache(true);
        databaseView.clearHistory();
        databaseView.getSettings().setJavaScriptEnabled(true);
        databaseView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        String strPage = getIntent().getExtras().get("StockSummaryPage").toString();

        String strURL = null;

        switch(strPage){

            case "MASTER":

                strURL = urlMaster;

                break;

            case "AUDIT":

                strURL = urlAudit;

                break;

            case "UNTRACKED":

                strURL = urlUntracked;

                break;

        }

        databaseView.loadUrl(strURL);

        if(getIntent().hasExtra("strAndriodPage")){

            strUIPage = getIntent().getExtras().get("strAndriodPage").toString();
        }

        if(getIntent().hasExtra("SERIALNO")){

            SERIALNO = getIntent().getExtras().get("SERIALNO").toString();
        }

        if(getIntent().hasExtra("itemDesc")){

            itemDesc = getIntent().getExtras().get("itemDesc").toString();
        }
    }

    //Very useful
    @Override
    public void onBackPressed() {

        if(strUIPage.equals("StockTakeReport")){
            Intent urlPage = new Intent(stockSummary.this, StockTakeReport.class);
            startActivity(urlPage);

            //for testing
            finish();
        }

        if(strUIPage.equals("StockTakeError")){
            Intent urlPage = new Intent(stockSummary.this, StockTakeError.class);
            urlPage.putExtra("SERIALNO", SERIALNO);
            startActivity(urlPage);
            //for testing
            finish();
        }

        if(strUIPage.equals("StocktakePass")){
            Intent urlPage = new Intent(stockSummary.this, StocktakePass.class);
            urlPage.putExtra("SERIALNO", SERIALNO);
            urlPage.putExtra("itemDesc", itemDesc);
            startActivity(urlPage);
            //for testing
            finish();
        }

        if(strUIPage.equals("StocktakePassUpdate")){
            Intent urlPage = new Intent(stockSummary.this, StockTakePassUpdate.class);
            urlPage.putExtra("SERIALNO", SERIALNO);
            urlPage.putExtra("itemDesc", itemDesc);
            startActivity(urlPage);
            //for testing
            finish();
        }

        if(strUIPage.equals("StockTakeErrorUpdate")){
            Intent urlPage = new Intent(stockSummary.this, StockTakeErrorUpdate.class);
            urlPage.putExtra("SERIALNO", SERIALNO);
            startActivity(urlPage);
            //for testing
            finish();
        }


    }
}