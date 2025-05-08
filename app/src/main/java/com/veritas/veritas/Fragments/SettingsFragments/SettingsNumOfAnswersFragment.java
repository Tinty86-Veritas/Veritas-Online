package com.veritas.veritas.Fragments.SettingsFragments;

import static com.veritas.veritas.Util.PublicVariables.getGames;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.veritas.veritas.Adapters.NumOfAnswersRecyclerAdapter;
import com.veritas.veritas.Adapters.RecyclerAdapter;
import com.veritas.veritas.DB.GamesDB;
import com.veritas.veritas.R;

import java.util.ArrayList;
import java.util.Arrays;

public class SettingsNumOfAnswersFragment extends Fragment {

    public static final String TAG = "SettingsNumOfAnswersFragment";

    private GamesDB gamesDB;

    private RecyclerView gamesRecyclerView;

    private final RecyclerAdapter games_adapter = new RecyclerAdapter(
            new ArrayList<>(Arrays.asList(getGames())));

    private BottomSheetBehavior<LinearLayout> sheetBehavior;
    private RecyclerView NumOfAnswersRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.num_of_answers_fragment, container, false);

        gamesDB = new GamesDB(requireContext());

        gamesRecyclerView = view.findViewById(R.id.num_of_answers_rv);

        gamesRecyclerView.setAdapter(games_adapter);

        LinearLayout bottomSheet = view.findViewById(R.id.num_of_answers_bs);

        sheetBehavior = BottomSheetBehavior.from(bottomSheet);

        NumOfAnswersRecyclerView = view.findViewById(R.id.modes_num_of_answers_rv);

        NumOfAnswersRecyclerAdapter num_of_answers_adapter = new NumOfAnswersRecyclerAdapter(requireContext());

        NumOfAnswersRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        NumOfAnswersRecyclerView.setAdapter(num_of_answers_adapter);

        games_adapter.setOnClickListener((v, position) -> {
            Toast.makeText(requireContext(), "IT WORKS", Toast.LENGTH_SHORT).show();
            if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        return view;
    }
}
