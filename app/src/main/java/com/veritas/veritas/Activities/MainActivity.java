package com.veritas.veritas.Activities;

import static com.veritas.veritas.Fragments.SpecialFragments.LobbyFragment.CURRENT_GROUP_KEY;
import static com.veritas.veritas.Fragments.SpecialFragments.LobbyFragment.GROUP_ID_KEY;
import static com.veritas.veritas.Fragments.SpecialFragments.LobbyFragment.IS_HOST_KEY;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.veritas.veritas.DB.Firebase.Util.FirebaseManager;
import com.veritas.veritas.Fragments.MainFragments.GameSelectionFragment;
import com.veritas.veritas.Fragments.MainFragments.GroupFragment;
import com.veritas.veritas.Fragments.MainFragments.SettingsPrefFragment;
import com.veritas.veritas.Fragments.SpecialFragments.LobbyFragment;
import com.veritas.veritas.Fragments.SpecialFragments.ModeFragment;
import com.veritas.veritas.Util.FragmentWorking;
import com.veritas.veritas.R;
import com.veritas.veritas.Util.TokenStorage;

public class MainActivity extends AppCompatActivity
        implements FragmentWorking.FragmentCallback {

    private static final String TAG = "MainActivity";

    private BottomNavigationView nav;

    private GameSelectionFragment gameSelectionFragment;
    private GroupFragment groupFragment;
    private SettingsPrefFragment settingsPrefFragment;

    private ModeFragment modeFragment = null;
    private LobbyFragment lobbyFragment = null;

    private FirebaseManager firebaseManager = null;

    private FragmentWorking fw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        navConfigure();
    }

    private void init() {
        nav = findViewById(R.id.bottom_navigation);

        fw = new FragmentWorking(TAG, getSupportFragmentManager(), this);

        gameSelectionFragment = new GameSelectionFragment();
        groupFragment = new GroupFragment();
        settingsPrefFragment = new SettingsPrefFragment();

        fw.setFragment(gameSelectionFragment);
    }

    private void navConfigure() {
        nav.setSelectedItemId(R.id.mode_selection_fragment);

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.fire_id) {
                if (modeFragment == null) {
                    fw.setFragment(gameSelectionFragment);
                } else {
                    fw.reviveSavedFragment(modeFragment);
                }
                return true;

            } else if (id == R.id.group_id) {
                /* TODO:
                    If sharedGroupId is not null lobbyFragment should appear instead of group fragment.
                    Also app should store isHost to prevent any inadequate app behaviour
                  */
                if (lobbyFragment == null) {
                    SharedPreferences sharedPreferences = getSharedPreferences(CURRENT_GROUP_KEY, Context.MODE_PRIVATE);
                    String groupId;
                    if ((groupId = sharedPreferences.getString(GROUP_ID_KEY, null)) != null) {
                        fw.setFragment(
                                new LobbyFragment(
                                        sharedPreferences.getBoolean(IS_HOST_KEY, false),
                                        groupId),
                                getApplicationContext()
                        );
                    } else {
                        fw.setFragment(groupFragment);
                    }
                } else {
                    fw.reviveSavedFragment(lobbyFragment);
                }
                return true;

            } else if (id == R.id.settings_id) {
                fw.setFragment(settingsPrefFragment);
                return true;
            } else {
                return false;
            }
        });
    }

    // TODO: If returns false may be app should automatically call OneTap auth menu
    public boolean canCreateLobby() {
        // Проверяем accessToken перед созданием лобби
        TokenStorage tokenStorage = new TokenStorage(getApplicationContext());

        return tokenStorage.getAccessToken(this) != null;
    }

    @Override
    public void setSpecialFragment(Fragment fragment) {
        if (fragment instanceof ModeFragment) {
            this.modeFragment = (ModeFragment) fragment;
        } else if (fragment instanceof LobbyFragment) {
            this.lobbyFragment = (LobbyFragment) fragment;
        }
    }

    public void setModeFragment(ModeFragment modeFragment) {
        this.modeFragment = modeFragment;
    }

    public void setLobbyFragment(LobbyFragment lobbyFragment) {
        this.lobbyFragment = lobbyFragment;
    }

    public LobbyFragment getLobbyFragment() {
        return lobbyFragment;
    }

    public GameSelectionFragment getGameSelectionFragment() {
        return gameSelectionFragment;
    }

    public GroupFragment getGroupFragment() {
        return groupFragment;
    }

    public FirebaseManager getFirebaseManager() {
        return firebaseManager;
    }

    public void setFirebaseManager(FirebaseManager firebaseManager) {
        this.firebaseManager = firebaseManager;
    }
}