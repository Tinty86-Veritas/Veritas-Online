package com.veritas.veritas.Util;

import static com.veritas.veritas.Fragments.SpecialFragments.LobbyFragment.CURRENT_GROUP_KEY;
import static com.veritas.veritas.Fragments.SpecialFragments.LobbyFragment.GROUP_ID_KEY;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

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

    public void setFragment(Fragment fragment) {
        if (fragment instanceof ModeFragment || fragment instanceof LobbyFragment){
            String error = "setFragment(fragment) received fragment instance of which is ModeFragment or LobbyFragment, so may be you've meant to use setFragment(fragment, context)?";
            Log.e(TAG, error);
            throw new RuntimeException(error);
        }
        transaction(fragment);
    }

    /**
     * Special overloaded func for special fragments setting handle
     * @param fragment fragment can be only instance of ModeFragment or LobbyFragment
     */
    public void setFragment(Fragment fragment, Context context) {
        if (callback == null) {
            Log.w(TAG, "You use setFragment(fragment, context) so are you sure you do not want to use callback?");
        }

        if (fragment instanceof ModeFragment){
            if (callback != null) {
                modeFragment = (ModeFragment) fragment;
                callback.getFragment(modeFragment);
            }
        } else if (fragment instanceof LobbyFragment) {
            if (callback != null) {
                lobbyFragment = (LobbyFragment) fragment;

                // Fetching cached group
                SharedPreferences sharedPreferences = context.getSharedPreferences(CURRENT_GROUP_KEY, Context.MODE_PRIVATE);
                String groupId = sharedPreferences.getString(GROUP_ID_KEY, null);
                if (groupId != null) {
                    Bundle args = fragment.getArguments();
                    if (args == null) args = new Bundle();
                    args.putString(GROUP_ID_KEY, groupId);
                    fragment.setArguments(args);
                }

                callback.getFragment(lobbyFragment);
            }
        } else {
            String error = "setFragment(fragment, context) received fragment instance of which is not ModeFragment or LobbyFragment, so may be you've meant to use setFragment(fragment)?";
            Log.e(TAG, error);
            throw new RuntimeException(error);
        }

        transaction(fragment);
    }


    public void reviveSavedFragment(Fragment fragment) {
//        if (fragment instanceof ModeFragment) {
//            ((ModeFragment) fragment).setIsRevived(true);
//        } else if (fragment instanceof LobbyFragment) {
//            ((LobbyFragment) fragment).setIsRevived(true);
//        }

        Bundle args = fragment.getArguments();
        if (args == null) args = new Bundle();
        args.putBoolean("REVIVED_MODE", true);
        fragment.setArguments(args);

        transaction(fragment);
    }

    private void transaction(Fragment fragment) {
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.place_holder, fragment);
        ft.commit();
    }
}