package com.example.hardbank;

public class ChatsModel {
    String consultantid;
    String customerid;
    String Status;
    public ChatsModel() {

    }

    public ChatsModel(String consultantid, String customerid, String status) {
        this.consultantid = consultantid;
        this.customerid = customerid;
        Status = status;
    }

    public String getConsultantid() {
        return consultantid;
    }

    public String getCustomerid() {
        return customerid;
    }

    public String getStatus() {
        return Status;
    }
}
