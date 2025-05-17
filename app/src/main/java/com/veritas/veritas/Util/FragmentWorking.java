package com.veritas.veritas.Util;

import static com.veritas.veritas.Util.PublicVariables.DARE;
import static com.veritas.veritas.Util.PublicVariables.NEVEREVER;
import static com.veritas.veritas.Util.PublicVariables.TRUTH;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.veritas.veritas.Fragments.MainFragments.GroupFragment;
import com.veritas.veritas.Fragments.MainFragments.ModeSelectionFragment;
import com.veritas.veritas.Fragments.SpecialFragments.LobbyFragment;
import com.veritas.veritas.Fragments.SpecialFragments.ModeFragment;
import com.veritas.veritas.Fragments.MainFragments.SettingsPrefFragment;
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

    public int setFragment(int frag_layout) {
        Fragment fragment;
        if (frag_layout == R.layout.mode_selection_fragment) {
            fragment = new ModeSelectionFragment();
        } else if (frag_layout == R.xml.settings){
            fragment = new SettingsPrefFragment();
        } else if (frag_layout == R.layout.group_fragment) {
            fragment = new GroupFragment();
        } else if (frag_layout == R.layout.lobby_fragment) {
            fragment = new LobbyFragment();
        } else {
            Log.wtf(TAG, "Method setFragment got inappropriate fragment id");
            Toast.makeText(context, "Method setFragment got inappropriate fragment id", Toast.LENGTH_SHORT).show();
            return 0;
        }

        FragmentTransaction ft = fm.beginTransaction();

        ft.replace(R.id.place_holder, fragment);
        ft.addToBackStack(null);
        ft.commit();

        return frag_layout;
    }

    public void setFragment(String mode_name) {
        Fragment fragment;
        switch (mode_name) {
            case MODE_FUN:
                fragment = new ModeFragment(MODE_FUN, TRUTH);
                break;
            case MODE_SOFT:
                fragment = new ModeFragment(MODE_SOFT, TRUTH);
                break;
            case MODE_HOT:
                fragment = new ModeFragment(MODE_HOT, TRUTH);
                break;
            case MODE_EXTREME:
                fragment = new ModeFragment(MODE_EXTREME, TRUTH);
                break;
            case MODE_MADNESS:
                fragment = new ModeFragment(MODE_MADNESS, TRUTH);
                break;
            case MODE_FUN_DARE:
                fragment = new ModeFragment(MODE_FUN, DARE);
                break;
            case MODE_SOFT_DARE:
                fragment = new ModeFragment(MODE_SOFT, DARE);
                break;
            case MODE_HOT_DARE:
                fragment = new ModeFragment(MODE_HOT, DARE);
                break;
            case MODE_EXTREME_DARE:
                fragment = new ModeFragment(MODE_EXTREME, DARE);
                break;
            case MODE_MADNESS_DARE:
                fragment = new ModeFragment(MODE_MADNESS, DARE);
                break;
            case MODE_FUN_NEVEREVER:
                fragment = new ModeFragment(MODE_FUN, NEVEREVER);
                break;
            case MODE_SOFT_NEVEREVER:
                fragment = new ModeFragment(MODE_SOFT, NEVEREVER);
                break;
            case MODE_HOT_NEVEREVER:
                fragment = new ModeFragment(MODE_HOT, NEVEREVER);
                break;
            case MODE_EXTREME_NEVEREVER:
                fragment = new ModeFragment(MODE_EXTREME, NEVEREVER);
                break;
            case MODE_MADNESS_NEVEREVER:
                fragment = new ModeFragment(MODE_MADNESS, NEVEREVER);
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
