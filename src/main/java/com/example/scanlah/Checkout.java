package com.example.scanlah;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Checkout extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText editName = null;

    Spinner sDepart = null;

    String departName = null;

    TextView displayDate = null;

    TextView displaySerialNo = null;

    Button btnSubmit = null;

    String displayDateTime = null;

    String eSerialNo = null;

    //Come back do
    TextView txtNameError = null;

    TextView txtDepartmentError = null;

    boolean isNameError = false;

    boolean isDepartNameError = false;

    boolean isSelectedDropdown = false;

    String eItemDescription = null;

    TextView displayItemDesc = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        editName = findViewById(R.id.editOfficerName);

        sDepart = findViewById(R.id.spinDept);

        displayDate = findViewById(R.id.lblDate);

        displaySerialNo = findViewById(R.id.lblSerialNumber);

        btnSubmit = findViewById(R.id.btnCHCSubmit);

        btnSubmit.setVisibility(View.GONE);

        displayItemDesc =findViewById(R.id.textView9);

        //Added
        HTTPClient.getHttpClient().setContext(Checkout.this);

        android.widget.ArrayAdapter<CharSequence> adapter = android.widget.ArrayAdapter.createFromResource(this, R.array.department, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sDepart.setAdapter(adapter);

        sDepart.setOnItemSelectedListener(this);

        if(getIntent().hasExtra(getString(R.string.serialNo))){

            eSerialNo = getIntent().getExtras().get(getString(R.string.serialNo)).toString();

            //showMessage("Serial No: " + eSerialNo);

            displaySerialNo.setText(eSerialNo);
        }

        //Item Description

        if(getIntent().hasExtra("initialItemDescription")){
            eItemDescription = getIntent().getExtras().get("initialItemDescription").toString();
            displayItemDesc.setText(eItemDescription);
        }

        displayDateTime = HTTPClient.getHttpClient().getTodayDate();

        displayDate.setText(HTTPClient.getHttpClient().getTodayDate());

        //Add validation check (Error Message)
        btnSubmit.setOnClickListener(view -> {

            try{

                txtNameError.setVisibility(View.GONE);
                txtDepartmentError.setVisibility(View.GONE);

                if(isDepartNameError){
                    txtDepartmentError.setText(R.string.checkout_error_required_department);
                    txtDepartmentError.setVisibility(View.VISIBLE);
                }

                if(editName.getText().toString().trim().equals("")) {
                    isNameError = true;
                    txtNameError.setText(R.string.checkout_error_required_officer);
                    txtNameError.setVisibility(View.VISIBLE);
                }else{
                    validateOfficerError(editName.getText().toString().trim());
                }

                if(isNameError){
                    txtNameError.setVisibility(View.VISIBLE);
                }

                if(!isDepartNameError && !isNameError) {

                    Intent intent = new Intent(Checkout.this,LoadingPage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("EXTRACT_SERIAL", eSerialNo.toString().trim());
                    intent.putExtra("EXTRACT_TYPE", "CHECKOUT");
                    intent.putExtra("EXTRACT__SUB_TYPE", "BORROW");
                    intent.putExtra("CHECKOUT_OFFICER", editName.getText().toString().trim());
                    intent.putExtra("CHECKOUT_DEPARTMENT", departName.toString().trim());
                    intent.putExtra("CHECKOUT_DATETIME", displayDateTime.toString().trim());
                    startActivity(intent);
                    //btnSubmit.setVisibility(View.GONE);

                    //for testing
                    finish();
                }

            }catch(Exception e){

                e.printStackTrace();
                showMessage("Error (Checkout)" + e.getMessage());

            }

        });

        txtNameError = findViewById(R.id.lblNameError);

        txtNameError.setVisibility(View.GONE);

        txtDepartmentError = findViewById(R.id.lblDepartmentError);

        txtDepartmentError.setVisibility(View.GONE);

        editName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                validateOfficerError(s.toString().trim());
            }

        });
    }

    public void validateOfficerError(String officerName){
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(officerName.toString().trim());
        boolean b = m.find();

        if (b) {
            //Toast.makeText(Checkout.this, "Error Detected", Toast.LENGTH_LONG).show();
            txtNameError.setText(getString(R.string.Officer_error_space));
            txtNameError.setVisibility(View.VISIBLE);
            isNameError = true;
        }else{
            isNameError = false;
            txtNameError.setVisibility(View.GONE);
        }

        if(editName.getText().toString().trim().equals("")) {
            isNameError = true;
            txtNameError.setText(R.string.checkout_error_required_officer);
            txtNameError.setVisibility(View.VISIBLE);
        }

        toggleSubmitButton();


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        departName = adapterView.getItemAtPosition(i).toString();

        if(isSelectedDropdown){
            if(i > 0){
                departName = departName;
                //showMessage("Department Name: " + departName);
                isDepartNameError = false;
                txtDepartmentError.setVisibility(View.GONE);
            }else{
                departName = null;
                isDepartNameError = true;
                txtDepartmentError.setText(R.string.checkout_error_required_department);
                txtDepartmentError.setVisibility(View.VISIBLE);
            }

            toggleSubmitButton();
        }else{
            isSelectedDropdown = true;
            isDepartNameError = true;
            isNameError = true;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void toggleSubmitButton(){

        if(!isDepartNameError && !isNameError) {
            btnSubmit.setVisibility(View.VISIBLE);
        }else{
            btnSubmit.setVisibility(View.GONE);
        }
    }

    public void showMessage(String message){
        Toast.makeText(Checkout.this, message, Toast.LENGTH_SHORT).show();
    }

    //Very useful
    @Override
    public void onBackPressed() {

        //Toast.makeText(Checkout.this, "Hi HI", Toast.LENGTH_SHORT).show();
        Intent urlPage = new Intent(Checkout.this, MainActivity.class);
        startActivity(urlPage);

        //for testing
        finish();
    }
}