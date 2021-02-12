package com.example.hardbank;

public class Seller {

    String address;
    String doc;
    String email;
    String fullname;
    String id;
    String shopname;
    String type;
    String verified;

    public Seller() {
    }
    public Seller(String address, String doc, String email, String fullname, String id, String shopname, String type, String verified ) {
        this.address = address;
        this.doc = doc;
        this.email = email;
        this.fullname = fullname;
        this.id = id;
        this.shopname = shopname;
        this.type = type;
        this.verified = verified;
    }

    public String getAddress() {
        return address;
    }

    public String getDoc() {
        return doc;
    }

    public String getEmail() {
        return email;
    }

    public String getFullname() {
        return fullname;
    }

    public String getId() {
        return id;
    }

    public String getShopname() {
        return shopname;
    }

    public String getType() {
        return type;
    }

    public String getVerified() {
        return verified;
    }
}
