package com.veritas.veritas.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.veritas.veritas.DB.Firebase.entity.Question;
import com.veritas.veritas.R;

import java.util.ArrayList;

public class LobbyRecyclerAdapter extends RecyclerView.Adapter<LobbyRecyclerAdapter.ViewHolder> {
    private static final String TAG = "LobbyRecyclerAdapter";

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView text;

        public ViewHolder(@NonNull View view) {
            super(view);
            text = view.findViewById(R.id.item);
        }
    }

    private final ArrayList<Question> questions;

    public LobbyRecyclerAdapter(ArrayList<Question> questions) {
        this.questions = questions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Question item = questions.get(position);
        holder.text.setText(item.getText());
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }
}
