package com.veritas.veritas.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.veritas.veritas.Fragments.Dialog.UserAddDialog;
import com.veritas.veritas.R;

import java.util.ArrayList;

public class SettingsFragment extends Fragment {

//    ArrayList<String> users_list = new ArrayList<>();

    ListView users_list_view;
    Button user_add_button;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);

        users_list_view = view.findViewById(R.id.users_list);
        user_add_button = view.findViewById(R.id.user_add_button);

        user_add_button.setOnClickListener(view1 -> {
            FragmentManager manager = getParentFragmentManager();
            UserAddDialog myDialogFragment = new UserAddDialog();
            myDialogFragment.show(manager, "myDialog");
        });

        return view;
    }
}
