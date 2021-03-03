package com.example.hardbank;

public class Consultant  {

    String email;
    String fullname;
    String id;
    String type;

    public Consultant(String email, String fullname, String id, String type) {
        this.email = email;
        this.fullname = fullname;
        this.id = id;
        this.type = type;
    }

    public  Consultant(){

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

    public String getType() {
        return type;
    }
}
