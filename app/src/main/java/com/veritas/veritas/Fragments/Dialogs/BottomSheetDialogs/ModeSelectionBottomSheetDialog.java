package com.veritas.veritas.Fragments.Dialogs.BottomSheetDialogs;

import static com.veritas.veritas.Util.PublicVariables.MODE_EXTREME;
import static com.veritas.veritas.Util.PublicVariables.MODE_FUN;
import static com.veritas.veritas.Util.PublicVariables.MODE_HOT;
import static com.veritas.veritas.Util.PublicVariables.MODE_MADNESS;
import static com.veritas.veritas.Util.PublicVariables.MODE_SOFT;
import static com.veritas.veritas.Util.PublicVariables.getModes;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.veritas.veritas.Activities.MainActivity;
import com.veritas.veritas.Fragments.SpecialFragments.ModeFragment;
import com.veritas.veritas.R;
import com.veritas.veritas.Util.FragmentWorking;

import java.util.ArrayList;

public class ModeSelectionBottomSheetDialog extends BottomSheetDialogFragment {
    private static final String TAG = "ModeSelectionBottomSheetDialog";

    private FragmentActivity activity;

    private String gameName;
//    private ArrayList<String> items;

    private LinearLayout linearLayout;

    public ModeSelectionBottomSheetDialog(String gameName) {
        this.gameName = gameName;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.standard_bottom_sheet_dialog_fragment, container, false);
        View view = inflater.inflate(R.layout.mode_selection_bottom_sheet_dialog, container, false);

        init(view);

        return view;
    }

    private void init(View view) {
        activity = requireActivity();

//        items = new ArrayList<>(List.of(
//                MODE_FUN, MODE_SOFT, MODE_HOT, MODE_EXTREME + " (16+)", MODE_MADNESS + " (18+)"
//        ));

        linearLayout = view.findViewById(R.id.modes_layout);

        addMaterialCardViews();

//        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
//
//        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
//
//        RecyclerAdapter adapter = new RecyclerAdapter(items);
//
//        adapter.setOnClickListener((RecyclerAdapter.RecyclerAdapterOnItemClickListener)
//                (view1, position) -> {
//            final FragmentWorking fw;
//
//            /*
//            "It is never a bad idea to make code as safe as you can" - someone (probably me :D)
//            So the followed piece of code is for safety ->
//            -> even considering that my app is using (at least specifically at the moment when I am writing this (11:38 pm...))
//            */
//
//            if (activity instanceof MainActivity mainActivity) {
//                fw = new FragmentWorking(
//                        TAG, getParentFragmentManager(), mainActivity);
//            } else {
//                throw new RuntimeException("MainActivity is not current Activity");
//            }
//
//            ModeFragment modeFragment = new ModeFragment(gameName, items.get(position));
//
//            fw.setFragment(modeFragment, requireContext());
//            dismiss();
//        });
//
//        recyclerView.setAdapter(adapter);
    }

    private void addMaterialCardViews() {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        String[] modes = getModes();
        for (String modeName : modes) {
            MaterialCardView cardView = (MaterialCardView) inflater.inflate(R.layout.standard_button, linearLayout, false);

            // This call should be before switch case with modes renaming
//            setModesColors(cardView, modeName);

            switch (modeName) {
                case MODE_EXTREME -> modeName += " (16+)";
                case MODE_MADNESS -> modeName += " (18+)";
            }

            MaterialTextView itemTextView = cardView.findViewById(R.id.item);
            if (itemTextView != null) {
                itemTextView.setText(modeName);
                itemTextView.setGravity(Gravity.CENTER);
            }

            final String finalModeName = modeName;

            cardView.setOnClickListener(v -> {
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

                ModeFragment modeFragment = new ModeFragment(gameName, finalModeName);

                fw.setFragment(modeFragment, requireContext());
                dismiss();
            });

            linearLayout.addView(cardView);
        }
    }

    private void setModesColors(MaterialCardView cardView, String modeName) {
        Context context = requireContext();
        int strokeColor;

        switch (modeName) {
            case MODE_FUN -> strokeColor = getStrokeColorFromStyle(R.style.FunModeStyle);
            case MODE_SOFT -> strokeColor = getStrokeColorFromStyle(R.style.SoftModeStyle);
            case MODE_HOT -> strokeColor = getStrokeColorFromStyle(R.style.HotModeStyle);
            case MODE_EXTREME -> strokeColor = getStrokeColorFromStyle(R.style.ExtremeModeStyle);
            case MODE_MADNESS -> strokeColor = getStrokeColorFromStyle(R.style.MadnessModeStyle);
            default ->  {
                TypedValue typedValue = new TypedValue();
                context.getTheme().resolveAttribute(com.google.android.material.R.attr.colorOutline, typedValue, true);
                strokeColor = typedValue.data;
            }
        }

        cardView.setStrokeColor(strokeColor);
        int strokeWidth = (int) (2 * requireContext().getResources().getDisplayMetrics().density);
        cardView.setStrokeWidth(strokeWidth);
    }

    /**
     * Helper-метод для получения strokeColor из заданного стиля,
     * используя MaterialCardView's declare-styleable.
     *
     * @param styleResId Идентификатор ресурса стиля.
     * @return Значение цвета strokeColor или 0, если не найдено.
     */
    private int getStrokeColorFromStyle(@StyleRes int styleResId) {
        Context context = requireContext();
        TypedArray typedArray = null;
        int strokeColor = 0;

        try {
            typedArray = context.obtainStyledAttributes(styleResId, com.google.android.material.R.styleable.MaterialCardView);

            strokeColor = typedArray.getColor(com.google.android.material.R.styleable.MaterialCardView_strokeColor, 0);

        } catch (Exception e) {
            Log.e(TAG, "Ошибка при получении strokeColor из стиля " + getResources().getResourceEntryName(styleResId) + ": ", e);
        } finally {
            if (typedArray != null) {
                typedArray.recycle();
            }
        }
        return strokeColor;
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
