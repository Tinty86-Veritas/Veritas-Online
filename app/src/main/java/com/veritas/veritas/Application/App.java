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
import com.veritas.veritas.Util.KeyStoreHelper;
import com.veritas.veritas.Util.TokenStorage;
import com.vk.id.AccessToken;
import com.vk.id.VKID;
import com.vk.id.VKIDAuthFail;
import com.vk.id.auth.AuthCodeData;
import com.vk.id.auth.VKIDAuthCallback;
import com.vk.id.auth.VKIDAuthParams;
import com.vk.id.refresh.VKIDRefreshTokenCallback;
import com.vk.id.refresh.VKIDRefreshTokenFail;
import com.vk.id.refresh.VKIDRefreshTokenParams;

import javax.crypto.SecretKey;

public class App extends Application {
    private static final String TAG = "App";

    private VKID.Companion vkid;

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
}
