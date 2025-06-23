package com.veritas.veritas.DB.Firebase.entity;

import java.util.ArrayList;

public class Group {
    private String id;
    private String joinCode;

    // TODO: I should change host's instance from GroupParticipant to just Long and rename it to host_id
    private GroupParticipant host;
    private ArrayList<GroupParticipant> participants;
    private ArrayList<Question> questions;

    public Group() {}

    public Group(String id, GroupParticipant host,
                 ArrayList<GroupParticipant> participants, ArrayList<Question> questions) {
        this.id = id;
        this.host = host;
        this.joinCode = generateJoinCode();
        this.participants = participants;
        this.questions = questions;
    }

    // I want to remove it but I have only an hour before stop-code :[
    private String generateJoinCode() {
        // May be it is a good idea to generate join codes based on host id
        // place holder
        return "ABC";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJoinCode() {
        return joinCode;
    }

    public void setJoinCode(String joinCode) {
        this.joinCode = joinCode;
    }

    public GroupParticipant getHost() {
        return host;
    }

    public void setHost(GroupParticipant host) {
        this.host = host;
    }

    public ArrayList<GroupParticipant> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<GroupParticipant> participants) {
        this.participants = participants;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }
}
