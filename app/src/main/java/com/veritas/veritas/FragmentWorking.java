package com.veritas.veritas;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.veritas.veritas.Fragments.AiFragment;
import com.veritas.veritas.Fragments.ModeSelectionFragment;
import com.veritas.veritas.Fragments.ModesFragments.ModeFragment;
import com.veritas.veritas.Fragments.SettingsFragment;

public class FragmentWorking {

    private final String TAG;
    private final Context context;

    private FragmentManager fm;

    public static final int fun_mode_id = 0;
    public static final int soft_mode_id = 1;
    public static final int hot_mode_id = 2;
    public static final int extreme_mode_id = 3;

    public static final int dare_fun_mode_id = 4;
    public static final int dare_soft_mode_id = 5;
    public static final int dare_hot_mode_id = 6;
    public static final int dare_extreme_mode_id = 7;

    public static final int neverEver_fun_mode_id = 8;
    public static final int neverEver_soft_mode_id = 9;
    public static final int neverEver_hot_mode_id = 10;
    public static final int neverEver_extreme_mode_id = 11;

    public FragmentWorking(Context context, String TAG, FragmentManager fm) {
        this.TAG = TAG;
        this.context = context;
        this.fm = fm;
    }

    public int setFragment(int frag_id) {
        Fragment fragment;
        if (frag_id == R.id.mode_selection_fragment) {
            fragment = new ModeSelectionFragment();
        } else if (frag_id == R.id.ai_direct_using_fragment) {
            fragment = new AiFragment();
        } else if (frag_id == R.id.settings_fragment){
            fragment = new SettingsFragment();
        } else if (frag_id == fun_mode_id) {
            fragment = new ModeFragment("Fun");
        } else if (frag_id == soft_mode_id) {
            fragment = new ModeFragment("Soft");
        } else if (frag_id == hot_mode_id) {
            fragment = new ModeFragment("Hot");
        } else if (frag_id == extreme_mode_id) {
            fragment = new ModeFragment("Extreme");
        } else if (frag_id == dare_fun_mode_id) {
            fragment = new ModeFragment("Fun Dare", "Dare");
        } else if (frag_id == dare_soft_mode_id) {
            fragment = new ModeFragment("Soft Dare", "Dare");
        } else if (frag_id == dare_hot_mode_id) {
            fragment = new ModeFragment("Hot Dare", "Dare");
        } else if (frag_id == dare_extreme_mode_id) {
            fragment = new ModeFragment("Extreme Dare", "Dare");
        } else if (frag_id == neverEver_fun_mode_id) {
            fragment = new ModeFragment("Fun NeverEver", "NeverEver");
        } else if (frag_id == neverEver_soft_mode_id) {
            fragment = new ModeFragment("Soft NeverEver", "NeverEver");
        } else if (frag_id == neverEver_hot_mode_id) {
            fragment = new ModeFragment("Hot NeverEver", "NeverEver");
        } else if (frag_id == neverEver_extreme_mode_id) {
            fragment = new ModeFragment("Extreme NeverEver", "NeverEver");
        } else {
            Log.wtf(TAG, "Method setFragment got inappropriate fragment id");
            Toast.makeText(context, "Method setFragment got inappropriate fragment id", Toast.LENGTH_SHORT).show();
            return 0;
        }

        FragmentTransaction ft = fm.beginTransaction();

        ft.replace(R.id.place_holder, fragment);
        ft.addToBackStack(null);
        ft.commit();

        return frag_id;
    }
}
