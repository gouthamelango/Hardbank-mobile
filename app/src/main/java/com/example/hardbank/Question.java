package com.example.hardbank;

public class Question {
    String question;
    String userid;

    public Question(){

    }
    public  Question(String question, String userid){
        this.question = question;
    }

    public String getUserid() {
        return userid;
    }

    public String getQuestion() {
        return question;
    }
}
