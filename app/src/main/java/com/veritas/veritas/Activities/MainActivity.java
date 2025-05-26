package com.veritas.veritas.Activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.veritas.veritas.Fragments.SpecialFragments.ModeFragment;
import com.veritas.veritas.Util.FragmentWorking;
import com.veritas.veritas.R;

public class MainActivity extends AppCompatActivity
        implements FragmentWorking.ModeFragmentCallback {

    private static final String TAG = "MainActivity";

    private BottomNavigationView nav;

    private ModeFragment modeFragment = null;

    private FragmentWorking fw;

    private int currentFragId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nav = findViewById(R.id.bottom_navigation);

        fw = new FragmentWorking(getApplicationContext(), TAG, getSupportFragmentManager(), this);

        currentFragId = fw.setFragment(R.id.mode_selection_fragment);

        nav.setSelectedItemId(R.id.mode_selection_fragment);

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.fire_id && currentFragId != R.id.fire_id) {
                if (modeFragment == null) {
                    currentFragId = fw.setFragment(R.id.mode_selection_fragment);
                } else {
                    fw.reviveSavedFragment(modeFragment);
                }
                return true;
            } else if (id == R.id.group_id && currentFragId != R.id.group_id) {
                currentFragId = fw.setFragment(R.id.group_fragment);
                return true;
            } else if (id == R.id.settings_id && currentFragId != R.id.settings_id) {
                currentFragId = fw.setFragment(R.xml.settings);
                return true;
            } else {
                return false;
            }
        });
    }

    @Override
    public void getModeFragment(ModeFragment modeFragment) {
        Log.d(TAG, "getModeFragment");
        this.modeFragment = modeFragment;
    }
}