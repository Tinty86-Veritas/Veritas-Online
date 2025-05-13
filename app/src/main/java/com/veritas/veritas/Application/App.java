package com.veritas.veritas.Application;

import android.app.Application;

import java.util.Locale;

import com.vk.id.VKID;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        VKID.Companion vkid = VKID.Companion;
        vkid.init(this);
        vkid.getInstance().setLocale(new Locale("ru"));
        vkid.setLogsEnabled(true);
    }

    public static VKID.Companion getVKID() {
        return VKID.Companion;
    }
}
