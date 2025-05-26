package com.veritas.veritas.Fragments.Dialogs.BottomSheetDialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.veritas.veritas.R;

public class JoinViaCodeBottomSheetDialog extends BottomSheetDialogFragment {
    private MaterialButton trueJoinViaCodeBt;
    private TextInputEditText inputCodeEt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.join_via_code_bottom_sheet_dialog, container, false);

        trueJoinViaCodeBt = view.findViewById(R.id.true_join_via_code_bt);
        inputCodeEt = view.findViewById(R.id.input_code_et);

        trueJoinViaCodeBt.setOnClickListener(v -> {
            Toast.makeText(requireContext(), inputCodeEt.getText(), Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
