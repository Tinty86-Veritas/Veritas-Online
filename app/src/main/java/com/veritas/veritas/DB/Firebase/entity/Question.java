package com.veritas.veritas.DB.Firebase.entity;

import static com.veritas.veritas.Util.CurrentTime.getCurrentTimeStamp;
import static com.veritas.veritas.Util.PublicVariables.getGames;

import android.util.Log;

import java.util.ArrayList;
import java.util.Objects;

public class Question {
    private String key;
    private String content;
    private String type;
    private String timeStamp;
    private ArrayList<Answer> answers;

    public Question() {}

    public Question(String TAG, String content, String type) {
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
            this.content = content;
            this.type = type;
            timeStamp = getCurrentTimeStamp();
            answers = new ArrayList<>();
        } else {
            Log.e(TAG, "question class got inappropriate question type.\nReceived question type: " + type);
        }

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return Objects.equals(content, question.content) && Objects.equals(type, question.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, type);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void addAnswer(Answer answer) {
        answers.add(answer);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
