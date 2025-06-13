package com.veritas.veritas.Fragments.MainFragments;

import static com.veritas.veritas.Application.App.getVKID;
import static com.veritas.veritas.Util.PublicVariables.getAuthCallback;
import static com.veritas.veritas.Util.PublicVariables.getAuthParams;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.veritas.veritas.Adapters.entity.User;
import com.veritas.veritas.DB.UsersDB;
import com.veritas.veritas.Fragments.Dialogs.UserAddDialog;
import com.veritas.veritas.Fragments.Preferences.ContextMenuPreference;
import com.veritas.veritas.R;
import com.vk.id.AccessToken;
import com.vk.id.VKIDAuthFail;
import com.vk.id.auth.AuthCodeData;
import com.vk.id.auth.VKIDAuthCallback;
import com.vk.id.auth.VKIDAuthParams;
import com.vk.id.onetap.xml.OneTapBottomSheet;

import com.vk.id.refreshuser.VKIDGetUserParams;

import java.util.List;

// TODO: Remove manual VK ID authorize and call authorize on first need

public class SettingsPrefFragment extends PreferenceFragmentCompat
        implements UserAddDialog.UserAddDialogListener, ContextMenuPreference.ContextMenuPreferenceListener {

    private static final String TAG = "SettingsPrefFragment";

    private static final String KEY_ADD_PLAYER = "add_player";
    private static final String KEY_PLAYERS_CATEGORY = "players_category";
    private static final String KEY_VK_ID_LOGIN_PREF = "vk_id_login";

    private UsersDB usersDB;
    private PreferenceCategory playersCat;

//    private Preference vkIdLoginPref;

    @Override
    public void onCreatePreferences(Bundle bundle, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

//        vkIdLoginPref = findPreference(KEY_VK_ID_LOGIN_PREF);
//
//        if (vkIdLoginPref != null) {
//            vkIdLoginPref.setOnPreferenceClickListener(preference -> {
//
//                getVKID().getInstance().authorize(getViewLifecycleOwner(), getAuthCallback(TAG, requireContext()), getAuthParams());
//
//                return true;
//            });
//        }

        playersCat = findPreference(KEY_PLAYERS_CATEGORY);

        usersDB = new UsersDB(requireContext());

        updatePlayersCat();

        Preference addUserPref = findPreference(KEY_ADD_PLAYER);
        addUserPref.setOnPreferenceClickListener(pref -> {
            UserAddDialog dialog = new UserAddDialog();
            dialog.setListener(this);
            dialog.show(getParentFragmentManager(), "myDialog");
            return true;
        });
    }

    private void updatePlayersCat() {
        // i >= 1 because 0 preference is always add_player preference and it should not be removed
        int count = playersCat.getPreferenceCount();
        for (int i = count - 1; i >= 1; i--) {
            playersCat.removePreference(playersCat.getPreference(i));
        }

        List<User> users = usersDB.selectAllFromPlayers();

        for (User user : users) {
            ContextMenuPreference userPref = new ContextMenuPreference(requireContext(), user, this);
            userPref.setKey(String.format("user=%s;sex=%s", user.getName(), user.getSex()));
            userPref.setTitle(user.getName());
            userPref.setSummary(user.getSex());
            playersCat.addPreference(userPref);
        }
    }

    @Override
    public void onUserAdded(String name, int sex) {
        usersDB.insertIntoPlayers(name, sex);
        updatePlayersCat();
    }

    @Override
    public void isDeleted(boolean status) {
        if (status) {
            updatePlayersCat();
        }
    }

    /*
    * I did not think a lot on that solution so I do not know is it a suitable solving but I hope so...
    * ...
    * ... unfortunately as always
    */

    @Override
    public void onDetach() {
        super.onDetach();
        usersDB.close();
    }
}

