package com.veritas.veritas.Util;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.veritas.veritas.Fragments.SpecialFragments.LobbyFragment;
import com.veritas.veritas.Fragments.SpecialFragments.ModeFragment;
import com.veritas.veritas.R;

public class FragmentWorking {

    public interface FragmentCallback {
        void getFragment(Fragment fragment);
    }

    private final String TAG;

    private FragmentManager fm;
    private FragmentCallback callback;

    private ModeFragment modeFragment;
    private LobbyFragment lobbyFragment;

    public FragmentWorking(String TAG, FragmentManager fm) {
        this.TAG = TAG;
        this.fm = fm;
    }

    public FragmentWorking(String TAG, FragmentManager fm, FragmentCallback callback) {
        this.TAG = TAG;
        this.fm = fm;
        this.callback = callback;
    }

    public Fragment setFragment(Fragment fragment) {
        if (fragment instanceof ModeFragment){
            if (callback != null) {
                modeFragment = (ModeFragment) fragment;
                callback.getFragment(modeFragment);
            }
        } else if (fragment instanceof LobbyFragment) {
            if (callback != null) {
                lobbyFragment = (LobbyFragment) fragment;
                callback.getFragment(lobbyFragment);
            }
        }

        transaction(fragment);

        return fragment;
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
        ft.commit();
    }
}