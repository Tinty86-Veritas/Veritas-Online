package com.veritas.veritas.DB.Firebase.entity;

import static com.veritas.veritas.Util.CurrentTime.getTimeStamp;

public class Answer {
    private String senderId;
    private String text;
    private String timeStamp;

    public Answer() {}

    public Answer(String senderId, String text) {
        this.senderId = senderId;
        this.text = text;
        this.timeStamp = getTimeStamp();
    }
}
