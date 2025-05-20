package com.veritas.veritas.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.veritas.veritas.Util.FragmentWorking;
import com.veritas.veritas.R;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    private BottomNavigationView nav;

    private int current_frag_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nav = findViewById(R.id.bottom_navigation);

        FragmentWorking fw = new FragmentWorking(getApplicationContext(), TAG, getSupportFragmentManager());

        current_frag_id = fw.setFragment(R.id.mode_selection_fragment);

        nav.setSelectedItemId(R.id.mode_selection_fragment);

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.fire_id && current_frag_id != R.id.fire_id) {
                current_frag_id = fw.setFragment(R.id.mode_selection_fragment);
                return true;
            } else if (id == R.id.settings_id && current_frag_id != R.id.settings_id) {
                current_frag_id = fw.setFragment(R.id.settings_fragment);
                return true;
            } else {
                return false;
            }
        });
    }
}