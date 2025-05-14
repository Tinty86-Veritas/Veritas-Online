package com.veritas.veritas.Fragments.Dialog;

import android.os.Bundle;
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
        View view = inflater.inflate(R.layout.reactions_bottom_sheet_dialog_fragment, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        ArrayList<String> items = new ArrayList<>(List.of(
                "Нравится вариант", "Не нравится вариант", "Вариант повторяется"
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
        Toast.makeText(requireContext(), "IT WORKS!!!", Toast.LENGTH_SHORT).show();

        // TODO: Дать возможность удалять реакцию, например повторным кликом по той же реакции

        GamesDB gamesDB = new GamesDB(requireContext());

        if (gamesDB.hasReaction(gameName, modeName, content)) {
            gamesDB.deleteReaction(gameName, modeName, content);
        }

        switch (position) {
            case 0 -> gamesDB.addReaction(gameName, modeName, "like", content);
            case 1 -> gamesDB.addReaction(gameName, modeName, "dislike", content);
            case 2 -> gamesDB.addReaction(gameName, modeName, "recurring", content);
        }
    }
}
