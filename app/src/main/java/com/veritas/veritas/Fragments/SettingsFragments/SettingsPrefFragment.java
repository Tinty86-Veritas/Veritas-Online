package com.veritas.veritas.Fragments.SettingsFragments;

import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;

import com.veritas.veritas.Adapters.entity.User;
import com.veritas.veritas.DB.UsersDB;
import com.veritas.veritas.Fragments.Dialog.UserAddDialog;
import com.veritas.veritas.Fragments.SettingsFragments.Preferences.ContextMenuPreference;
import com.veritas.veritas.R;

import java.util.List;

public class SettingsPrefFragment extends PreferenceFragmentCompat
        implements UserAddDialog.UserAddDialogListener, ContextMenuPreference.ContextMenuPreferenceListener {
    private static final String KEY_ADD_PLAYER = "add_player";
    private static final String KEY_PLAYERS_CATEGORY = "players_category";
    private static final String KEY_VK_ID_LOGIN_PREF = "vk_id_login";

    private UsersDB usersDB;
    private PreferenceCategory playersCat;

    private Preference vkIdLoginPref;
    @Override
    public void onCreatePreferences(Bundle bundle, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

        vkIdLoginPref = findPreference(KEY_VK_ID_LOGIN_PREF);

        if (vkIdLoginPref != null) {
            vkIdLoginPref.setOnPreferenceClickListener(preference -> {
                // Здесь будет ваш код для запуска VK ID One Tap Auth
                // Согласно документации VK ID, это может быть вызов метода authorize()
                // или показ специального UI элемента (если SDK его предоставляет)

                // Пример (вам нужно будет адаптировать его под ваш код и документацию VK ID):
                // VKID.Companion.getInstance().authorize(getActivity(), yourAuthCallback, yourAuthParams);

                Toast.makeText(getContext(), "Нажата кнопка VK ID Login", Toast.LENGTH_SHORT).show(); // Для примера

                return true;
            });
        }

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
}

