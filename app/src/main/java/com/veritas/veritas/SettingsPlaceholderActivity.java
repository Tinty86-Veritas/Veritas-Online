package com.veritas.veritas;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsPlaceholderActivity extends AppCompatActivity {

    private static final String TAG = "SettingsPlaceholderActivity";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.settings_placeholder_activity);
        FragmentWorking fw = new FragmentWorking(getApplicationContext(), TAG, getSupportFragmentManager());

        int extra = getIntent().getIntExtra("settingsId", 0);

        if (extra == 0) {
            finish();
        } else {
            fw.setFragment(extra);
        }
    }
}
