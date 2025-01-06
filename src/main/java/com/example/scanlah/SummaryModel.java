package com.example.scanlah;

public class SummaryModel {

    private String uniqueID;
    private String statusCode;
    private String serialNo;
    private String Page;

    private String RequestOfficer;

    private String Timestamp;

    public  String isAvailable;

    public String Department;

    public String ItemDescription;

    // Getter Methods
    public String getUniqueID() {
        return uniqueID;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public String getRequestOfficer(){return RequestOfficer; }

    public String getTimestamp(){return Timestamp; }

    public String getIsAvailable() {
        return isAvailable;
    }

    public String getDepartment() {
        return Department;
    }

    public String getItemDescription() {
        return ItemDescription;
    }

    // Setter Methods

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public void setRequestOfficer(String RequestOfficer){this.RequestOfficer = RequestOfficer;}

    public void setTimestamp(String Timestamp){this.Timestamp = Timestamp;}

    public void setIsAvailable(String isAvailable) {
        this.isAvailable = isAvailable;
    }

    public void setDepartment(String Department){
        this.Department = Department;
    }

    public void setItemDescription(String ItemDescription){
        this.ItemDescription = ItemDescription;
    }
}
