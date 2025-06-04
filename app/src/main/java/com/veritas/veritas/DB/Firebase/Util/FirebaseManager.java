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

    public static final String GROUPS_KEY = "groups";
    public static final String GROUPS_MAP_KEY = "groupsMap";
    public static final String PARTICIPANTS_KEY = "participants";

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();;

    private String groupId;

    public FirebaseManager() {}

    /**
     * @param groupId groupId of the currently connected group
     */
    public FirebaseManager(String groupId) {
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



    /**
     * Интерфейс для callback'ов при валидации кода группы
     */
    public interface OnGroupCodeValidationListener {
        void onValidCode(String groupId);
        void onInvalidCode();
        void onError(String error);
    }

    /**
     * Валидирует код группы и возвращает ID группы если код существует
     *
     * ПОДВОДНЫЕ КАМНИ И ВАЖНЫЕ МОМЕНТЫ:
     *
     * 1. СИНХРОНИЗАЦИЯ ДАННЫХ: Самый критичный момент!
     *    - При создании новой группы ОБЯЗАТЕЛЬНО нужно создавать запись в group_codes
     *    - При удалении группы ОБЯЗАТЕЛЬНО нужно удалять запись из group_codes
     *    - При изменении кода группы нужно обновить и основную запись, и индекс
     *    - Рекомендуется использовать Firebase Transactions для атомарности операций
     *
     * 2. RACE CONDITIONS:
     *    - Если несколько пользователей одновременно проверяют один код
     *    - Может возникнуть ситуация, когда код валиден в момент проверки,
     *      но недоступен в момент подключения к группе
     *    - Решение: всегда проверять существование группы после получения ID
     *
     * 3. КЭШИРОВАНИЕ FIREBASE:
     *    - Firebase может вернуть кэшированные данные, которые могут быть устаревшими
     *    - Особенно критично в offline режиме
     *    - При критичных операциях рассмотрите использование .getSource(Source.SERVER)
     *
     * 4. ПРОИЗВОДИТЕЛЬНОСТЬ:
     *    - Каждый вызов этого метода = 1 запрос к Firebase
     *    - При частых вызовах рассмотрите локальное кэширование
     *    - Метод работает быстро только при правильной индексации
     *
     * 5. ОБРАБОТКА NULL/ПУСТЫХ ЗНАЧЕНИЙ:
     *    - Всегда проверяем на null и пустые строки
     *    - Firebase может вернуть null значения в неожиданных местах
     *
     * @param groupCode Код группы для валидации (не должен быть null или пустым)
     * @param listener Callback для обработки результата валидации
     */
    public void validateGroupCode(String groupCode, OnGroupCodeValidationListener listener) {
        // ПРОВЕРКА 1: Базовая валидация входных данных
        if (groupCode == null || groupCode.trim().isEmpty()) {
            Log.w(TAG, "validateGroupCode called with null or empty group code");
            listener.onError("Код группы не может быть пустым");
            return;
        }

        // ОСНОВНОЙ ЗАПРОС: Поиск в индексе group_codes
        databaseReference.child(GROUPS_MAP_KEY).child(groupCode.trim())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            if (dataSnapshot.exists()) {
                                // Код найден в индексе, получаем ID группы
                                String foundGroupId = dataSnapshot.getValue(String.class);

                                // ПРОВЕРКА 3: Валидация полученного ID
                                if (foundGroupId == null || foundGroupId.trim().isEmpty()) {
                                    Log.e(TAG, "Found group code " + groupCode + " but group ID is null or empty");
                                    listener.onError("Ошибка целостности данных: некорректный ID группы");
                                    return;
                                }

                                // ДОПОЛНИТЕЛЬНАЯ ПРОВЕРКА: Убеждаемся, что группа действительно существует
                                // Это защищает от ситуации, когда в индексе есть запись, а группы нет
                                verifyGroupExists(foundGroupId, groupCode, listener);

                            } else {
                                // Код не найден в индексе
                                Log.d(TAG, "Group code not found: " + groupCode);
                                listener.onInvalidCode();
                            }

                        } catch (Exception e) {
                            // ОБРАБОТКА ИСКЛЮЧЕНИЙ: Любые ошибки парсинга или обработки
                            Log.e(TAG, "Exception during group code validation", e);
                            listener.onError("Ошибка при валидации кода: " + e.getMessage());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // ОБРАБОТКА ОШИБОК БАЗЫ ДАННЫХ
                        Log.e(TAG, "Database error during group code validation: " + databaseError.getMessage());
                        listener.onError("Ошибка базы данных: " + databaseError.getMessage());
                    }
                });
    }

    /**
     * Дополнительная проверка существования группы по ID
     * Защищает от несоответствия между индексом и основными данными
     *
     * ПОДВОДНЫЙ КАМЕНЬ: Этот дополнительный запрос удваивает количество обращений к Firebase
     * Но обеспечивает целостность данных и защищает от багов синхронизации
     *
     * @param groupId ID группы для проверки
     * @param originalCode Оригинальный код группы (для логирования)
     * @param listener Callback для результата
     */
    private void verifyGroupExists(String groupId, String originalCode, OnGroupCodeValidationListener listener) {
        databaseReference.child("groups").child(groupId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Группа существует, код валиден
                            Log.d(TAG, "Successfully validated group code: " + originalCode + " -> " + groupId);
                            listener.onValidCode(groupId);
                        } else {
                            // КРИТИЧЕСКАЯ ПРОБЛЕМА: В индексе есть запись, но группы нет
                            Log.e(TAG, "Data inconsistency: group_codes has entry for " + originalCode +
                                    " pointing to " + groupId + " but group doesn't exist");
                            listener.onError("Ошибка целостности данных: группа не найдена");

                            // TODO: Рассмотрите возможность автоматической очистки некорректных записей
                            // Но это требует прав на запись и может быть опасно
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "Database error during group existence verification: " + databaseError.getMessage());
                        listener.onError("Ошибка при проверке группы: " + databaseError.getMessage());
                    }
                });
    }

    /**
     * Проверяет, содержит ли строка символы, недопустимые в Firebase ключах
     *
     * Firebase ключи не могут содержать: . $ # [ ] /
     * Также рекомендуется избегать пробелов и специальных символов
     *
     * @param code Код для проверки
     * @return true если содержит недопустимые символы
     */
    private boolean containsInvalidFirebaseChars(String code) {
        // Список недопустимых символов для Firebase ключей
        char[] invalidChars = {'.', '$', '#', '[', ']', '/'};

        for (char invalidChar : invalidChars) {
            if (code.indexOf(invalidChar) != -1) {
                return true;
            }
        }

        // Дополнительная проверка на пробелы (хотя технически допустимы, но не рекомендуются)
        return code.contains(" ");
    }

    // ПРИМЕР ИСПОЛЬЗОВАНИЯ (можно удалить в production коде):
    /*
    public void exampleUsage() {
        validateGroupCode("ABC123", new OnGroupCodeValidationListener() {
            @Override
            public void onValidCode(String groupId) {
                // Код валиден, можно подключаться к группе
                Log.d(TAG, "Подключаемся к группе: " + groupId);
                // Здесь можно создать новый FirebaseManager с полученным groupId
                // FirebaseManager groupManager = new FirebaseManager(groupId);
            }

            @Override
            public void onInvalidCode() {
                // Код не найден
                Log.d(TAG, "Код группы не найден");
                // Показать пользователю сообщение об ошибке
            }

            @Override
            public void onError(String error) {
                // Произошла ошибка при валидации
                Log.e(TAG, "Ошибка валидации: " + error);
                // Показать пользователю сообщение об ошибке
            }
        });
    }
    */
}

