package com.veritas.veritas.DB.Firebase.entity;

import java.util.ArrayList;

public class Group {
    private String id;
    private String joinCode;
    private GroupParticipant host;
    private ArrayList<GroupParticipant> participants;
    private ArrayList<Question> questions;

    public Group() {}

    public Group(String id, String joinCode, GroupParticipant host,
                 ArrayList<GroupParticipant> participants, ArrayList<Question> questions) {
        this.id = id;
        this.host = host;
        this.joinCode = generateJoinCode();
        this.participants = participants;
        this.questions = questions;
    }

    public String generateJoinCode() {
        // place holder
        return "ABC";
    }
}
