package com.veritas.veritas.Util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.veritas.veritas.Fragments.MainFragments.GameSelectionFragment;
import com.veritas.veritas.Fragments.MainFragments.GroupFragment;
import com.veritas.veritas.Fragments.SpecialFragments.LobbyFragment;
import com.veritas.veritas.Fragments.SpecialFragments.ModeFragment;
import com.veritas.veritas.Fragments.MainFragments.SettingsPrefFragment;
import com.veritas.veritas.R;

public class FragmentWorking {

    public interface FragmentCallback {
        void getFragment(Fragment fragment);
    }

    private final String TAG;
    private final Context context;

    private FragmentManager fm;
    private FragmentCallback callback;

    private ModeFragment modeFragment;
    private LobbyFragment lobbyFragment;

    public FragmentWorking(Context context, String TAG, FragmentManager fm) {
        this.TAG = TAG;
        this.context = context;
        this.fm = fm;
    }

    public FragmentWorking(Context context, String TAG, FragmentManager fm, FragmentCallback callback) {
        this.TAG = TAG;
        this.context = context;
        this.fm = fm;
        this.callback = callback;
    }

    public int setFragment(int fragLayout) {
        Fragment fragment;
        if (fragLayout == R.layout.mode_selection_fragment) {
            fragment = new GameSelectionFragment();
        } else if (fragLayout == R.xml.settings){
            fragment = new SettingsPrefFragment();
        } else if (fragLayout == R.layout.group_fragment) {
            fragment = new GroupFragment();
        } else if (fragLayout == R.layout.lobby_fragment) {
            fragment = new LobbyFragment();
            if (callback != null) {
                lobbyFragment = (LobbyFragment) fragment;
                callback.getFragment(lobbyFragment);
            }
        } else {
            Log.wtf(TAG, "Method setFragment got inappropriate fragment id");
            Toast.makeText(context, "Method setFragment got inappropriate fragment id", Toast.LENGTH_SHORT).show();
            return 0;
        }

        transaction(fragment);

        return fragLayout;
    }

    public void setFragment(String gameName, String modeName) {
        ModeFragment fragment = new ModeFragment(modeName, gameName);
        if (callback != null) {
            modeFragment = fragment;
            callback.getFragment(modeFragment);
        }

        transaction(fragment);
    }

    public void reviveSavedFragment(Fragment fragment) {
        transaction(fragment);
        if (fragment instanceof ModeFragment) {
            ((ModeFragment) fragment).setIsRevived(true);
        } else if (fragment instanceof LobbyFragment) {
            ((LobbyFragment) fragment).setIsRevived(true);
        }
    }

    private void transaction(Fragment fragment) {
        FragmentTransaction ft = fm.beginTransaction();

        ft.replace(R.id.place_holder, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }
}