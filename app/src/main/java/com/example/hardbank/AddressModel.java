package com.example.hardbank;

public class AddressModel {

    String address;
    String name;
    String phone;

    public AddressModel() {
    }

    public AddressModel(String address, String name, String phone) {
        this.address = address;
        this.name = name;
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
}
