package com.example.scanlah;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import kotlin.text.Charsets;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HTTPClient {

    Context context;

    private static HTTPClient httpClient = null;

    String COMPLETED = "COMPLETED";

    /*initial api*/
    String initialUID;

    String initialPageName = null;

    String initialSerialNo = null;

    //Later to delete
    String strDateTime = null;

    String strOfficerName = null;

    String strDisplayOfficerName = null;

    String eOutSerialNo = null;

    String eInSerialNo = null;

    String eOutuuID = null;
    String iRETURNuuID = null;

    boolean isCompletedInitial = false;

    Boolean isCompletedCheckout = false;

    Boolean isCompletedCheckin = false;

    //need to change
    String appScriptUrl = "https://script.google.com/macros/s/AKfycbyYfaDbTLBvMcC3F-769OwUSMqfi_XmhSL7e9qEGnL0mKkMTLkcmTPwjKAS3RzCYvsLvg/exec?";

    //Testing
    boolean isInitial = false;

    boolean isCheckout = false;

    boolean isCheckIn = false;

    Timer timer;

    /*StockTake Module*/
    String stockTakeUUID = null;

    String stockAppScriptUrl = "https://script.google.com/macros/s/AKfycbx9Ta2nBb83CBNXPdXMfZbz8FYLxEFsB1cSWxQ2wCnbX7SnESPz34KThe0bSgKHBZJtYw/exec?";

    boolean isStockTakeCompleted = false;

    boolean isStocktake = false;

    String stockTakeSerial = null;

    /*Login Status*/
    String loginUUID = null;

    String strIsViewCredentialReport = null;

    String strSendEmailAddress = null;

    public static synchronized HTTPClient getInstance(Context context) {

        if(httpClient == null) {
            httpClient = new HTTPClient(context);
        }

        return httpClient;
    }

    public static HTTPClient getHttpClient(){
        return httpClient;
    }

    public HTTPClient(Context context){
        this.context = context;
    }

    public Context setContext(Context context){
        return this.context = context;
    }

    //Step 1:
    public void inInitialStatus(String serialNo, String status){

        initialUID = UUID.randomUUID().toString();

        String strSerialNo = null;

        try {
            strSerialNo = URLEncoder.encode(serialNo, Charsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        String url = "https://plumber.gov.sg/webhooks/78fc2bc6-4944-404b-a329-d510cf28bb86?" +
                "serialNo=" + strSerialNo.trim() + "&uniqueId=" + initialUID + "&status=" + status;

        initialPageName = status;

        initialSerialNo = serialNo;

        isInitial = false;

        //very important
        setIsCompletedinitial(false);

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

    //Step 2:
    public void getInitialStatus(){
        try{

            String url = appScriptUrl +
                    "pageAction=getUniqueStatus&uniqueId=" + initialUID;

            Request requestScan = new Request.Builder().url(url).build();

            new OkHttpClient().newCall(requestScan).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    if(!getIsCompletedInitial()){
                        //Get the status
                        Gson gson = new Gson();
                        ResponseBody responseBody =response.body();
                        SummaryInitialModel entity = gson.fromJson(responseBody.string(), SummaryInitialModel.class);

                        if(entity != null){
                            if(entity.getStatus().equals(COMPLETED)) {
                                //setIsCompleted(true);
                                response.body().close();
                                getInitialStatusResult();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                    e.printStackTrace();

                }
            });

        }catch (Exception e){

        }
    }

    //Step 3: (Determine to Go Which Page)
    public void getInitialStatusResult(){

        try{

            String url = appScriptUrl +
                    "pageAction=getInitalConditon&uniqueId=" + initialUID;

            Request requestScan = new Request.Builder().url(url).build();

            new OkHttpClient().newCall(requestScan).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    if(!getIsCompletedInitial()){
                        //Get the status
                        Gson gson = new Gson();
                        ResponseBody responseBody =response.body();
                        SummaryInitialResultModel entity = gson.fromJson(responseBody.string(), SummaryInitialResultModel.class);

                        if(entity != null) {
                            if (entity.getIsAvailable().equals("TRUE")){
                                String statusCode = entity.getStatusCode();
                                response.body().close();

                                if(!isInitial){
                                    if(statusCode.equals("200")){
                                        //Original
                                        setIsCompletedinitial(true);

                                        if(initialPageName.equals("BORROW")){
                                            setIsCompletedinitial(true);
                                            getSummaryResult(initialUID);
                                        }else{
                                            //May want to consider skipping the Checkin-> Original
                                            /*
                                            Intent intent = new Intent(context,Checkin.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.putExtra("serialNo", initialSerialNo);
                                            intent.putExtra("initialUID", initialUID);
                                            context.startActivity(intent);
                                            */

                                            //Immediate Go to the process (modify):
                                            String strTodayDateTime = getTodayDate();
                                            String[] itimeDate = strTodayDateTime.trim().split("\\,");
                                            String strDateTime1 = itimeDate[0] + "%2c%20" + itimeDate[1];
                                            autoCallCheckin(initialSerialNo, strDateTime1);
                                            deleteUUID(initialUID);
                                        }

                                    }

                                    if(statusCode.equals("404")){
                                        setIsCompletedinitial(true);
                                        if(initialPageName.equals("RETURN")){
                                            Intent intent = new Intent(context,CheckinError.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.putExtra("OUT_ERROR", statusCode);
                                            intent.putExtra("SERIALNO", initialSerialNo);
                                            context.startActivity(intent);
                                        }else{
                                            //Handle Error (BORROW)
                                            Intent intent = new Intent(context,CheckoutError.class);
                                            intent.putExtra("OUT_ERROR", statusCode);
                                            intent.putExtra("OUT_ERROR_SERIAL_NO", initialSerialNo);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            context.startActivity(intent);
                                        }
                                    }

                                    if(statusCode.equals("409")){
                                        setIsCompletedinitial(true);
                                        getSummaryResult(initialUID);
                                    }

                                    if(statusCode.equals("404") ){
                                        deleteUUID(initialUID);
                                    }

                                    isInitial = true;
                                }
                            }
                        }
                    }
                }
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                    e.printStackTrace();

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void autoCallCheckin(String strInSerial, String strInDateTime){

        setIsCompletedCheckIn(false);

        //Call CheckIn API
        checkIn(strInSerial,strInDateTime,
                "Submitted");

        Handler mainHandler = new Handler(Looper.getMainLooper());

        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                if(!getIsCompletedCheckIn()){
                    getCheckIntStatus();
                    mainHandler.postDelayed(this, 2 * 1000);
                }
            }
        };
        mainHandler.postDelayed(runnable, 1 * 1000);
    }



    public void getSummaryResult(String strUUID){

        try{

            String url = appScriptUrl +
                    "uniqueId=" + strUUID + "&pageAction=getInventorySummary";

            Request requestScan = new Request.Builder().url(url).build();

            new OkHttpClient().newCall(requestScan).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                        Gson gson = new Gson();
                        ResponseBody responseBody =response.body();
                        SummaryModel entity = gson.fromJson(responseBody.string(), SummaryModel.class);

                        if(entity != null) {
                            if (entity.getIsAvailable().equals("TRUE")) {
                                response.body().close();

                                if(entity.getStatusCode().equals("409")){
                                    if(initialPageName.equals("BORROW")){
                                        Intent intent = new Intent(context,CheckoutError.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("OUT_ERROR", entity.getStatusCode());
                                        intent.putExtra("OUT_ERROR_SERIAL_NO", entity.getSerialNo());
                                        intent.putExtra("OUT_ERROR_REQUEST_TIMESTAMP",
                                                entity.getTimestamp());
                                        intent.putExtra("OUT_ERROR_REQUEST_OFFICER",
                                                entity.getRequestOfficer() + ", "
                                                        + entity.getDepartment());
                                        intent.putExtra("OUT_ERROR_ITEM_DESCRIPTION",
                                                entity.getItemDescription());
                                        context.startActivity(intent);
                                        intent.putExtra("initialUID", initialUID);
                                    }else{
                                        Intent intent = new Intent(context,CheckinError.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("OUT_ERROR", entity.getStatusCode());
                                        intent.putExtra("SERIALNO", initialSerialNo);
                                        intent.putExtra("ERROR_IN_ITEM_DESC", entity.getItemDescription());
                                        context.startActivity(intent);
                                    }

                                }else{
                                    if(initialPageName.equals("BORROW")){

                                        Intent intent = new Intent(context,Checkout.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("serialNo", initialSerialNo);
                                        //initialUID
                                        intent.putExtra("initialUID", initialSerialNo);
                                        intent.putExtra("initialItemDescription", entity.getItemDescription());
                                        context.startActivity(intent);

                                    }
                                }



                                //Delete the initialUID
                                deleteUUID(initialUID);
                            }
                        }
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

    public String getTodayDate(){

        java.util.Date today = new java.util.Date();

        //Oringial
        //String pattern = "dd MMM yyyy , hh:mmaa";
        String pattern = "dd MMM yyyy";

        java.text.DateFormat df = new java.text.SimpleDateFormat(pattern, Locale.ENGLISH);

        String strToday = df.format(today);
        /*
        java.text.DateFormat df1 = new java.text.SimpleDateFormat(pattern);

        String strToday1 = df1.format(today);
         */
        String pattern1 = "hh:mmaa";

        java.text.DateFormat dt = new java.text.SimpleDateFormat(pattern1);

        String strTime = dt.format(today);

        String finalTIme = null;

        String display = null;

        if(strTime.substring(0,1).equals("0")){
            finalTIme = strTime.substring(1,strTime.length());
        }else{
            finalTIme = strTime;
        }

        display = strToday + ", " + finalTIme;

        return display;
    }

    //Delete the UUID and Properties
    public void deleteROw(String uuid){

        String url = appScriptUrl +
                  "&uniqueId=" + uuid;
        try{

            Request requestScan = new Request.Builder().url(url).build();

            new OkHttpClient().newCall(requestScan).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    //Get the status

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

    //Call checkout
    public void checkOUT(String officeName, String department, String timeDate, String serialNo,
                         String checkoutStatus){
        try{

            isCheckout = false;

            String[] etimeDate = timeDate.toString().split("\\,");

            strDateTime = etimeDate[0] + "%2c%20" + etimeDate[1];

            //Allow special character
            strOfficerName = officeName.toString().replace(" ", "%20");

            //all to be lowercase (validation)
            strDisplayOfficerName = officeName.toString().replace(" ", "%20");

            eOutSerialNo = serialNo;

            //https://plumber.gov.sg/webhooks/70c63b83-58d2-4602-b5e6-726b600efba0?
            // serialNo=123456789101&requestOfficer=PEIYUAN&timestamp=11%20August%202024%2c%2011:52%20AM&department=ODCS&status=BORROW

            eOutuuID = UUID.randomUUID().toString();

            //String url = strURL + "redirectPage=page&pageDesc=inventory&pageAction=view&serialNo=" + serialNo;
            String url = "https://plumber.gov.sg/webhooks/70c63b83-58d2-4602-b5e6-726b600efba0?" +
                    "serialNo=" + serialNo + "&requestOfficer=" + strOfficerName +
                    "&status=BORROW&timestamp=" + strDateTime +
                    "&department=" + department + "&uniqueId=" + eOutuuID;

            Request requestScan = new Request.Builder().url(url).build();

            new OkHttpClient().newCall(requestScan).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
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

    public void getCheckoutStatus(){
        try{

            String url = appScriptUrl +
                    "pageAction=getUniqueStatus&uniqueId=" + eOutuuID;

            Request requestScan = new Request.Builder().url(url).build();

            new OkHttpClient().newCall(requestScan).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    if(!getIsCompletedCheckout()){
                        //Get the status
                        Gson gson = new Gson();
                        ResponseBody responseBody =response.body();
                        SummaryInitialModel entity = gson.fromJson(responseBody.string(), SummaryInitialModel.class);

                        if(entity != null){
                            if(entity.getStatus().equals(COMPLETED)) {
                                //setIsCompleted(true);
                                response.body().close();
                                getCheckOutStatusResult();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                    e.printStackTrace();

                }
            });

        }catch (Exception e){

        }
    }

    public void getCheckOutStatusResult(){

        try{

            String url = appScriptUrl +
                    "pageAction=getInitalConditon&uniqueId=" + eOutuuID;

            Request requestScan = new Request.Builder().url(url).build();

            new OkHttpClient().newCall(requestScan).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    if(!getIsCompletedCheckout()){
                        //Get the status
                        Gson gson = new Gson();
                        ResponseBody responseBody =response.body();
                        SummaryInitialResultModel entity = gson.fromJson(responseBody.string(), SummaryInitialResultModel.class);

                        if(entity != null) {
                            if (entity.getIsAvailable().equals("TRUE")){

                                if(!isCheckout){
                                    String statusCode = entity.getStatusCode();
                                    response.body().close();

                                    if(statusCode.equals("200")){
                                        setIsCompletedCheckout(true);
                                        Intent intent = new Intent(context,MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("Submitted", "Submitted");
                                        context.startActivity(intent);
                                    }

                                    if(statusCode.equals("404")){
                                        setIsCompletedCheckout(true);
                                        Intent intent = new Intent(context,CheckoutError.class);
                                        intent.putExtra("OUT_ERROR", statusCode);
                                        intent.putExtra("OUT_ERROR_SERIAL_NO", eOutuuID);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(intent);
                                    }

                                    if(statusCode.equals("409")){
                                        setIsCompletedCheckout(true);
                                        getSummaryResultCheck(eOutuuID, "BORROW");
                                    }

                                    if(statusCode.equals("200") || statusCode.equals("404") ){
                                        deleteUUID(eOutuuID);
                                    }

                                    isCheckout = true;
                                }
                            }
                        }
                    }
                }
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                    e.printStackTrace();

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void checkIn(String serialNo, String dateTime, String checkoutStatus){
        try{

            //String url = strURL + "redirectPage=page&pageDesc=inventory&pageAction=view&serialNo=" + serialNo;

            isCheckIn = false;

            eInSerialNo = serialNo;

            iRETURNuuID = UUID.randomUUID().toString();

            String url = "https://plumber.gov.sg/webhooks/a7ee48f8-681a-41a7-842c-2511ae98c48c?" +
                    "serialNo=" + serialNo +
                    "&status=RETURN&timestamp=" + dateTime + "&uniqueId=" + iRETURNuuID;

            Request requestScan = new Request.Builder().url(url).build();

            new OkHttpClient().newCall(requestScan).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
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

    public void getCheckIntStatus(){
        try{

            String url = appScriptUrl +
                    "pageAction=getUniqueStatus&uniqueId=" + iRETURNuuID;

            Request requestScan = new Request.Builder().url(url).build();

            new OkHttpClient().newCall(requestScan).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    if(!getIsCompletedCheckIn()){
                        //Get the status
                        Gson gson = new Gson();
                        ResponseBody responseBody =response.body();
                        SummaryInitialModel entity = gson.fromJson(responseBody.string(), SummaryInitialModel.class);

                        if(entity != null){
                            if(entity.getStatus().equals(COMPLETED)) {
                                //setIsCompleted(true);
                                response.body().close();
                                getCheckInStatusResult();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                    e.printStackTrace();

                }
            });

        }catch (Exception e){

        }
    }

    public void getCheckInStatusResult(){

        try{

            String url = appScriptUrl +
                    "pageAction=getInitalConditon&uniqueId=" + iRETURNuuID;

            Request requestScan = new Request.Builder().url(url).build();

            new OkHttpClient().newCall(requestScan).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    if(!getIsCompletedCheckIn()){
                        //Get the status
                        Gson gson = new Gson();
                        ResponseBody responseBody =response.body();
                        SummaryInitialResultModel entity = gson.fromJson(responseBody.string(), SummaryInitialResultModel.class);

                        if(entity != null) {
                            if (entity.getIsAvailable().equals("TRUE")){

                                if(!isCheckIn){
                                    String statusCode = entity.getStatusCode();
                                    response.body().close();

                                    if(statusCode.equals("200")){
                                        setIsCompletedCheckIn(true);
                                        setIsCompletedinitial(true);
                                        Intent intent = new Intent(context,MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("Submitted", "Submitted");
                                        context.startActivity(intent);
                                    }

                                    if(statusCode.equals("404")){
                                        setIsCompletedCheckIn(true);
                                        Intent intent = new Intent(context,CheckinError.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("OUT_ERROR", statusCode);
                                        context.startActivity(intent);
                                    }

                                    if(statusCode.equals("409")){
                                        setIsCompletedCheckIn(true);
                                        getSummaryResultCheck(iRETURNuuID, "RETURN");
                                    }

                                    if(statusCode.equals("200") || statusCode.equals("404") ){
                                        deleteUUID(iRETURNuuID);
                                    }

                                    isCheckIn = true;
                                }
                            }
                        }
                    }
                }
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                    e.printStackTrace();

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getSummaryResultCheck(String strUUID, String strUserStatus){

        try{

            String url = appScriptUrl +
                    "uniqueId=" + strUUID + "&pageAction=getInventorySummary";

            Request requestScan = new Request.Builder().url(url).build();

            new OkHttpClient().newCall(requestScan).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    Gson gson = new Gson();
                    ResponseBody responseBody =response.body();
                    SummaryModel entity = gson.fromJson(responseBody.string(), SummaryModel.class);

                    if(entity != null) {
                        if (entity.getIsAvailable().equals("TRUE")) {
                            response.body().close();

                            if(strUserStatus.equals("BORROW")){
                                Intent intent = new Intent(context,CheckoutError.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("OUT_ERROR", entity.getStatusCode());
                                intent.putExtra("OUT_ERROR_SERIAL_NO", entity.getSerialNo());
                                intent.putExtra("OUT_ERROR_REQUEST_TIMESTAMP",
                                        entity.getTimestamp());
                                intent.putExtra("OUT_ERROR_REQUEST_OFFICER",
                                        entity.getRequestOfficer() + ", "
                                                + entity.getDepartment());
                                context.startActivity(intent);
                                intent.putExtra("initialUID", initialUID);
                            }else{
                                Intent intent = new Intent(context,CheckinError.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("OUT_ERROR", entity.getStatusCode());
                                intent.putExtra("initialUID", initialUID);
                                context.startActivity(intent);
                            }

                            deleteUUID(strUUID);
                        }
                    }
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

    public void deleteUUID (String strUUID){

        //initialUID = UUID.randomUUID().toString();

        String url = appScriptUrl +
                "pageAction=deleteUUID&uniqueId=" + strUUID;

        //very important
        setIsCompletedinitial(false);

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

    //Stock-Take
    public void stockTakeInitial(String serialNo){
        stockTakeUUID = UUID.randomUUID().toString();

        String strSerialNo = null;

        try {
            strSerialNo = URLEncoder.encode(serialNo, Charsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        stockTakeSerial = strSerialNo;

        String url = "https://plumber.gov.sg/webhooks/cf6ed174-143e-4e8a-b18c-39820938276c?" +
                "serialNo=" + strSerialNo.trim() + "&uniqueId=" + stockTakeUUID + "&timestamp=" + getTodayDate();

        //very important
        setIsCompletedStockTake(false);

        isStocktake = false;

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

    public void getStockTakeStatus(){

        try{

            String url = stockAppScriptUrl +
                    "pageAction=getUniqueStatus&uniqueId=" + stockTakeUUID;

            Request requestScan = new Request.Builder().url(url).build();

            new OkHttpClient().newCall(requestScan).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    if(!getIsCompletedStockTake()){
                        //Get the status
                        Gson gson = new Gson();
                        ResponseBody responseBody =response.body();
                        SummaryInitialModel entity = gson.fromJson(responseBody.string(), SummaryInitialModel.class);

                        if(entity != null){
                            if(entity.getStatus().equals(COMPLETED)) {
                                //setIsCompleted(true);
                                response.body().close();
                                getStockTakesResult();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                    e.printStackTrace();

                }
            });
        }catch (Exception e){

        }
    }

    public void getStockTakesResult(){

        try{
            String url = stockAppScriptUrl +
                    "pageAction=getInitalConditon&uniqueId=" + stockTakeUUID;
            Request requestScan = new Request.Builder().url(url).build();

            new OkHttpClient().newCall(requestScan).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    if(!getIsCompletedStockTake()){
                        //Get the status
                        Gson gson = new Gson();
                        ResponseBody responseBody =response.body();
                        SummaryInitialResultModel entity = gson.fromJson(responseBody.string(), SummaryInitialResultModel.class);

                        if(entity != null) {
                            if (entity.getIsAvailable().equals("TRUE")){
                                String statusCode = entity.getStatusCode();
                                response.body().close();

                                    if(!isStocktake){

                                        if(statusCode.equals("404")){
                                            setIsCompletedStockTake(true);

                                            Intent intent = new Intent(context,StockTakeError.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.putExtra("OUT_ERROR", statusCode);
                                            intent.putExtra("SERIALNO", stockTakeSerial);
                                            context.startActivity(intent);
                                            deleteStickUUID(stockTakeUUID);

                                        }else if (statusCode.equals("200")){
                                            setIsCompletedStockTake(true);
                                            getSummaryStockResult(stockTakeUUID);

                                        }
                                        isStocktake = true;
                                    }
                            }
                        }
                    }
                }
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                    e.printStackTrace();

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void getSummaryStockResult(String strUUID) {

        String url = stockAppScriptUrl +
                "uniqueId=" + strUUID + "&pageAction=getInventorySummary";

        Request requestScan = new Request.Builder().url(url).build();

        new OkHttpClient().newCall(requestScan).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                Gson gson = new Gson();
                ResponseBody responseBody = response.body();
                StocktakeModel entity = gson.fromJson(responseBody.string(), StocktakeModel.class);

                if (entity != null) {
                    if (entity.getIsAvailable().equals("TRUE")) {
                        response.body().close();

                        if (entity.getStatusCode().equals("200")) {

                            Intent intent = new Intent(context,StocktakePass.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("OUT_ERROR", entity.getStatusCode());
                            intent.putExtra("SERIALNO", stockTakeSerial);
                            intent.putExtra("itemDesc", entity.getItemDescription());
                            context.startActivity(intent);
                        }
                        //Delete the initialUID
                        deleteUUID(initialUID);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

                e.printStackTrace();

            }
        });
    }

    public void deleteStickUUID (String strUUID){

        //initialUID = UUID.randomUUID().toString();

        String url = stockAppScriptUrl +
                "pageAction=deleteUUID&uniqueId=" + strUUID;

        //very important
        setIsCompletedStockTake(false);

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

    //Login



    //GET or SET
    Boolean getIsCompletedInitial(){
        return isCompletedInitial;
    }

    void setIsCompletedinitial(boolean isCompleted){
        this.isCompletedInitial = isCompleted;
    }

    //isCompletedCheckout

    Boolean getIsCompletedCheckout(){
        return isCompletedCheckout;
    }

    void setIsCompletedCheckout(boolean isCompleted){
        this.isCompletedCheckout = isCompleted;
    }

    Boolean getIsCompletedCheckIn(){return isCompletedCheckin;}

    //Stocktake

    void setIsCompletedCheckIn(boolean isCompleted){
        this.isCompletedCheckin = isCompleted;
    }

    Boolean getIsCompletedStockTake(){
        return isStockTakeCompleted;
    }

    void setIsCompletedStockTake(boolean isCompleted){
        this.isStockTakeCompleted = isCompleted;
    }

    /**/
    void setIsViewReportLogin(String status){
        this.strIsViewCredentialReport = status;
    }

    String getIsViewReportLogin(){
        return this.strIsViewCredentialReport;
    }

    void setStrSendEmailAddress(String strSendEmailAddress){
        this.strSendEmailAddress = strSendEmailAddress;
    }

    String getStrSendEmailAddress(){
        return strSendEmailAddress;
    }
}
