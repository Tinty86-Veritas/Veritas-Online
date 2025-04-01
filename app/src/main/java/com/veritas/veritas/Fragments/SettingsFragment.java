package com.veritas.veritas.Fragments;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.veritas.veritas.Adapters.UsersListAdapter;
import com.veritas.veritas.Adapters.entity.User;
import com.veritas.veritas.DB.UsersDB;
import com.veritas.veritas.Fragments.Dialog.UserAddDialog;
import com.veritas.veritas.R;


public class SettingsFragment extends Fragment implements UserAddDialog.UserAddDialogListener {

    ArrayAdapter<User> adapter;

    private UsersDB usersDB;

    private ListView users_list_view;
    private Button user_add_button;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.settings_fragment, container, false);

        usersDB = new UsersDB(requireContext());

        users_list_view = view.findViewById(R.id.users_list);
        user_add_button = view.findViewById(R.id.user_add_button);

        if (adapter == null) {
            adapter = new UsersListAdapter(requireContext(), usersDB.selectAllFromPlayers());
        } else {
            adapter.addAll(usersDB.selectAllFromPlayers());
        }

        users_list_view.setAdapter(adapter);

        registerForContextMenu(users_list_view);

        user_add_button.setOnClickListener(view1 -> {
            UserAddDialog dialog = new UserAddDialog();
            dialog.setListener(this);
            dialog.show(getParentFragmentManager(), "myDialog");
        });

        return view;
    }

    private void updateAdapter() {
        adapter.clear();

        adapter.addAll(usersDB.selectAllFromPlayers());

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onUserAdded(String name, int sex) {
        usersDB.insertIntoPlayers(name, sex);

        updateAdapter();
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 0, getString(R.string.delete));
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        CharSequence title = item.getTitle();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (title.equals(getString(R.string.delete))) {
            User user = usersDB.selectAllFromPlayers().get(info.position);
            usersDB.deleteFromPlayers(user.getName());

            updateAdapter();

            Toast.makeText(requireContext(), "Deleted", Toast.LENGTH_SHORT).show();
        }

        return super.onContextItemSelected(item);
    }
}
