package com.example.chitchat;

public class Message {
    private String from, message, type, to;

    public Message() {

    }

    public Message(String from, String message, String type, String to) {
        this.from = from;
        this.message = message;
        this.type = type;
        this.to = to;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
