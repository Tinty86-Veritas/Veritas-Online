package com.veritas.veritas.Fragments.SettingsFragments;

import android.content.Context;
import android.util.AttributeSet;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.veritas.veritas.Fragments.Dialog.NumOfAnswersBottomSheetDialog;

// TODO: Реализован bottom sheet dialog, но не реализованы взаимодействия с ним и сохраниение результатов взаимодействий

public class BottomSheetDialogPreference extends Preference
        implements NumOfAnswersBottomSheetDialog.NumOfAnswersBottomSheetDialogListener {

    FragmentManager fm;

    public BottomSheetDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        fm = ((FragmentActivity) context).getSupportFragmentManager();
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        holder.itemView.setOnClickListener(v -> {
            NumOfAnswersBottomSheetDialog bottomSheetDialog = new NumOfAnswersBottomSheetDialog();
            bottomSheetDialog.show(fm, "num_of_answers_sheet");
        });
    }

    @Override
    public void getNumOfAnswers(String mode, int num) {

    }
}
