package com.veritas.veritas.Fragments.MainFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.veritas.veritas.Fragments.Dialog.JoinViaCodeBottomSheetDialog;
import com.veritas.veritas.R;

public class GroupFragment extends Fragment {
    private static final String TAG = "GroupFragment";

    private AppCompatButton joinViaCodeBt;
    private AppCompatButton createGroupBt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.group_fragment, container, false);

        joinViaCodeBt = view.findViewById(R.id.join_via_code_bt);
        createGroupBt = view.findViewById(R.id.create_group_bt);

        joinViaCodeBt.setOnClickListener(v -> {
            FragmentManager fm = getParentFragmentManager();

            JoinViaCodeBottomSheetDialog bottomSheetDialog = new JoinViaCodeBottomSheetDialog();
            bottomSheetDialog.show(fm, TAG);
        });

        return view;
    }
}
