package com.veritas.veritas.Fragments;

import static com.veritas.veritas.Util.FragmentWorking.MODE_EXTREME_DARE;
import static com.veritas.veritas.Util.FragmentWorking.MODE_EXTREME_NEVEREVER;
import static com.veritas.veritas.Util.FragmentWorking.MODE_FUN;
import static com.veritas.veritas.Util.FragmentWorking.MODE_FUN_DARE;
import static com.veritas.veritas.Util.FragmentWorking.MODE_FUN_NEVEREVER;
import static com.veritas.veritas.Util.FragmentWorking.MODE_HOT;
import static com.veritas.veritas.Util.FragmentWorking.MODE_HOT_DARE;
import static com.veritas.veritas.Util.FragmentWorking.MODE_HOT_NEVEREVER;
import static com.veritas.veritas.Util.FragmentWorking.MODE_MADNESS;
import static com.veritas.veritas.Util.FragmentWorking.MODE_MADNESS_DARE;
import static com.veritas.veritas.Util.FragmentWorking.MODE_MADNESS_NEVEREVER;
import static com.veritas.veritas.Util.FragmentWorking.MODE_SOFT;
import static com.veritas.veritas.Util.FragmentWorking.MODE_SOFT_DARE;
import static com.veritas.veritas.Util.FragmentWorking.MODE_EXTREME;
import static com.veritas.veritas.Util.FragmentWorking.MODE_SOFT_NEVEREVER;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.veritas.veritas.Util.FragmentWorking;
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

        modes_names = new ArrayList<>(List.of("Fun", "Soft", "Hot", "Extreme", "Madness",
                        "Fun Dare", "Soft Dare", "Hot Dare", "Extreme Dare", "Madness Dare",
                "Fun NeverEver", "Soft NeverEver", "Hot NeverEver", "Extreme NeverEver", "Madness NeverEver"));

        FragmentWorking fw = new FragmentWorking(getContext(), TAG, getParentFragmentManager());

        modes_list_view = view.findViewById(R.id.modes_list);

        adapter = new ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, modes_names);

        modes_list_view.setAdapter(adapter);

        modes_list_view.setOnItemClickListener((adapterView, view1, i, l) -> {
            String selectedMode = modes_names.get(i);
            switch (selectedMode) {
                case MODE_FUN:
                    fw.setFragment(MODE_FUN);
                    break;
                case MODE_SOFT:
                    fw.setFragment(MODE_SOFT);
                    break;
                case MODE_HOT:
                    fw.setFragment(MODE_HOT);
                    break;
                case MODE_EXTREME:
                    fw.setFragment(MODE_EXTREME);
                    break;
                case MODE_MADNESS:
                    fw.setFragment(MODE_MADNESS);
                    break;
                case MODE_FUN_DARE:
                    fw.setFragment(MODE_FUN_DARE);
                    break;
                case MODE_SOFT_DARE:
                    fw.setFragment(MODE_SOFT_DARE);
                    break;
                case MODE_HOT_DARE:
                    fw.setFragment(MODE_HOT_DARE);
                    break;
                case MODE_EXTREME_DARE:
                    fw.setFragment(MODE_EXTREME_DARE);
                    break;
                case MODE_MADNESS_DARE:
                    fw.setFragment(MODE_MADNESS_DARE);
                    break;
                case MODE_FUN_NEVEREVER:
                    fw.setFragment(MODE_FUN_NEVEREVER);
                    break;
                case MODE_SOFT_NEVEREVER:
                    fw.setFragment(MODE_SOFT_NEVEREVER);
                    break;
                case MODE_HOT_NEVEREVER:
                    fw.setFragment(MODE_HOT_NEVEREVER);
                    break;
                case MODE_EXTREME_NEVEREVER:
                    fw.setFragment(MODE_EXTREME_NEVEREVER);
                    break;
                case MODE_MADNESS_NEVEREVER:
                    fw.setFragment(MODE_MADNESS_NEVEREVER);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + selectedMode);
            }
        });


        return view;
    }

}
