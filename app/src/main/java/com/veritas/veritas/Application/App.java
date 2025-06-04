package com.veritas.veritas.Application;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.LifecycleOwner;

import java.util.Locale;

import com.veritas.veritas.R;
import com.vk.id.AccessToken;
import com.vk.id.VKID;
import com.vk.id.refresh.VKIDRefreshTokenCallback;
import com.vk.id.refresh.VKIDRefreshTokenFail;
import com.vk.id.refresh.VKIDRefreshTokenParams;

public class App extends Application {
    private static final String TAG = "App";

    private VKID.Companion vkid;
    private static AccessToken accessToken;

    @Override
    public void onCreate() {
        super.onCreate();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        vkid = VKID.Companion;

        vkid.init(this);
        vkid.getInstance().setLocale(new Locale("ru"));
//        vkid.setLogsEnabled(true);
    }

    public static VKID.Companion getVKID() {
        return VKID.Companion;
    }

    public static void setAccessToken(AccessToken accessToken) {
        App.accessToken = accessToken;
    }

    // TODO: I am not sure if when the token expires accessToken.getExpireTime() will return 0
    public static AccessToken getAccessToken(LifecycleOwner lifecycleOwner, Context context) {

        // I think accessToken can be null only if user has not been authorized before
        if (accessToken == null) {
            Log.d(TAG, "accessToken is null");
            Toast.makeText(context, R.string.user_not_authorized, Toast.LENGTH_SHORT).show();
            return null;
        }

        if (accessToken.getExpireTime() == 0) {
            getVKID().getInstance().refreshToken(lifecycleOwner, new VKIDRefreshTokenCallback() {
                @Override
                public void onSuccess(@NonNull AccessToken accessToken) {
                    App.accessToken = accessToken;
                }

                @Override
                public void onFail(@NonNull VKIDRefreshTokenFail vkidRefreshTokenFail) {
                    Log.e(TAG, "VKIDRefreshTokenFail:\n" + vkidRefreshTokenFail.getDescription());
                }
            }, new VKIDRefreshTokenParams.Builder().build());
        }
        return accessToken;
    }
}
