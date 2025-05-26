package com.veritas.veritas.DB.Firebase.entity;

import static com.veritas.veritas.Util.CurrentTime.getCurrentTimeStamp;

public class Answer {
    private String senderId;
    private String text;
    private String timeStamp;

    public Answer() {}

    public Answer(String senderId, String text) {
        this.senderId = senderId;
        this.text = text;
        this.timeStamp = getCurrentTimeStamp();
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
