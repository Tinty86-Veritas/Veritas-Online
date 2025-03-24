package com.veritas.veritas.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.veritas.veritas.Adapters.entity.User;

import java.util.ArrayList;

public class UsersListAdapter extends ArrayAdapter<User> {

    public UsersListAdapter(@NonNull Context context, ArrayList<User> arr) {
        super(context, android.R.layout.simple_list_item_2, arr);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final User user = getItem(position);

        if (user == null) {
            Log.wtf("UsersListAdapter", "UsersListAdapter.getView got null as user");
            return convertView;
        }

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, null);
        }

        ((TextView) convertView.findViewById(android.R.id.text1)).setText(user.getName());
        ((TextView) convertView.findViewById(android.R.id.text2)).setText(user.getSex());

        return convertView;
    }
}

