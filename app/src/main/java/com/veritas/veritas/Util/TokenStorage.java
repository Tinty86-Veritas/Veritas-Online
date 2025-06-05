package com.veritas.veritas.Util;

import static com.veritas.veritas.Application.App.getVKID;
import static com.veritas.veritas.Util.PublicVariables.getAuthCallback;
import static com.veritas.veritas.Util.PublicVariables.getAuthParams;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.veritas.veritas.R;
import com.vk.id.AccessToken;
import com.vk.id.refresh.VKIDRefreshTokenCallback;
import com.vk.id.refresh.VKIDRefreshTokenFail;
import com.vk.id.refresh.VKIDRefreshTokenParams;

import javax.crypto.SecretKey;

public class TokenStorage {
    private static final String TAG = "TokenStorage";

    private static final String PREF_NAME = "secure_prefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_EXPIRE_TIME = "expire_time";
    private static final String KEY_USER_ID = "user_id";
    private final SharedPreferences sharedPreferences;
    private SecretKey secretKey;
    private Context context;

    public TokenStorage(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        try {
            secretKey = KeyStoreHelper.getSecretKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void saveAccessToken(String token, long userId, long expireTime) {
        String encryptedToken;
        try {
            encryptedToken = CryptoHelper.encrypt(token, secretKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        sharedPreferences.edit()
                .putString(KEY_ACCESS_TOKEN, encryptedToken)
                .putLong(KEY_USER_ID, userId)
                .putLong(KEY_EXPIRE_TIME, expireTime)
                .apply();
    }

    public String getAccessToken(LifecycleOwner lifecycleOwner) {
        String encryptedToken = sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
        // I think accessToken can be null only if user has not been authorized before
        if (encryptedToken == null) {
            Log.d(TAG, "accessToken is null");
            Toast.makeText(context, R.string.user_not_authorized, Toast.LENGTH_LONG).show();
            return null;
        }
        String decryptedToken;
        try {
            decryptedToken = CryptoHelper.decrypt(encryptedToken, secretKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (getExpireTime() <= System.currentTimeMillis() / 1000) {
            getVKID().getInstance().refreshToken(lifecycleOwner, new VKIDRefreshTokenCallback() {
                @Override
                public void onSuccess(@NonNull AccessToken accessToken) {
                    saveAccessToken(accessToken.getToken(), accessToken.getUserID(), accessToken.getExpireTime());

                    getAccessToken(lifecycleOwner);
                }

                @Override
                public void onFail(@NonNull VKIDRefreshTokenFail vkidRefreshTokenFail) {
                    if (vkidRefreshTokenFail instanceof VKIDRefreshTokenFail.RefreshTokenExpired) {
                        // RefreshToken истёк, требуется повторная авторизация
                        getVKID().getInstance().authorize(lifecycleOwner, getAuthCallback(TAG, context), getAuthParams());
                    } else {
                        // Обработка других ошибок
                    }
                }
            }, new VKIDRefreshTokenParams.Builder().build());
        }
        return decryptedToken;
    }

    public long getUserId() {
        return sharedPreferences.getLong(KEY_USER_ID, 0);
    }

    public long getExpireTime() {
        return sharedPreferences.getLong(KEY_EXPIRE_TIME, 0);
    }

    public void clearToken() {
        sharedPreferences.edit()
                .remove(KEY_ACCESS_TOKEN)
                .remove(KEY_EXPIRE_TIME)
                .apply();
    }
}
