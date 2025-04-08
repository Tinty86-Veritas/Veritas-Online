package com.veritas.veritas.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.veritas.veritas.R;
import com.veritas.veritas.SettingsPlaceholderActivity;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {

    private static final String TAG = "SettingsFragment";

    private ArrayAdapter adapter;

    private ListView settings_lv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);

        ArrayList<String> settings_list = new ArrayList<>(List.of(
                getString(R.string.players)));

        settings_lv = view.findViewById(R.id.settings_lv);

        settings_lv.setOnItemClickListener((adapterView, view1, i, l) -> {
            Intent intent = new Intent(getActivity(), SettingsPlaceholderActivity.class);
            switch (i) {
                case 0: intent.putExtra("settingsId", R.id.settings_bd_fragment); break;
            }
            startActivity(intent);
        });

        adapter = new ArrayAdapter(requireContext(),
                android.R.layout.simple_list_item_1, settings_list);

        settings_lv.setAdapter(adapter);

        return view;
    }
}
