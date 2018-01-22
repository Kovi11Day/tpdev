package com.test.kovilapauvaday.prototype_connect.messages;

/**
 * Created by ARAM on 13/01/2018.
 */

public class Message {

    private String message;
    private String from;

    public Message(String message, String from) {
        this.message = message;
        this.from = from;
    }

    public Message(String from) {
        this.from = from;
    }

    public Message(){
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }


}
