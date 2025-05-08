package com.veritas.veritas.Adapters;

import static com.veritas.veritas.Util.PublicVariables.getModes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.RecyclerView;

import com.veritas.veritas.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NumOfAnswersRecyclerAdapter extends RecyclerView.Adapter<NumOfAnswersRecyclerAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tv;
        final AppCompatSpinner spinner;
        public ViewHolder(View view) {
            super(view);
            tv = view.findViewById(R.id.mode);
            spinner = view.findViewById(R.id.num_of_answers_spinner);
        }
    }


    private final Context context;

    private final List<String> modes = new ArrayList<>(Arrays.asList(getModes()));

    public NumOfAnswersRecyclerAdapter(Context context) {
        this.context = context;
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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                context, android.R.layout.simple_spinner_item, modes);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        holder.spinner.setAdapter(adapter);


        String game = modes.get(position);
        holder.tv.setText(game);
    }

    @Override
    public int getItemCount() {
        return modes.toArray().length;
    }
}
