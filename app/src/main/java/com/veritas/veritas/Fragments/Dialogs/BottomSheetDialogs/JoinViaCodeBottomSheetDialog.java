package com.veritas.veritas.Fragments.Dialogs.BottomSheetDialogs;

import static com.veritas.veritas.DB.Firebase.Util.FirebaseManager.GROUPS_KEY;
import static com.veritas.veritas.DB.Firebase.Util.FirebaseManager.PARTICIPANTS_KEY;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.veritas.veritas.Application.App;
import com.veritas.veritas.DB.Firebase.Util.FirebaseManager;
import com.veritas.veritas.DB.Firebase.entity.GroupParticipant;
import com.veritas.veritas.Fragments.SpecialFragments.LobbyFragment;
import com.veritas.veritas.R;
import com.veritas.veritas.Util.FragmentWorking;
import com.veritas.veritas.Util.TokenStorage;

public class JoinViaCodeBottomSheetDialog extends BottomSheetDialogFragment {
    private static final String TAG = "JoinViaCodeBottomSheetDialog";
    private FirebaseManager firebaseManager;
    private FragmentWorking fw;

    private MaterialButton trueJoinViaCodeBt;
    private TextInputEditText inputCodeEt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.join_via_code_bottom_sheet_dialog, container, false);

        init(view);

        return view;
    }

    private void init(View view) {
        firebaseManager = new FirebaseManager();
        fw = new FragmentWorking(TAG, getParentFragmentManager());

        trueJoinViaCodeBt = view.findViewById(R.id.true_join_via_code_bt);
        inputCodeEt = view.findViewById(R.id.input_code_et);

        trueJoinViaCodeBt.setOnClickListener(v -> {
            Editable textEd = inputCodeEt.getText();
            String text;
            if (textEd == null) {
                Toast.makeText(requireContext(), "Код пуст", Toast.LENGTH_SHORT).show();
                return;
            } else {
                text = textEd.toString();
            }
            firebaseManager.validateGroupCode(text,
                    new FirebaseManager.OnGroupCodeValidationListener() {
                @Override
                public void onValidCode(String groupId) {
                    addParticipant(groupId);
                    fw.setFragment(new LobbyFragment(false, groupId));
                    dismiss();
                }

                @Override
                public void onInvalidCode() {
                    Toast.makeText(requireContext(), "Код недействителен", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void addParticipant(String groupId) {
        TokenStorage tokenStorage = new TokenStorage(requireContext());
        long userId = tokenStorage.getUserId();
        GroupParticipant newParticipant = new GroupParticipant(userId);
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference(GROUPS_KEY)
                .child(groupId);

        groupRef.child(PARTICIPANTS_KEY).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                // Находим следующий доступный индекс
                int nextIndex = 0;

                // Проходим по всем существующим элементам
                for (MutableData child : mutableData.getChildren()) {
                    try {
                        int currentIndex = Integer.parseInt(child.getKey());
                        // Находим максимальный индекс и добавляем 1
                        if (currentIndex >= nextIndex) {
                            nextIndex = currentIndex + 1;
                        }
                    } catch (NumberFormatException e) {
                        // Игнорируем нечисловые ключи
                    }
                }

                // Добавляем новый элемент
                mutableData.child(String.valueOf(nextIndex)).setValue(newParticipant);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                    Log.e("Firebase", "Транзакция не удалась: " + databaseError.getMessage());
                } else if (committed) {
                    Log.d("Firebase", "Элемент успешно добавлен в массив");
                }
            }
        });
    }
}
