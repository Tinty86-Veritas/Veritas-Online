package com.veritas.veritas.Adapters;

import static com.veritas.veritas.Util.PublicVariables.getModes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.veritas.veritas.DB.GamesDB;
import com.veritas.veritas.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NumOfAnswersRecyclerAdapter extends RecyclerView.Adapter<NumOfAnswersRecyclerAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final MaterialTextView tv;
        final AutoCompleteTextView autoCompleteTV;
        public ViewHolder(View view) {
            super(view);
            tv = view.findViewById(R.id.mode);
            autoCompleteTV = view.findViewById(R.id.num_of_answers_auto_complete_tv);
        }
    }

    private final Context context;

    private final List<String> modes = new ArrayList<>(Arrays.asList(getModes()));

    private String game_name;

    public NumOfAnswersRecyclerAdapter(Context context, String game_name) {
        this.context = context;
        this.game_name = game_name;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_num_of_answers_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        GamesDB gamesDB = new GamesDB(context);

        String mode = modes.get(position);
        holder.tv.setText(mode);

        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(
                context,  android.R.layout.simple_dropdown_item_1line, new Integer[] {
                1, 3, 5, 7, 10, 15, 20, 25, 50
        });

        holder.autoCompleteTV.setAdapter(adapter);

        int numOfAnswers = gamesDB.getRequestNum(game_name, mode);
        gamesDB.close();

        holder.autoCompleteTV.setText(String.valueOf(numOfAnswers), false);

        holder.autoCompleteTV.setOnItemClickListener(
                (parent, view, position1, id) -> {
            Integer selectedItem = (Integer) parent.getItemAtPosition(position1);

            GamesDB gamesDB1 = new GamesDB(context);
            gamesDB1.updateRequestNum(game_name, mode, selectedItem);
            gamesDB1.close();
        });
    }

    @Override
    public int getItemCount() {
        return modes.size();
    }
}
