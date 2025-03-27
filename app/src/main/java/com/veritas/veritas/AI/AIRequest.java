package com.veritas.veritas.AI;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
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

//    private Context context;

    private static final String TAG = "AIRequest";

    private static final String API_KEY = "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";
    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";

    // developing const only

    private static final String TEST_CONTENT = "В чем смысл жизни?";

    // ---------------

//    private final String FUN_MODE_PROMPT = context.getString(R.string.FUN_MODE_PROMPT).trim();
//
//    public AIRequest(Context context) {
//        this.context = context;
//    }

    public interface ApiCallback {
        void onSuccess(String content);
        void onFailure(String error);
    }

    private static String createJSON(Gson gson, String message_content) {
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

    public static void sendPOST(ApiCallback callback) {

        Gson gson = new Gson();

        Log.i(TAG, "sendPOST method entered");

        OkHttpClient client = new OkHttpClient();

        Headers headers = new Headers.Builder()
                .add("Authorization", "Bearer " + API_KEY)
                .add("Content-Type", "application/json")
                .build();

        String jsonBody = createJSON(gson, TEST_CONTENT);

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
//                        Map<String, String> jsonResponse = gson.fromJson(responseData, Map.class);
//                        result = jsonResponse.get("");

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
}
