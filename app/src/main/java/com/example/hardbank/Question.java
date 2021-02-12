package com.example.hardbank;

import com.google.firebase.Timestamp;

public class Question {
    String question;
    String userid;
    Timestamp time;

    public Question(){

    }
    public  Question(String question, String userid, Timestamp time){
        this.question = question;
        this.time = time;
        this.userid = userid;
    }

    public Timestamp getTime() {
        return time;
    }

    public String getUserid() {
        return userid;
    }

    public String getQuestion() {
        return question;
    }
}
