package com.veritas.veritas.Fragments.Dialogs.BottomSheetDialogs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.veritas.veritas.Activities.MainActivity;
import com.veritas.veritas.Adapters.RecyclerAdapter;
import com.veritas.veritas.DB.Firebase.Util.FirebaseManager;
import com.veritas.veritas.DB.Firebase.entity.Question;
import com.veritas.veritas.DB.GamesDB;
import com.veritas.veritas.Fragments.SpecialFragments.LobbyFragment;
import com.veritas.veritas.R;

import java.util.ArrayList;
import java.util.List;

public class ReactionsBottomSheetDialog extends BottomSheetDialogFragment
        implements RecyclerAdapter.RecyclerAdapterOnItemClickListener {

    private static final String TAG = "ReactionsBottomSheetDialog";

    private FragmentActivity activity;
    private Context context;

    private String gameName;
    private String modeName;
    private String content;

    private LobbyFragment lobbyFragment;

    public ReactionsBottomSheetDialog(String gameName, String modeName, String content) {
        this.gameName = gameName;
        this.modeName = modeName;
        this.content = content;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.standard_bottom_sheet_dialog_fragment, container, false);

        init(view);

        return view;
    }

    private void init(View view) {
        activity = requireActivity();
        context = requireContext();

        lobbyFragment = ((MainActivity) activity).getLobbyFragment();

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        ArrayList<String> items = new ArrayList<>(List.of(
                getString(R.string.share_with_lobby), getString(R.string.like_option), getString(R.string.dislike_option), getString(R.string.recurring_option)
        ));

        RecyclerAdapter adapter = new RecyclerAdapter(items);

        adapter.setOnClickListener(this);

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        if (dialog != null) {
            FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<?> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        GamesDB gamesDB = new GamesDB(context);
        switch (position) {
            case 0 -> {
                if (activity instanceof MainActivity) {
                    FirebaseManager firebaseManager = ((MainActivity) activity).getFirebaseManager();

                    // TODO: This check for some reason passes when user is not the host
                    if (firebaseManager == null) {
                        Log.w(TAG, "firebaseManager is null");
                        Toast.makeText(context, R.string.lobby_have_not_created_yet, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    shareWithLobby(firebaseManager);
                } else {
                    Log.wtf(TAG, "MainActivity somehow is not current Activity");
                    throw new RuntimeException("MainActivity is not current Activity");
                }
            }
            case 1 -> {
                final String type = "like";
                if (!deleteHandle(gamesDB, type)) {
                    addHandle(gamesDB, type);
                }
            }
            case 2 -> {
                final String type = "dislike";
                if (!deleteHandle(gamesDB, type)) {
                    addHandle(gamesDB, type);
                }
            }
            case 3 -> {
                final String type = "recurring";
                if (!deleteHandle(gamesDB, type)) {
                    addHandle(gamesDB, type);
                }
            }
        }
        gamesDB.close();
    }

    // TODO: If user is not the host of the lobby init message won't be replaced but will be just stored
    private void shareWithLobby(FirebaseManager firebaseManager) {
        firebaseManager.getQuestionByIndex(0, new FirebaseManager.OnQuestionGotListener() {
            @Override
            public void onSuccess(Question question) {
                if (lobbyFragment == null) {
                    Log.w(TAG, "Cannot share question with lobby because lobbyFragment is null");
                    Toast.makeText(context, R.string.lobby_have_not_created_yet, Toast.LENGTH_SHORT).show();
                    return;
                }

                Question INIT_MESSAGE = lobbyFragment.getINIT_MESSAGE();
                Question newQuestion = new Question(TAG, content, gameName);
                if (question.equals(INIT_MESSAGE)) {
                    firebaseManager.updateQuestionByIndex(0, newQuestion,
                            new FirebaseManager.OnQuestionsUpdatedListener() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(context, "Успешно отправлено в группу", Toast.LENGTH_SHORT).show();
                                    dismiss();
                                }

                                @Override
                                public void onFailure(String error) {
                                    Log.e(TAG, "Failed to update INIT_MESSAGE: " + error);
                                    Toast.makeText(context, "Ошибка при обновлении: " + error, Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    firebaseManager.addQuestion(newQuestion, new FirebaseManager.OnQuestionsUpdatedListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(context, "Успешно отправлено в группу", Toast.LENGTH_SHORT).show();
                            dismiss();
                        }

                        @Override
                        public void onFailure(String error) {
                            if (error.equals("duplicating question")) {
                                Log.w(TAG, "duplicating question");
                                Toast.makeText(context, "Уже добавлено", Toast.LENGTH_LONG).show();
                            } else {
                                Log.e(TAG, "Failed to add question: " + error);
                                Toast.makeText(context, "Ошибка при добавлении: " + error, Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Failed to get question by index: " + error);
                Toast.makeText(context, "Ошибка при получении данных: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearPreviousType(GamesDB gamesDB) {
        if (gamesDB.hasReaction(gameName, modeName, content)) {
            gamesDB.deleteReaction(gameName, modeName, content);
        }
    }

    private boolean deleteHandle(GamesDB gamesDB, String currentType) {
        final String type = gamesDB.getReactionType(gameName, modeName, content);
        if (type != null) {
            if (type.equals(currentType)) {
                gamesDB.deleteReaction(gameName, modeName, content);
                Toast.makeText(context, R.string.reaction_deleted, Toast.LENGTH_SHORT).show();
                return true;
            } else {
                Log.wtf(TAG, type);
            }
        }
        return false;
    }

    private void addHandle(GamesDB gamesDB, String type) {
        clearPreviousType(gamesDB);
        gamesDB.addReaction(gameName, modeName, type, content);
        Toast.makeText(context, R.string.reaction_saved, Toast.LENGTH_SHORT).show();
        dismiss();
    }
}
