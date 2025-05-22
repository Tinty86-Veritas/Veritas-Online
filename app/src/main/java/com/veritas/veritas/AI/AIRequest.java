package com.veritas.veritas.AI;

import static com.veritas.veritas.Util.PublicVariables.DARE;
import static com.veritas.veritas.Util.PublicVariables.NEVEREVER;
import static com.veritas.veritas.Util.PublicVariables.TRUTH;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.veritas.veritas.Adapters.entity.User;
import com.veritas.veritas.DB.GamesDB;
import com.veritas.veritas.DB.UsersDB;
import com.veritas.veritas.Exceptions.EmptyUsersList;
import com.veritas.veritas.Exceptions.NotEnoughPlayers;
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

// TODO: нужно отлавливать ошибку вызываемую достиганием порога доступных запросов к нейронке

public class AIRequest {

    private static final String TAG = "AIRequest";

    private static final String API_KEY = ____;
    private static final String API_URL = ____;

    private static String prompt;

    private int answersNum;

    Gson gson = new Gson();

    public AIRequest(Context context, String modeName, String gameName)
            throws EmptyUsersList, NotEnoughPlayers {

        Map<String, Object[]> reactions = new HashMap<>();

        GamesDB gamesDB = new GamesDB(context);

        for (String type : new String[] {"like", "dislike", "recurring"}) {
            List<String> rawContentList = gamesDB.getReaction(gameName, modeName, type);
            String[] contentList = new String[rawContentList.size()];
            contentList = rawContentList.toArray(contentList);

            if (!rawContentList.isEmpty()) {
                reactions.put(type, contentList);
            }
        }

        String reactionsJson;

        if (!reactions.isEmpty()) {
            reactionsJson = gson.toJson(reactions);
        } else {
            reactionsJson = "No reactions";
        }

        Log.d(TAG, "reactionsJson:\n" + reactionsJson);

        answersNum = gamesDB.getRequestNum(gameName, modeName);

        gamesDB.close();

        UsersDB usersDB = new UsersDB(context);

        ArrayList<User> users = usersDB.selectAllFromPlayers();

        if (users.isEmpty()) {
            throw new EmptyUsersList(TAG);
        } else if (users.size() == 1 && !gameName.equals(NEVEREVER)) {
            throw new NotEnoughPlayers(TAG);
        }

        usersDB.close();

        HashMap<String, HashMap<String, String>> payload = new HashMap<>();

        HashMap<String, String> cur_user = new HashMap<>();

        for (User user : users) {
            cur_user.put(user.getName(), user.getSex());
            payload.put("players", cur_user);
        }

        String participants = gson.toJson(payload);

        Log.i(TAG, "participantsJSON:\n" + participants);

        switch (gameName) {
            case TRUTH ->
                    prompt = String.format(context.getString(R.string.truth_prompt).trim(),
                            answersNum, reactionsJson)
                            + "Режим: " + modeName
                            + ". Участники и их пола: " + participants;
            case DARE ->
                    prompt = String.format(context.getString(R.string.dare_prompt).trim(),
                            answersNum, reactionsJson)
                            + "Режим: " + modeName
                            + ". Участники и их пола: " + participants;
            case NEVEREVER ->
                    prompt = String.format(context.getString(R.string.neverEver_prompt).trim(),
                            answersNum, reactionsJson)
                            + "Режим: " + modeName;
            default -> Log.e(TAG, "gameName is inappropriate");
        }

        Log.d(TAG, "prompt:\n" + prompt);
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
