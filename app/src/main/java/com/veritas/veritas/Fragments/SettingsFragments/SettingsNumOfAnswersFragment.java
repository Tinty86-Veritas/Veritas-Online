package com.veritas.veritas.Fragments.SettingsFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.veritas.veritas.DB.GamesDB;
import com.veritas.veritas.R;

public class SettingsNumOfAnswersFragment extends Fragment {

    public static final String TAG = "SettingsNumOfAnswersFragment";

    private GamesDB gamesDB;

    private RecyclerView numOfAnswersRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_bd_fragment, container, false);

        gamesDB = new GamesDB(requireContext());

        numOfAnswersRecyclerView = new RecyclerView(requireContext());

        return view;
    }
}
