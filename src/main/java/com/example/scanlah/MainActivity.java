package com.example.scanlah;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.moduleinstall.ModuleInstall;
import com.google.android.gms.common.moduleinstall.ModuleInstallClient;
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;
import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class MainActivity extends AppCompatActivity {

    Button btnOut = null;

    Button btnIn = null;

    //TextView urlLink = null;

    Button btnWebURl = null;

    /*Google Scanner*/

    boolean isScannerInstalled = false;

    GmsBarcodeScanner gmsScanner = null;

    GmsBarcodeScannerOptions gmsOption = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //https://plumber.gov.sg/tiles/9c5aeb0f-1db2-4a3a-a615-a2ea829ebfee/54f8b9b9-0bf1-4fe1-96e9-53091aab8eb4

        /*
        urlLink = findViewById(R.id.textView15);
        urlLink.setMovementMethod(LinkMovementMethod.getInstance());

         */

        gmsOption = new GmsBarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).enableAutoZoom().build();

        gmsScanner = GmsBarcodeScanning.getClient(MainActivity.this, gmsOption);



        btnOut = findViewById(R.id.btnOut);

        btnOut.setOnClickListener(view -> {
            //zxcoding
            //scanCodeOut();
            if(isScannerInstalled){

                startOutScanning();

            }else{
                Toast.makeText(MainActivity.this, "Scanner is not install", Toast.LENGTH_LONG).show();
            }

        });

        //HTTPClient.getInstance(MainActivity.this);

        HTTPClient.getHttpClient().setContext(MainActivity.this);

        btnIn = findViewById(R.id.btnIn);

        installGoogleScanner();

        btnIn.setOnClickListener(view ->{
            //zxcoding
            //scanCodeIn();

            //scanCodeOut();
            if(isScannerInstalled){

                startInScanning();

            }else{
                Toast.makeText(MainActivity.this, "Scanner is not install", Toast.LENGTH_LONG).show();
            }

        });

        if(getIntent().hasExtra(getString(R.string.send_sucessful))){

            Toast.makeText(MainActivity.this, getString(R.string.send_sucessful), Toast.LENGTH_SHORT).show();
        }

        btnWebURl = findViewById(R.id.btnURLLink);

        String htmlText = "View inventory transactions";
        btnWebURl.setPaintFlags(btnWebURl.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        btnWebURl.setOnClickListener(view ->{

            Intent urlPage;

            if(HTTPClient.getHttpClient().getIsViewReportLogin().equals("TRUE")){
                urlPage = new Intent(MainActivity.this, ViewReportCredential.class);
                urlPage.putExtra("PAGE_REDIRECT", "INVENTORY_MASTER");
                startActivity(urlPage);
            }else{
                urlPage = new Intent(MainActivity.this, SummaryPage.class);
                urlPage.putExtra("PAGE_REDIRECT", "INVENTORY_MASTER");
                startActivity(urlPage);
            }



            //for testing
            finish();

        });
    }

    public void startInScanning(){

        gmsScanner
                .startScan()
                .addOnSuccessListener(
                        barcode -> {
                            // Task completed successfully
                            final MediaPlayer beepSound = MediaPlayer.create(MainActivity.this, com.google.zxing.client.android.R.raw.zxing_beep);
                            beepSound.start();
                            String inResult = getBarCodeResult
                                    (barcode.getRawValue(),barcode.getFormat());
                            /*
                            Toast.makeText(MainActivity.this,
                                    inResult, Toast.LENGTH_LONG).show();
                             */

                            Intent intent = new Intent(MainActivity.this,LoadingPage.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("EXTRACT_SERIAL", inResult);
                            intent.putExtra("EXTRACT_TYPE", "INITIAL");
                            intent.putExtra("EXTRACT__SUB_TYPE", "RETURN");
                            startActivity(intent);

                            //for testing
                            finish();

                        })
                .addOnCanceledListener(
                        () -> {
                            // Task canceled

                        })
                .addOnFailureListener(
                        e -> {
                            // Task failed with an exception
                        });

    }

    public void startOutScanning(){

        gmsScanner
                .startScan()
                .addOnSuccessListener(
                        barcode -> {
                            // Task completed successfully
                            final MediaPlayer beepSound = MediaPlayer.create(MainActivity.this, com.google.zxing.client.android.R.raw.zxing_beep);
                            beepSound.start();
                            String outResult = getBarCodeResult
                                    (barcode.getRawValue(),barcode.getFormat());
                            /*
                            Toast.makeText(MainActivity.this,
                                    outResult, Toast.LENGTH_LONG).show();
                             */

                            Intent intent = new Intent(MainActivity.this,LoadingPage.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("EXTRACT_SERIAL", outResult);
                            intent.putExtra("EXTRACT_TYPE", "INITIAL");
                            intent.putExtra("EXTRACT__SUB_TYPE", "BORROW");
                            startActivity(intent);

                            //for testing
                            finish();
                        })
                .addOnCanceledListener(
                        () -> {
                            // Task canceled

                        })
                .addOnFailureListener(
                        e -> {
                            // Task failed with an exception
                        });
    }

    public String getBarCodeResult(String strResult, int format){

        String getResult = null;

        switch (format){
            case Barcode.FORMAT_CODE_128:
                if(strResult.startsWith("]C1")){
                    getResult=strResult.replace("]C1","");
                }else{
                    getResult = strResult;
                }
                break;
            default:
                getResult = strResult;

                break;
        }

        return getResult;
    }

    //Goggle Scan
    public void installGoogleScanner(){

        ModuleInstallClient client =  ModuleInstall.getClient(MainActivity.this);

        ModuleInstallRequest request = ModuleInstallRequest.newBuilder()
                .addApi(GmsBarcodeScanning.getClient(MainActivity.this))
                .build();

        client.installModules(request).addOnSuccessListener(response->{
            isScannerInstalled = true;
        }).addOnFailureListener(response ->{
            isScannerInstalled = false;
            Toast.makeText(MainActivity.this, response.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    private void scanCodeIn(){

        /*
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        //lock the orientation
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barInLaucher.launch(options);
         */

        ScanOptions options = new ScanOptions();
        options.setOrientationLocked(false);
        options.setCaptureActivity(CustomScannerActivity.class);
        barcodeLauncherIn.launch(options);

    }

    /*
    ActivityResultLauncher<ScanOptions> barInLaucher = registerForActivityResult(new ScanContract(), result ->{

        if(result.getContents() != null){

            //AlertDialog Builder
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Submit Check In Request");
            builder.setMessage("Do you want to Check Out: " + result.getContents() +
                    "? Please.");
            //this is to retrieve the result
            builder.setMessage("Seria No: " + result.getContents());
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                    //URL - Show
                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse("https://google.com.sg"));
                    startActivity(intent);

                    HTTPClient.getHttpClient().checkIn(result.getContents().toString().trim());

                    Toast.makeText(MainActivity.this, "Submit", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    });
    */

    private void scanCodeOut(){

        /*
        ScanOptions options = new ScanOptions();
        //options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        //lock the orientation
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLaucher.launch(options);
        */

        ScanOptions options = new ScanOptions();
        options.setOrientationLocked(false);
        options.setCaptureActivity(CustomScannerActivity.class);
        barcodeLauncherOut.launch(options);
    }

    /*
    ActivityResultLauncher<ScanOptions> barLaucher = registerForActivityResult(new ScanContract(), result ->{

        if(result.getContents() != null) {
            Intent checkOutPage = new Intent(MainActivity.this, Checkout.class);
            checkOutPage.putExtra(getString(R.string.serialNo), result.getContents());
            startActivity(checkOutPage);
        }

        /*
        if(result.getContents() != null){

            //AlertDialog Builder
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Serial No:");
            //this is to retrieve the result
            builder.setMessage(result.getContents());
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                    /*
                    //URL - Show
                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse("https://google.com.sg"));
                    startActivity(intent);

                    Intent checkOutPage = new Intent(MainActivity.this, Checkout.class);
                    checkOutPage.putExtra(getString(R.string.serialNo), result.getContents());
                    startActivity(checkOutPage);

                }
            }).show();
        }

    });

     */

    //Testing code (this is to change the intent activity
    private final ActivityResultLauncher<ScanOptions> barcodeLauncherIn = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
                    Intent originalIntent = result.getOriginalIntent();
                    if (originalIntent == null) {
                        Log.d("MainActivity", "Cancelled scan");
                        Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_LONG).show();
                    } else if(originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                        Log.d("MainActivity", "Cancelled scan due to missing camera permission");
                        Toast.makeText(MainActivity.this, "Cancelled due to missing camera permission", Toast.LENGTH_LONG).show();
                    }
                } else {
                    //code changes here
                    /*
                    Log.d("MainActivity", "Scanned");
                    Toast.makeText(MainActivity.this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                    */
                    //HTTPClient.getHttpClient().getInitialStatus(result.getContents(), "IN");

                    Intent intent = new Intent(MainActivity.this,LoadingPage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("EXTRACT_SERIAL", result.getContents());
                    intent.putExtra("EXTRACT_TYPE", "INITIAL");
                    intent.putExtra("EXTRACT__SUB_TYPE", "RETURN");
                    startActivity(intent);

                    //for testing
                    finish();
                }
            });

    private final ActivityResultLauncher<ScanOptions> barcodeLauncherOut = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
                    Intent originalIntent = result.getOriginalIntent();
                    if (originalIntent == null) {
                        Log.d("MainActivity", "Cancelled scan");
                        Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_LONG).show();
                    } else if(originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                        Log.d("MainActivity", "Cancelled scan due to missing camera permission");
                        Toast.makeText(MainActivity.this, "Cancelled due to missing camera permission", Toast.LENGTH_LONG).show();
                    }
                } else {

                    Intent intent = new Intent(MainActivity.this,LoadingPage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("EXTRACT_SERIAL", result.getContents());
                    intent.putExtra("EXTRACT_TYPE", "INITIAL");
                    intent.putExtra("EXTRACT__SUB_TYPE", "BORROW");
                    startActivity(intent);

                    //for testing
                    finish();
                }
            });

    //Very useful
    @Override
    public void onBackPressed() {

        //Toast.makeText(Checkout.this, "Hi HI", Toast.LENGTH_SHORT).show();
        Intent urlPage = new Intent(MainActivity.this, Home.class);
        startActivity(urlPage);

        //for testing
        finish();
    }
}