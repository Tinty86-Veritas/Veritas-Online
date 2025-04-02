package com.veritas.veritas.AI;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.veritas.veritas.Adapters.entity.User;
import com.veritas.veritas.DB.UsersDB;
import com.veritas.veritas.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AIRequest {

    private static final String TAG = "AIRequest";

    private static final String API_KEY = "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";
    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";

    private static String prompt;

    private boolean isDare;

    // developing only

    private final static int answersNum = 30;

//    prompt = "В чем смысл жизни?";

    // ---------------

    Gson gson = new Gson();

    public AIRequest(Context context, String mode_name, boolean isDare) {

        this.isDare = isDare;

        UsersDB usersDB = new UsersDB(context);

        ArrayList<User> users = usersDB.selectAllFromPlayers();

        HashMap<String, HashMap<String, String>> payload = new HashMap<>();

        HashMap<String, String> cur_user = new HashMap<>();

        for (User user : users) {
            cur_user.put(user.getName(), user.getSex());
            payload.put("players", cur_user);
        }

        String participants = gson.toJson(payload);

        Log.i(TAG, "participantsJSON:\n" + participants);

        if (mode_name != null) {
            if (isDare) {
                prompt = String.format(context.getString(R.string.dare_prompt).trim(), answersNum)
                        + "Режим: " + mode_name
                        + ". Участники и их пола: " + participants;
            } else {
                prompt = String.format(context.getString(R.string.truth_prompt).trim(), answersNum)
                        + "Режим: " + mode_name
                        + ". Участники и их пола: " + participants;
            }
        } else {
            Log.wtf(TAG, "mode name is somehow null");
        }
    }

    private String createJSON(String message_content) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("model", "deepseek/deepseek-chat:free");

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", message_content);
        messages.add(message);

        payload.put("messages", messages);

        return gson.toJson(payload);
    }

    public void sendPOST(ApiCallback callback) {

        Log.i(TAG, "sendPOST method entered");

        OkHttpClient client = new OkHttpClient();

        Headers headers = new Headers.Builder()
                .add("Authorization", "Bearer " + API_KEY)
                .add("Content-Type", "application/json")
                .build();

        String jsonBody = createJSON(prompt);

        RequestBody body = RequestBody.create(
                jsonBody,
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(API_URL)
                .headers(headers)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        String responseData = response.body().string().trim();
                        Log.i(TAG, "API Response:\n" + responseData);

                        Map<String, Object> root = gson.fromJson(responseData, Map.class);

                        List<Map<String, Object>> choices = (List<Map<String, Object>>) root.get("choices");

                        Map<String, Object> firstChoice = (Map) choices.get(0);

                        Map<String, Object> message = (Map) firstChoice.get("message");

                        String content = (String) message.get("content");

                        callback.onSuccess(content);

                    } else {
                        callback.onFailure("Error code: " + response.code());
                        Log.wtf(TAG, "response body is null");
                    }
                } else {
                    Log.w(TAG, "API Error. Status Code:\n" + response.code());
                }
            }
        });
    }

    public interface ApiCallback {
        void onSuccess(String content);
        void onFailure(String error);
    }
}
