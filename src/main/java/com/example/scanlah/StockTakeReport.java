package com.example.scanlah;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StockTakeReport extends AppCompatActivity {

    Button btnURLMaster = null;

    Button btnURLAudit = null;

    Button btnURLUntracked = null;

    String strStockPage = "StockSummaryPage";

    String strAndriodPage = "strAndriodPage";

    String strRedirectPage = "PAGE_REDIRECT";

    String strUIPage = "StockTakeReport";

    String senderEmail = null;

    Button btnURLSendAllReportEmail;

    String sendReportLink = "https://plumber.gov.sg/webhooks/40b8242f-16d1-4899-9f1e-79a785ec6414";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_take);

        btnURLMaster = findViewById(R.id.btnURLMaster);

        btnURLAudit = findViewById(R.id.btnURLAudit);

        btnURLUntracked = findViewById(R.id.btnURLUntracked);

        HTTPClient.getHttpClient().setContext(StockTakeReport.this);

        btnURLSendAllReportEmail = findViewById(R.id.btnURLSendAllReport);

        senderEmail = HTTPClient.getHttpClient().getStrSendEmailAddress();

        btnURLMaster.setPaintFlags(btnURLMaster.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        btnURLMaster.setOnClickListener(view ->{

            Intent urlPage;

            if(HTTPClient.getHttpClient().getIsViewReportLogin().equals("TRUE")){
                urlPage = new Intent(StockTakeReport.this, ViewReportCredential.class);
                urlPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                urlPage.putExtra(strStockPage, "MASTER");
                //Extra
                urlPage.putExtra(strRedirectPage, "STOCK_MASTER");
                urlPage.putExtra(strAndriodPage,strUIPage);
                startActivity(urlPage);
            }else{
                urlPage = new Intent(StockTakeReport.this, stockSummary.class);
                urlPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                urlPage.putExtra(strStockPage, "MASTER");
                //Extra
                urlPage.putExtra(strRedirectPage, "STOCK_MASTER");
                urlPage.putExtra(strAndriodPage,strUIPage);
                startActivity(urlPage);
            }
        });

        btnURLAudit.setPaintFlags(btnURLAudit.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        btnURLAudit.setOnClickListener(view ->{

            Intent urlPage;

            if(HTTPClient.getHttpClient().getIsViewReportLogin().equals("TRUE")){
                urlPage = new Intent(StockTakeReport.this, ViewReportCredential.class);
                urlPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                urlPage.putExtra(strStockPage, "AUDIT");
                //Extra
                urlPage.putExtra(strRedirectPage, "STOCK_MASTER");
                urlPage.putExtra(strAndriodPage,strUIPage);
                startActivity(urlPage);
            }else{
                urlPage = new Intent(StockTakeReport.this, stockSummary.class);
                urlPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                urlPage.putExtra(strStockPage, "AUDIT");
                //Extra
                urlPage.putExtra(strRedirectPage, "STOCK_MASTER");
                urlPage.putExtra(strAndriodPage,strUIPage);
                startActivity(urlPage);
            }

        });

        btnURLUntracked.setPaintFlags(btnURLUntracked.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        btnURLUntracked.setOnClickListener(view ->{

            Intent urlPage;

            if(HTTPClient.getHttpClient().getIsViewReportLogin().equals("TRUE")){
                urlPage = new Intent(StockTakeReport.this, ViewReportCredential.class);
                urlPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                urlPage.putExtra(strStockPage, "UNTRACKED");
                //Extra
                urlPage.putExtra(strRedirectPage, "STOCK_MASTER");
                urlPage.putExtra(strAndriodPage,strUIPage);
                startActivity(urlPage);
            }else{
                urlPage = new Intent(StockTakeReport.this, stockSummary.class);
                urlPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                urlPage.putExtra(strStockPage, "UNTRACKED");
                //Extra
                urlPage.putExtra(strRedirectPage, "STOCK_MASTER");
                urlPage.putExtra(strAndriodPage,strUIPage);
                startActivity(urlPage);
            }
        });

        btnURLSendAllReportEmail.setPaintFlags(btnURLUntracked.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        btnURLSendAllReportEmail.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(StockTakeReport.this);
            builder.setTitle("Alert");
            builder.setMessage("Send Report link to " + senderEmail);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Perform action on OK button click
                    callSendReportLink();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Perform action on Cancel button click
                    dialog.dismiss(); // Close the dialog
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    public void callSendReportLink(){

        String url = sendReportLink +
                "?senderEmail=" + senderEmail;
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

    //Very useful
    @Override
    public void onBackPressed() {

        //Toast.makeText(Checkout.this, "Hi HI", Toast.LENGTH_SHORT).show();
        Intent urlPage = new Intent(StockTakeReport.this, stocktakemain.class);
        startActivity(urlPage);

        //for testing
        finish();
    }
}