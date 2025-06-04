package com.veritas.veritas.DB.Firebase.Util;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.veritas.veritas.DB.Firebase.entity.Group;
import com.veritas.veritas.DB.Firebase.entity.Question;

import java.util.ArrayList;

public class FirebaseManager {
    private static final String TAG = "FirebaseManager";

    private DatabaseReference databaseReference;

    private String groupId;

    /**
     * @param groupId groupId of the currently connected group
     */
    public FirebaseManager(String groupId) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        this.groupId = groupId;
    }

    /**
     * Интерфейс для callback'ов при получении вопросов
     */
    public interface OnQuestionsRetrievedListener {
        void onSuccess(ArrayList<Question> questions);
        void onFailure(String error);
    }

    /**
     * Интерфейс для callback'ов при обновлении вопросов
     */
    public interface OnQuestionsUpdatedListener {
        void onSuccess();
        void onFailure(String error);
    }

    /**
     * Получить ArrayList<Question> для конкретной группы
     * @param listener Callback для обработки результата
     */
    public void getGroupQuestions(OnQuestionsRetrievedListener listener) {
        databaseReference.child("groups").child(groupId).child("questions")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            ArrayList<Question> questions = new ArrayList<>();

                            if (dataSnapshot.exists()) {
                                for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {
                                    Question question = questionSnapshot.getValue(Question.class);
                                    if (question != null) {
                                        questions.add(question);
                                    }
                                }
                            }

                            Log.d(TAG, "Successfully retrieved " + questions.size() + " questions for group " + groupId);
                            listener.onSuccess(questions);

                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing questions data", e);
                            listener.onFailure("Ошибка при парсинге данных: " + e.getMessage());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Database error: " + error.getMessage());
                        listener.onFailure("Ошибка базы данных: " + error.getMessage());
                    }
                });
    }

    /**
     * Обновить ArrayList<Question> для конкретной группы
     * @param questions Обновленный список вопросов
     * @param listener Callback для обработки результата
     */
    public void updateGroupQuestions(ArrayList<Question> questions,
                                     OnQuestionsUpdatedListener listener) {
        databaseReference.child("groups").child(groupId).child("questions")
                .setValue(questions)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Successfully updated questions for group " + groupId);
                    listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update questions for group " + groupId, e);
                    listener.onFailure("Ошибка при обновлении: " + e.getMessage());
                });
    }

    /**
     * Callback interface when receiving a question
     */
    public interface OnQuestionGotListener {
        void onSuccess(Question question);
        void onFailure(String error);
    }

    /**
     * Get question by index
     * @param questionIndex The index of the question to be found
     * @param listener The callback for processing the result
     */
    public void getQuestionByIndex(int questionIndex, OnQuestionGotListener listener) {
        getGroupQuestions(new OnQuestionsRetrievedListener() {
            @Override
            public void onSuccess(ArrayList<Question> questions) {
                if (questionIndex >= 0 && questionIndex < questions.size()) {
                    Question question = questions.get(questionIndex);
                    listener.onSuccess(question);
                } else {
                    listener.onFailure("Некорректный индекс вопроса: " + questionIndex + ", размер списка: " + questions.size());
                }
            }

            @Override
            public void onFailure(String error) {
                listener.onFailure(error);
            }
        });
    }

    // TODO: Check for question duplicate before adding it

    /**
     * Добавить новый вопрос к существующему списку
     * @param newQuestion Новый вопрос для добавления
     * @param listener Callback для обработки результата
     */
    public void addQuestion(Question newQuestion,
                            OnQuestionsUpdatedListener listener) {
        // Сначала получаем текущий список
        getGroupQuestions(new OnQuestionsRetrievedListener() {
            @Override
            public void onSuccess(ArrayList<Question> currentQuestions) {
                if (currentQuestions.contains(newQuestion)) {
                    listener.onFailure("duplicating question");
                } else {
                    // Добавляем новый вопрос
                    currentQuestions.add(newQuestion);

                    // Обновляем в базе данных
                    updateGroupQuestions(currentQuestions, listener);
                }
            }

            @Override
            public void onFailure(String error) {
                listener.onFailure(error);
            }
        });
    }

    /**
     * Удалить вопрос по индексу
     * @param questionIndex Индекс вопроса для удаления
     * @param listener Callback для обработки результата
     */
    public void removeQuestion(int questionIndex,
                               OnQuestionsUpdatedListener listener) {
        getGroupQuestions(new OnQuestionsRetrievedListener() {
            @Override
            public void onSuccess(ArrayList<Question> currentQuestions) {
                if (questionIndex >= 0 && questionIndex < currentQuestions.size()) {
                    currentQuestions.remove(questionIndex);
                    updateGroupQuestions(currentQuestions, listener);
                } else {
                    listener.onFailure("Некорректный индекс вопроса");
                }
            }

            @Override
            public void onFailure(String error) {
                listener.onFailure(error);
            }
        });
    }

    /**
     * Обновить конкретный вопрос по индексу
     * @param questionIndex Индекс вопроса для обновления
     * @param updatedQuestion Обновленный вопрос
     * @param listener Callback для обработки результата
     */
    public void updateQuestionByIndex(int questionIndex, Question updatedQuestion,
                                      OnQuestionsUpdatedListener listener) {
        getGroupQuestions(new OnQuestionsRetrievedListener() {
            @Override
            public void onSuccess(ArrayList<Question> currentQuestions) {
                if (questionIndex >= 0 && questionIndex < currentQuestions.size()) {
                    currentQuestions.set(questionIndex, updatedQuestion);
                    updateGroupQuestions(currentQuestions, listener);
                } else {
                    listener.onFailure("Некорректный индекс вопроса");
                }
            }

            @Override
            public void onFailure(String error) {
                listener.onFailure(error);
            }
        });
    }

    /**
     * Получить всю группу целиком
     * @param listener Callback для обработки результата
     */
    public void getFullGroup(OnGroupRetrievedListener listener) {
        databaseReference.child("groups").child(groupId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            Group group = dataSnapshot.getValue(Group.class);
                            if (group != null) {
                                Log.d(TAG, "Successfully retrieved full group " + groupId);
                                listener.onSuccess(group);
                            } else {
                                listener.onFailure("Группа не найдена");
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing group data", e);
                            listener.onFailure("Ошибка при парсинге данных группы: " + e.getMessage());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "Database error: " + databaseError.getMessage());
                        listener.onFailure("Ошибка базы данных: " + databaseError.getMessage());
                    }
                });
    }

    /**
     * Интерфейс для callback'ов при получении полной группы
     */
    public interface OnGroupRetrievedListener {
        void onSuccess(Group group);
        void onFailure(String error);
    }
}

