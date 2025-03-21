package com.veritas.veritas.Fragments.Dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.veritas.veritas.R;

public class UserAddDialog extends DialogFragment {

    public interface UserAddDialogListener {
        void onUserAdded(String name, long sex);
    }

    private UserAddDialogListener listener;

    // Устанавливаем слушатель в методе setListener
    public void setListener(UserAddDialogListener listener) {
        this.listener = listener;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.add_user_dialog, null);

        builder.setView(view)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        int selected_sex;

                        String name = String.valueOf(((EditText) view.findViewById(R.id.edit_text_name)).getText());

                        int selected_sex_id = ((RadioGroup) view.findViewById(R.id.sex_radio_group)).getCheckedRadioButtonId();

                        if (selected_sex_id == R.id.radio_button_male) {
                            selected_sex = 1;
                        } else {
                            selected_sex = 2;
                        }

                        Log.i("UserAddDialog", "name: " + name + "\nsex: " + selected_sex);
                        // Передаем данные через интерфейс
                        if (listener != null) {
                            listener.onUserAdded(name, selected_sex);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        UserAddDialog.this.getDialog().cancel();
                    }
                });
        return builder.show();
    }
}
