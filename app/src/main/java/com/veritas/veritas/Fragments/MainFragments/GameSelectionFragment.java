package com.veritas.veritas.Fragments.MainFragments;

import static com.veritas.veritas.Util.PublicVariables.getGames;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.veritas.veritas.Adapters.RecyclerAdapter;
import com.veritas.veritas.Fragments.Dialogs.BottomSheetDialogs.ModeSelectionBottomSheetDialog;
import com.veritas.veritas.R;

import java.util.ArrayList;
import java.util.List;

public class GameSelectionFragment extends Fragment
        implements RecyclerAdapter.RecyclerAdapterOnItemClickListener {

    private static final String TAG = "GameSelectionFragment";

    private LinearLayout linearLayout;

    private ArrayList<String> gamesNames;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mode_selection_fragment, container, false);

        linearLayout = view.findViewById(R.id.games_layout);

        gamesNames = new ArrayList<>(List.of(
                getString(R.string.truth), getString(R.string.dare), getString(R.string.neverEver)
        ));

        addMaterialCardViews();

        return view;
    }

    private void addMaterialCardViews() {
        LayoutInflater inflater = LayoutInflater.from(requireContext());

        for (String gameName : gamesNames) {
            // Надуваем макет MaterialCardView
            MaterialCardView cardView = (MaterialCardView) inflater.inflate(R.layout.game_button, linearLayout, false);

            ColorStateList strokeColor;
            if (gameName.equals(getString(R.string.truth))) {
                strokeColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.truth));
            } else if (gameName.equals(getString(R.string.dare))) {
                strokeColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.dare));
            } else {
                strokeColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.neverEver));
            }

            cardView.setStrokeColor(strokeColor);
            int strokeWidth = (int) (3 * requireContext().getResources().getDisplayMetrics().density);
            cardView.setStrokeWidth(strokeWidth);

            MaterialTextView itemTextView = cardView.findViewById(R.id.item);
            if (itemTextView != null) {
                itemTextView.setText(gameName);
            }

            final String appGameName = convertGameName(gameName);

            cardView.setOnClickListener(v -> {
                ModeSelectionBottomSheetDialog bottomSheetDialog =
                        new ModeSelectionBottomSheetDialog(appGameName);
                bottomSheetDialog.show(getParentFragmentManager(), TAG);
            });

            linearLayout.addView(cardView);
        }
    }

    private String convertGameName(String gameName) {
        final String appGameName;

        if (gameName.equals(getString(R.string.truth))) {
            appGameName = getGames()[0];
        } else if (gameName.equals(getString(R.string.dare))) {
            appGameName = getGames()[1];
        } else if (gameName.equals(getString(R.string.neverEver))) {
            appGameName = getGames()[2];
        } else {
            appGameName = "";
            Toast.makeText(requireContext(), "gameName error", Toast.LENGTH_SHORT).show();
        }
        return appGameName;
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
