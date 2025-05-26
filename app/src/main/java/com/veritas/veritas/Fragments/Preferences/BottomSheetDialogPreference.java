package com.veritas.veritas.Fragments.Preferences;

import android.content.Context;
import android.util.AttributeSet;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.veritas.veritas.Fragments.Dialogs.BottomSheetDialogs.NumOfAnswersBottomSheetDialog;

public class BottomSheetDialogPreference extends Preference {

    private static final String TAG = "BottomSheetDialogPreference";

    FragmentManager fm;

    public BottomSheetDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        fm = ((FragmentActivity) context).getSupportFragmentManager();
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        holder.itemView.setOnClickListener(v -> {
            NumOfAnswersBottomSheetDialog bottomSheetDialog = new NumOfAnswersBottomSheetDialog(getKey());
            bottomSheetDialog.show(fm, TAG);
        });
    }
}
