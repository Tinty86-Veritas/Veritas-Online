package com.veritas.veritas.Fragments.ModesFragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.veritas.veritas.R;

import java.util.ArrayList;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tv;
        public ViewHolder(View view) {
            super(view);
            tv = view.findViewById(R.id.question);
        }
    }

    private final ArrayList<String> questions;

    public RecyclerAdapter(ArrayList<String> questions) {
        this.questions = questions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, int position) {
        String question = questions.get(position);
        holder.tv.setText(question);
    }

    @Override
    public int getItemCount() {
        return questions.toArray().length;
    }
}
