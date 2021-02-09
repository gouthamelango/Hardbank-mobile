package com.example.hardbank;

public class Answers {
    String answer;
    String  customerid;

    public Answers(){

    }
    public Answers(String answer, String customerid){
        this.answer = answer;
        this.customerid = customerid;
    }

    public String getAnswer() {
        return answer;
    }

    public String getCustomerid() {
        return customerid;
    }
}
