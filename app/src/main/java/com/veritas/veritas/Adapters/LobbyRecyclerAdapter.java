package com.veritas.veritas.Adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.veritas.veritas.DB.Firebase.entity.Question;
import com.veritas.veritas.R;

import java.util.ArrayList;

public class LobbyRecyclerAdapter extends RecyclerView.Adapter<LobbyRecyclerAdapter.ViewHolder> {
    private static final String TAG = "LobbyRecyclerAdapter";

    private boolean isInitMessage = false;
    private Context context;

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

    public LobbyRecyclerAdapter(Context context, ArrayList<Question> questions, boolean isInitMessage) {
        if (context != null) {
            this.context = context;
        }

        this.questions = questions;
        this.isInitMessage = isInitMessage;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.standard_button, parent, false);

        /* Init message configuration:
        *       1) Setting stroke color to initMessageStroke (from res/values/colors.xml)
        *       2) Setting stroke width from 1dp (default standard_button value) to 3dp
        */

        if (isInitMessage) {
            MaterialCardView cardView = view.findViewById(R.id.card_view);
            ColorStateList strokeColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.initMessageStroke));
            cardView.setStrokeColor(strokeColor);
            int strokeWidth = (int) (3 * context.getResources().getDisplayMetrics().density);
            cardView.setStrokeWidth(strokeWidth);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Question item = questions.get(position);
        holder.text.setText(item.getContent());
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    @Override
    public int getItemViewType(int position) {
        // Определяем тип элемента для правильного отображения init message
        if (position < questions.size()) {
            Question question = questions.get(position);
            if (question != null && "init".equals(question.getType())) {
                return 1; // Init message type
            }
        }
        return 0; // Regular message type
    }
}
