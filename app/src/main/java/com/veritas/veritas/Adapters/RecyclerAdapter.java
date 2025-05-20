package com.veritas.veritas.Adapters;

import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.veritas.veritas.R;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    public interface RecyclerAdapterOnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface RecyclerAdapterOnLongItemClickListener {
        void onLongItemClick(View view, int position);
    }

    private RecyclerAdapterOnItemClickListener OnItemClickListener;
    private RecyclerAdapterOnLongItemClickListener OnLongItemClickListener;

    public void setOnClickListener(RecyclerAdapterOnItemClickListener listener) {
        OnItemClickListener = listener;
    }

    public void setOnClickListener(RecyclerAdapterOnLongItemClickListener listener) {
        OnLongItemClickListener = listener;
    }

    private final ArrayList<String> items;
    private boolean centering = true;
    private Typeface font = null;

    public RecyclerAdapter(ArrayList<String> items) {
        this.items = items;
    }

    public RecyclerAdapter(ArrayList<String> items, boolean centering) {
        this.items = items;
        this.centering = centering;
    }

    public RecyclerAdapter(ArrayList<String> items, boolean centering, Typeface font) {
        this.items = items;
        this.centering = centering;
        this.font = font;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final MaterialTextView tv;
        public ViewHolder(View view) {
            super(view);
            tv = view.findViewById(R.id.item);
            if (centering) {
                tv.setGravity(Gravity.CENTER);
            } if (font != null) {
                tv.setTypeface(font);
            }

            if (OnItemClickListener != null) {
                itemView.setOnClickListener(v -> {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        OnItemClickListener.onItemClick(v, pos);
                    }
                });
            }

            if (OnLongItemClickListener != null) {
                itemView.setOnLongClickListener(v -> {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        OnLongItemClickListener.onLongItemClick(v, pos);
                    }
                    return true;
                });
            }
        }
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
        String item = items.get(position);
        holder.tv.setText(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
