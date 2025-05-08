package com.veritas.veritas.Util;

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
import com.veritas.veritas.Fragments.SettingsFragments.SettingsBDFragment;
import com.veritas.veritas.Fragments.SettingsFragments.SettingsNumOfAnswersFragment;
import com.veritas.veritas.Fragments.SettingsFragments.SettingsPrefFragment;
import com.veritas.veritas.R;

public class FragmentWorking {

    private final String TAG;
    private final Context context;

    private FragmentManager fm;
  
    public static final String MODE_FUN = "Fun";
    public static final String MODE_SOFT = "Soft";
    public static final String MODE_HOT = "Hot";
    public static final String MODE_EXTREME = "Extreme";
    public static final String MODE_MADNESS = "Madness";
    public static final String MODE_FUN_DARE = "Fun Dare";
    public static final String MODE_SOFT_DARE = "Soft Dare";
    public static final String MODE_HOT_DARE = "Hot Dare";
    public static final String MODE_EXTREME_DARE = "Extreme Dare";
    public static final String MODE_MADNESS_DARE = "Madness Dare";
    public static final String MODE_FUN_NEVEREVER = "Fun NeverEver";
    public static final String MODE_SOFT_NEVEREVER = "Soft NeverEver";
    public static final String MODE_HOT_NEVEREVER = "Hot NeverEver";
    public static final String MODE_EXTREME_NEVEREVER = "Extreme NeverEver";
    public static final String MODE_MADNESS_NEVEREVER = "Madness NeverEver";

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
//            fragment = new SettingsFragment();
            fragment = new SettingsPrefFragment();
        } else if (frag_id == R.id.settings_bd_fragment){
            fragment = new SettingsBDFragment();
        } else if (frag_id == R.id.num_of_answers_fragment){
            fragment = new SettingsNumOfAnswersFragment();
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

    public void setFragment(String mode_name) {
        Fragment fragment;
        switch (mode_name) {
            case MODE_FUN:
                fragment = new ModeFragment(MODE_FUN);
                break;
            case MODE_SOFT:
                fragment = new ModeFragment(MODE_SOFT);
                break;
            case MODE_HOT:
                fragment = new ModeFragment(MODE_HOT);
                break;
            case MODE_EXTREME:
                fragment = new ModeFragment(MODE_EXTREME);
                break;
            case MODE_MADNESS:
                fragment = new ModeFragment(MODE_MADNESS);
                break;
            case MODE_FUN_DARE:
                fragment = new ModeFragment(MODE_FUN_DARE, "Dare");
                break;
            case MODE_SOFT_DARE:
                fragment = new ModeFragment(MODE_SOFT_DARE, "Dare");
                break;
            case MODE_HOT_DARE:
                fragment = new ModeFragment(MODE_HOT_DARE, "Dare");
                break;
            case MODE_EXTREME_DARE:
                fragment = new ModeFragment(MODE_EXTREME_DARE, "Dare");
                break;
            case MODE_MADNESS_DARE:
                fragment = new ModeFragment(MODE_MADNESS_DARE, "Dare");
                break;
            case MODE_FUN_NEVEREVER:
                fragment = new ModeFragment(MODE_FUN_NEVEREVER, "NeverEver");
                break;
            case MODE_SOFT_NEVEREVER:
                fragment = new ModeFragment(MODE_SOFT_NEVEREVER, "NeverEver");
                break;
            case MODE_HOT_NEVEREVER:
                fragment = new ModeFragment(MODE_HOT_NEVEREVER, "NeverEver");
                break;
            case MODE_EXTREME_NEVEREVER:
                fragment = new ModeFragment(MODE_EXTREME_NEVEREVER, "NeverEver");
                break;
            case MODE_MADNESS_NEVEREVER:
                fragment = new ModeFragment(MODE_MADNESS_NEVEREVER, "NeverEver");
                break;
            default:
                Log.wtf(TAG, "Method setFragment got inappropriate fragment name");
                Toast.makeText(context, "Method setFragment got inappropriate fragment name", Toast.LENGTH_SHORT).show();
                return;
        }

        FragmentTransaction ft = fm.beginTransaction();

        ft.replace(R.id.place_holder, fragment);
        ft.addToBackStack(null);
        ft.commit();

    }
}
