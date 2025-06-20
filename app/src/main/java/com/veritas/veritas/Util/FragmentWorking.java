package com.veritas.veritas.Util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.veritas.veritas.Fragments.MainFragments.GroupFragment;
import com.veritas.veritas.Fragments.MainFragments.GameSelectionFragment;
import com.veritas.veritas.Fragments.SpecialFragments.ModeFragment;
import com.veritas.veritas.Fragments.MainFragments.SettingsPrefFragment;
import com.veritas.veritas.R;

public class FragmentWorking {

    public interface ModeFragmentCallback {
        void getModeFragment(ModeFragment modeFragment);
    }

    private final String TAG;
    private final Context context;

    private FragmentManager fm;
    private ModeFragmentCallback callback;

    private ModeFragment modeFragment;

    public FragmentWorking(Context context, String TAG, FragmentManager fm) {
        this.TAG = TAG;
        this.context = context;
        this.fm = fm;
    }

    public FragmentWorking(Context context, String TAG, FragmentManager fm, ModeFragmentCallback callback) {
        this.TAG = TAG;
        this.context = context;
        this.fm = fm;
        this.callback = callback;
    }

    public int setFragment(int fragId) {
        Fragment fragment;
        if (fragId == R.id.mode_selection_fragment) {
            fragment = new GameSelectionFragment();
        } else if (fragId == R.xml.settings){
            fragment = new SettingsPrefFragment();
        } else if (fragId == R.id.group_fragment) {
            fragment = new GroupFragment();
        } else {
            Log.wtf(TAG, "Method setFragment got inappropriate fragment id");
            Toast.makeText(context, "Method setFragment got inappropriate fragment id", Toast.LENGTH_SHORT).show();
            return 0;
        }

        transaction(fragment);

        return fragId;
    }

    public void setFragment(String gameName, String modeName) {
        if (callback != null) {
            Log.d(TAG, "callback is not null");
            modeFragment = new ModeFragment(modeName, gameName);

            Fragment fragment = modeFragment;

            transaction(fragment);

            callback.getModeFragment(modeFragment);
        } else {
            Log.d(TAG, "callback is null");
            Fragment fragment = new ModeFragment(modeName, gameName);
            transaction(fragment);
        }
    }

    public void reviveSavedFragment(Fragment fragment) {
        transaction(fragment);
        //
        ((ModeFragment) fragment).setIsRevived(true);
    }

    private void transaction(Fragment fragment) {
        FragmentTransaction ft = fm.beginTransaction();

        ft.replace(R.id.place_holder, fragment);
        ft.addToBackStack(null);
        ft.commit();

    }
}
