package com.veritas.veritas.Fragments.Dialogs.BottomSheetDialogs;

import static com.veritas.veritas.Util.PublicVariables.MODE_EXTREME;
import static com.veritas.veritas.Util.PublicVariables.MODE_FUN;
import static com.veritas.veritas.Util.PublicVariables.MODE_HOT;
import static com.veritas.veritas.Util.PublicVariables.MODE_MADNESS;
import static com.veritas.veritas.Util.PublicVariables.MODE_SOFT;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.veritas.veritas.Activities.MainActivity;
import com.veritas.veritas.Adapters.RecyclerAdapter;
import com.veritas.veritas.Fragments.SpecialFragments.ModeFragment;
import com.veritas.veritas.R;
import com.veritas.veritas.Util.FragmentWorking;

import java.util.ArrayList;
import java.util.List;

public class ModeSelectionBottomSheetDialog extends BottomSheetDialogFragment
        implements RecyclerAdapter.RecyclerAdapterOnItemClickListener {
    private static final String TAG = "ModeSelectionBottomSheetDialog";

    private FragmentActivity activity;

    private String gameName;
    private ArrayList<String> items;

    public ModeSelectionBottomSheetDialog(String gameName) {
        this.gameName = gameName;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.standard_bottom_sheet_dialog_fragment, container, false);

        init(view);

        return view;
    }

    private void init(View view) {
        activity = requireActivity();

        items = new ArrayList<>(List.of(
                MODE_FUN, MODE_SOFT, MODE_HOT, MODE_EXTREME + " (16+)", MODE_MADNESS + " (18+)"
        ));

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        RecyclerAdapter adapter = new RecyclerAdapter(items);

        adapter.setOnClickListener(this);

        recyclerView.setAdapter(adapter);
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

        final FragmentWorking fw;

        /*
        "It is never a bad idea to make code as safe as you can" - someone (probably me :D)
        So the followed piece of code is for safety ->
        -> even considering that my app is using (at least specifically at the moment when I am writing this (11:38 pm...))
        */

        if (activity instanceof MainActivity mainActivity) {
            fw = new FragmentWorking(
                    TAG, getParentFragmentManager(), mainActivity);
        } else {
            throw new RuntimeException("MainActivity is not current Activity");
        }

        ModeFragment modeFragment = new ModeFragment(gameName, items.get(position));

        fw.setFragment(modeFragment, requireContext());
        dismiss();
    }
}
