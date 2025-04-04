package com.veritas.veritas.Fragments;

import static com.veritas.veritas.FragmentWorking.dare_extreme_mode_id;
import static com.veritas.veritas.FragmentWorking.dare_fun_mode_id;
import static com.veritas.veritas.FragmentWorking.dare_hot_mode_id;
import static com.veritas.veritas.FragmentWorking.dare_soft_mode_id;
import static com.veritas.veritas.FragmentWorking.extreme_mode_id;
import static com.veritas.veritas.FragmentWorking.fun_mode_id;
import static com.veritas.veritas.FragmentWorking.hot_mode_id;
import static com.veritas.veritas.FragmentWorking.neverEver_extreme_mode_id;
import static com.veritas.veritas.FragmentWorking.neverEver_fun_mode_id;
import static com.veritas.veritas.FragmentWorking.neverEver_hot_mode_id;
import static com.veritas.veritas.FragmentWorking.neverEver_soft_mode_id;
import static com.veritas.veritas.FragmentWorking.soft_mode_id;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.veritas.veritas.FragmentWorking;
import com.veritas.veritas.R;

import java.util.ArrayList;
import java.util.List;

public class ModeSelectionFragment extends Fragment {

    private static final String TAG = "ModeSelectionFragment";

    private ArrayList<String> modes_names;

    private ArrayAdapter adapter;
    private ListView modes_list_view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mode_selection_fragment, container, false);

        modes_names = new ArrayList<>(List.of("Fun", "Soft", "Hot", "Extreme",
                        "Fun Dare", "Soft Dare", "Hot Dare", "Extreme Dare",
                "NeverEver Fun", "NeverEver Soft", "NeverEver Hot", "NeverEver Extreme"));

        FragmentWorking fw = new FragmentWorking(getContext(), TAG, getParentFragmentManager());

        modes_list_view = view.findViewById(R.id.modes_list);

        adapter = new ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, modes_names);

        modes_list_view.setAdapter(adapter);

        modes_list_view.setOnItemClickListener((adapterView, view1, i, l) -> {
            switch (modes_names.get(i)) {
                case "Fun": fw.setFragment(fun_mode_id); break;
                case "Soft": fw.setFragment(soft_mode_id); break;
                case "Hot": fw.setFragment(hot_mode_id); break;
                case "Extreme": fw.setFragment(extreme_mode_id); break;
                case "Fun Dare": fw.setFragment(dare_fun_mode_id); break;
                case "Soft Dare": fw.setFragment(dare_soft_mode_id); break;
                case "Hot Dare": fw.setFragment(dare_hot_mode_id); break;
                case "Extreme Dare": fw.setFragment(dare_extreme_mode_id); break;
                case "NeverEver Fun": fw.setFragment(neverEver_fun_mode_id); break;
                case "NeverEver Soft": fw.setFragment(neverEver_soft_mode_id); break;
                case "NeverEver Hot": fw.setFragment(neverEver_hot_mode_id); break;
                case "NeverEver Extreme": fw.setFragment(neverEver_extreme_mode_id); break;
            }
        });

        return view;
    }

}
