package com.veritas.veritas.Fragments.Dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.veritas.veritas.Adapters.NumOfAnswersRecyclerAdapter;
import com.veritas.veritas.R;

public class NumOfAnswersBottomSheetDialog extends BottomSheetDialogFragment {

    // The keys are identical to the game names so they can be used as game names as well
    String key;

    public NumOfAnswersBottomSheetDialog(String key) {
        this.key = key;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_dialog_fragment, container, false);

        RecyclerView numOfAnswersRecyclerView = view.findViewById(R.id.modes_num_of_answers_rv);

        NumOfAnswersRecyclerAdapter num_of_answers_adapter =
                new NumOfAnswersRecyclerAdapter(requireContext(), key);

        numOfAnswersRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        numOfAnswersRecyclerView.setAdapter(num_of_answers_adapter);

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

}
