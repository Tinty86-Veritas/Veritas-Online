package com.veritas.veritas.Fragments.SettingsFragments;

import static com.veritas.veritas.Util.PublicVariables.getGames;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.veritas.veritas.Adapters.RecyclerAdapter;
import com.veritas.veritas.DB.GamesDB;
import com.veritas.veritas.R;

import java.util.ArrayList;
import java.util.Arrays;

public class SettingsNumOfAnswersFragment extends Fragment {

    public static final String TAG = "SettingsNumOfAnswersFragment";

    private GamesDB gamesDB;

    private RecyclerView gamesRecyclerView;

    private final RecyclerAdapter adapter = new RecyclerAdapter(
            new ArrayList<>(Arrays.asList(getGames())));

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.num_of_answers_fragment, container, false);

        gamesDB = new GamesDB(requireContext());

        gamesRecyclerView = view.findViewById(R.id.num_of_answers_recycler_view);

        gamesRecyclerView.setAdapter(adapter);

        return view;
    }
}
