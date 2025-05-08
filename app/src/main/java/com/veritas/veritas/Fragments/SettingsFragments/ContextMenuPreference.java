package com.veritas.veritas.Fragments.SettingsFragments;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.PopupMenu;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.veritas.veritas.Adapters.entity.User;
import com.veritas.veritas.DB.UsersDB;
import com.veritas.veritas.R;

public class ContextMenuPreference extends Preference {

    public interface ContextMenuPreferenceListener {
        void isDeleted(boolean status);
    }


    private UsersDB usersDB;
    private User user;

    private ContextMenuPreferenceListener listener;

    public ContextMenuPreference(Context context, User user, ContextMenuPreferenceListener listener) {
        super(context);
        this.user = user;
        usersDB = new UsersDB(context);
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        holder.itemView.setOnLongClickListener(v -> {
            showPopupMenu(v);
            return true;
        });
    }

    private void showPopupMenu(View anchor) {
        PopupMenu popup = new PopupMenu(getContext(), anchor);
        popup.inflate(R.menu.pref_context_menu);
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_delete) {
                usersDB.deleteFromPlayers(user.getName(), user.getSex());
                usersDB.close();
                listener.isDeleted(true);
                return true;
            } else {
                return false;
            }
        });
        popup.show();
    }
}
