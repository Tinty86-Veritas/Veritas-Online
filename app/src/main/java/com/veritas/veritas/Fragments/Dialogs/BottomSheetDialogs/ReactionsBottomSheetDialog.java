package com.veritas.veritas.Fragments.Dialogs.BottomSheetDialogs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.veritas.veritas.Adapters.RecyclerAdapter;
import com.veritas.veritas.DB.GamesDB;
import com.veritas.veritas.R;

import java.util.ArrayList;
import java.util.List;

public class ReactionsBottomSheetDialog extends BottomSheetDialogFragment
        implements RecyclerAdapter.RecyclerAdapterOnItemClickListener {

    private static final String TAG = "ReactionsBottomSheetDialog";

    private String gameName;
    private String modeName;
    private String content;

    public ReactionsBottomSheetDialog(String gameName, String modeName, String content) {
        this.gameName = gameName;
        this.modeName = modeName;
        this.content = content;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.standard_bottom_sheet_dialog_fragment, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        ArrayList<String> items = new ArrayList<>(List.of(
                getString(R.string.like_option), getString(R.string.dislike_option), getString(R.string.recurring_option)
        ));

        RecyclerAdapter adapter = new RecyclerAdapter(items);

        adapter.setOnClickListener(this);

        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        if (dialog != null) {
            FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<?> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        GamesDB gamesDB = new GamesDB(requireContext());

        switch (position) {
            case 0 -> {
                final String type = "like";
                if (!deleteHandle(gamesDB, type)) {
                    addHandle(gamesDB, type);
                }
            }
            case 1 -> {
                final String type = "dislike";
                if (!deleteHandle(gamesDB, type)) {
                    addHandle(gamesDB, type);
                }
            }
            case 2 -> {
                final String type = "recurring";
                if (!deleteHandle(gamesDB, type)) {
                    addHandle(gamesDB, type);
                }
            }
        }
        gamesDB.close();
    }

    private void clearPreviousType(GamesDB gamesDB) {
        if (gamesDB.hasReaction(gameName, modeName, content)) {
            gamesDB.deleteReaction(gameName, modeName, content);
        }
    }

    private boolean deleteHandle(GamesDB gamesDB, String currentType) {
        final String type = gamesDB.getReactionType(gameName, modeName, content);
        if (type != null) {
            if (type.equals(currentType)) {
                gamesDB.deleteReaction(gameName, modeName, content);
                Toast.makeText(requireContext(), R.string.reaction_deleted, Toast.LENGTH_SHORT).show();
                return true;
            } else {
                Log.wtf(TAG, type);
            }
        }
        return false;
    }

    private void addHandle(GamesDB gamesDB, String type) {
        clearPreviousType(gamesDB);
        gamesDB.addReaction(gameName, modeName, type, content);
        Toast.makeText(requireContext(), R.string.reaction_saved, Toast.LENGTH_SHORT).show();
        dismiss();
    }
}
