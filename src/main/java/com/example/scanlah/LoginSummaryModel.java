package com.example.scanlah;

public class LoginSummaryModel {

    private String uniqueID;

    private String statusCode;

    public  String isAvailable;

    public  String isActive;

    public String username;

    public String department;

    public String isOTPEnabled;

    public String isViewReportCredential;

    public String getIsViewReportCredential() {
        return isViewReportCredential;
    }

    public void setIsViewReportCredential(String isViewReportCredential) {
        this.isOTPEnabled = isViewReportCredential;
    }

    public String getIsOTPEnabled() {
        return isOTPEnabled;
    }

    public void setIsOTPEnabled(String isOTPEnabled) {
        this.isOTPEnabled = isOTPEnabled;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.username = department;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isAvailable) {
        this.isActive = isAvailable;
    }

    public String getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(String isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }
}
