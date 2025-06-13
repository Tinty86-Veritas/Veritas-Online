package com.veritas.veritas.Util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.vk.id.AccessToken;
import com.vk.id.VKIDAuthFail;
import com.vk.id.auth.AuthCodeData;
import com.vk.id.auth.VKIDAuthCallback;
import com.vk.id.auth.VKIDAuthParams;

public class PublicVariables {

    public static final String MODE_FUN = "Fun";
    public static final String MODE_SOFT = "Soft";
    public static final String MODE_HOT = "Hot";
    public static final String MODE_EXTREME = "Extreme";
    public static final String MODE_MADNESS = "Madness";

    private static final String[] modes = new String[] {
            "Fun", "Soft", "Hot", "Extreme", "Madness"
    };

    public static final String TRUTH = "truth";
    public static final String DARE = "dare";
    public static final String NEVEREVER = "neverEver";

    private static final String[] games = new String[] {
            TRUTH, DARE, NEVEREVER
    };

    public static String[] getModes() {
        return modes;
    }

    public static String[] getGames() {
        return games;
    }


    private static final VKIDAuthParams authParams = new VKIDAuthParams.Builder().build();

    public static VKIDAuthParams getAuthParams() {
        return authParams;
    }

    public static VKIDAuthCallback getAuthCallback(String TAG, Context context) {
        return new VKIDAuthCallback() {
            @Override
            public void onAuthCode(@NonNull AuthCodeData authCodeData, boolean b) {
                Log.d(TAG, "onAuthCode");
            }

            @Override
            public void onAuth(@NonNull AccessToken accessToken) {
                Log.d(TAG, "onAuth");

                Toast.makeText(context, "Вы успешно авторизовались", Toast.LENGTH_SHORT).show();

                TokenStorage tokenStorage = new TokenStorage(context);
                tokenStorage.saveAccessToken(accessToken.getToken(), accessToken.getUserID(), accessToken.getExpireTime());
            }

            @Override
            public void onFail(@NonNull VKIDAuthFail authFail) {
                Log.d(TAG, "onFail");
                // Авторизация не удалась. Обработайте ошибку
                String errorMessage = "VK ID Auth Failed: " + authFail.getDescription();
                errorMessage += " - " + authFail.getDescription();
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
            }
        };
    }
}
