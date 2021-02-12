package com.example.hardbank;

import com.google.firebase.Timestamp;

public class Answers {
    String answer;
    String  customerid;

    Timestamp time;

    public Answers(){

    }
    public Answers(String answer, String customerid, Timestamp time){
        this.answer = answer;
        this.customerid = customerid;
        this.time = time;
    }


    public Timestamp getTime() {
        return time;
    }

    public String getAnswer() {
        return answer;
    }

    public String getCustomerid() {
        return customerid;
    }
}
