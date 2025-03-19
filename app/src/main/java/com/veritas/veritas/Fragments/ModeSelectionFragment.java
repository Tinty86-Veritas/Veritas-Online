package com.veritas.veritas.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.veritas.veritas.FragmentWorking;
import com.veritas.veritas.R;

import java.util.ArrayList;
import java.util.List;

public class ModeSelectionFragment extends Fragment {

    private static final String TAG = "ModeSelectionFragment";

    private ArrayList<String> modes_names = new ArrayList<>(List.of("Fun", "Soft"));

    private ArrayAdapter adapter;
    private ListView modes_list_view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mode_selection_fragment, container, false);

        FragmentWorking fw = new FragmentWorking(getContext(), TAG, getParentFragmentManager());

        modes_list_view = view.findViewById(R.id.modes_list);

        adapter = new ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, modes_names);

        modes_list_view.setAdapter(adapter);

        modes_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (modes_names.get(i)) {
                    case "Fun": fw.setFragment(R.id.fun_mode_fragment); break;
                    case "Soft": fw.setFragment(R.id.soft_mode_fragment); break;
                }
            }
        });

        return view;
    }

}
