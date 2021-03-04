package com.example.hardbank;

import com.google.firebase.Timestamp;

public class ChatMessage  {

    String  message;
    String senderid;
    Timestamp time;

    public ChatMessage(String message, String senderid, Timestamp time) {
        this.message = message;
        this.senderid = senderid;
        this.time = time;
    }
    public  ChatMessage(){

    }

    public String getMessage() {
        return message;
    }

    public String getSenderid() {
        return senderid;
    }

    public Timestamp getTime() {
        return time;
    }
}
