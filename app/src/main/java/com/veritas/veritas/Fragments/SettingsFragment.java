package com.veritas.veritas.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;

import com.veritas.veritas.DB.UsersTable;
import com.veritas.veritas.Fragments.Dialog.UserAddDialog;
import com.veritas.veritas.R;

public class SettingsFragment extends Fragment implements UserAddDialog.UserAddDialogListener {
    private ActivityResultLauncher<Intent> dialogLauncher;

//    ArrayList<String> users_list = new ArrayList<>();

    private ListView users_list_view;
    private Button user_add_button;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);

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
    public void onUserAdded(String name, String sex) {
        Toast.makeText(getContext(), "User added: " + name + ", " + sex, Toast.LENGTH_SHORT).show();
        UsersTable usersTable = new UsersTable(requireContext());

    }
}
