package com.veritas.veritas.DB.Firebase.entity;

import static com.veritas.veritas.Util.CurrentTime.getCurrentTimeStamp;
import static com.veritas.veritas.Util.PublicVariables.getGames;

import android.util.Log;

import java.util.ArrayList;

public class Question {
    private String text;
    private String type;
    private String timeStamp;
    private ArrayList<Answer> answers;

    public Question() {}

    public Question(String TAG, String text, String type) {
        boolean isTypeCorrect = false;
        if (type.equals("init")) {
            isTypeCorrect = true;
        } else {
            for (String rawType : getGames()) {
                if (rawType.equals(type)) {
                    isTypeCorrect = true;
                    break;
                }
            }
        }

        if (isTypeCorrect) {
            this.text = text;
            this.type = type;
            timeStamp = getCurrentTimeStamp();
            answers = new ArrayList<>();
        } else {
            Log.e(TAG, "question class got inappropriate question type.\nReceived question type: " + type);
        }

    }

    public void addAnswer(Answer answer) {
        answers.add(answer);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<Answer> answers) {
        this.answers = answers;
    }
}
