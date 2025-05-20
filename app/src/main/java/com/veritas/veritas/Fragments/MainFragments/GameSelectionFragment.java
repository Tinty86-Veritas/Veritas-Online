package com.veritas.veritas.Fragments.MainFragments;

import static com.veritas.veritas.Util.PublicVariables.getGames;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.veritas.veritas.Adapters.RecyclerAdapter;
import com.veritas.veritas.Fragments.Dialogs.BottomSheetDialogs.ModeSelectionBottomSheetDialog;
import com.veritas.veritas.R;

import java.util.ArrayList;
import java.util.List;

public class GameSelectionFragment extends Fragment
        implements RecyclerAdapter.RecyclerAdapterOnItemClickListener {

    private static final String TAG = "GameSelectionFragment";

    private ArrayList<String> gamesNames;

    private RecyclerAdapter adapter;
    private RecyclerView modesRV;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mode_selection_fragment, container, false);

        gamesNames = new ArrayList<>(List.of(
                getString(R.string.truth), getString(R.string.dare), getString(R.string.neverEver)
        ));

        modesRV = view.findViewById(R.id.modes_rv);

        adapter = new RecyclerAdapter(gamesNames, true);

        adapter.setOnClickListener(this);

        modesRV.setAdapter(adapter);

        return view;
    }

    @Override
    public void onItemClick(View view, int position) {
        // Translating user view resource to app appropriate resource
        String gameName = getGames()[position];

        ModeSelectionBottomSheetDialog bottomSheetDialog =
                new ModeSelectionBottomSheetDialog(gameName);
        bottomSheetDialog.show(getParentFragmentManager(), TAG);
    }
}
