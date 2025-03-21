package com.veritas.veritas.Fragments;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.veritas.veritas.Adapters.UsersListAdapter;
import com.veritas.veritas.Adapters.entity.User;
import com.veritas.veritas.DB.PlayersTable;
import com.veritas.veritas.DB.SexesTable;
import com.veritas.veritas.DB.UsersDB;
import com.veritas.veritas.Fragments.Dialog.UserAddDialog;
import com.veritas.veritas.R;

import java.util.ArrayList;

public class SettingsFragment extends Fragment implements UserAddDialog.UserAddDialogListener {

    ArrayList<String> users_list = new ArrayList<>();

    private UsersDB usersDB;
    private PlayersTable playersTable;

    private ListView users_list_view;
    private Button user_add_button;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.settings_fragment, container, false);

        usersDB = new UsersDB(requireContext());
        playersTable = new PlayersTable(requireContext());

        users_list_view = view.findViewById(R.id.users_list);
        user_add_button = view.findViewById(R.id.user_add_button);

        user_add_button.setOnClickListener(view1 -> {
            UserAddDialog dialog = new UserAddDialog();
            dialog.setListener(this);
            dialog.show(getParentFragmentManager(), "myDialog");
        });

        return view;
    }

    @Override
    public void onUserAdded(String name, long sex) {
        Toast.makeText(getContext(), "User added: " + name + ", " + sex, Toast.LENGTH_SHORT).show();

        SQLiteDatabase db = usersDB.getWritableDatabase();

        playersTable.insert(name, sex);

        UsersListAdapter adapter = new UsersListAdapter(requireContext(), playersTable.selectAll());

        users_list_view.setAdapter(adapter);
    }
}
