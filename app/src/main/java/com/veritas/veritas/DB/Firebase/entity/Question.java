package com.veritas.veritas.DB.Firebase.entity;

import static com.veritas.veritas.Util.CurrentTime.getTimeStamp;
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
        for (String rawType : getGames()) {
            if (rawType.equals(type)) {
                isTypeCorrect = true;
                break;
            }
        }
        if (isTypeCorrect) {
            this.text = text;
            this.type = type;
            timeStamp = getTimeStamp();
            answers = new ArrayList<>();
        } else {
            Log.e(TAG, "question class got inappropriate question type.\nReceived question type: " + type);
        }

    }

    public void addAnswer(Answer answer) {
        answers.add(answer);
    }
}
