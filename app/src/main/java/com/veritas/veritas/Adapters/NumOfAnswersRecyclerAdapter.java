package com.veritas.veritas.Adapters;

import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.RecyclerView;

import com.veritas.veritas.R;

public class NumOfAnswersRecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tv;
        final AppCompatSpinner spinner;
        public ViewHolder(View view) {
            super(view);
            tv = view.findViewById(R.id.question);
            spinner = view.findViewById(R.id.num_of_answers_spinner);
        }
    }
}
