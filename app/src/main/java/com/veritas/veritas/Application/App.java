package com.veritas.veritas.Application;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;
import java.util.Locale;

import com.vk.id.VKID;

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
