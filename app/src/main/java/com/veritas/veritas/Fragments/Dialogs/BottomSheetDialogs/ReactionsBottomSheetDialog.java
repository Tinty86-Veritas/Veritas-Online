package com.veritas.veritas.Fragments.Dialogs.BottomSheetDialogs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.veritas.veritas.Exceptions.AddQuestionFailedException;
import com.veritas.veritas.Exceptions.InitMessageUpdateFailedException;
import com.veritas.veritas.Exceptions.LobbyIsNullException;
import com.veritas.veritas.Fragments.SpecialFragments.LobbyFragment;
import com.veritas.veritas.R;

import java.util.ArrayList;
import java.util.List;

public class ReactionsBottomSheetDialog extends BottomSheetDialogFragment
        implements RecyclerAdapter.RecyclerAdapterOnItemClickListener {

    private static final String TAG = "ReactionsBottomSheetDialog";

    private String gameName;
    private String modeName;
    private String content;

    public ReactionsBottomSheetDialog(String gameName, String modeName, String content) {
        this.gameName = gameName;
        this.modeName = modeName;
        this.content = content;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.standard_bottom_sheet_dialog_fragment, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        ArrayList<String> items = new ArrayList<>(List.of(
                getString(R.string.share_with_lobby), getString(R.string.like_option), getString(R.string.dislike_option), getString(R.string.recurring_option)
        ));

        RecyclerAdapter adapter = new RecyclerAdapter(items);

        adapter.setOnClickListener(this);

        recyclerView.setAdapter(adapter);

        return view;
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
        GamesDB gamesDB = new GamesDB(requireContext());
        switch (position) {
            // Почему-то не работает
            case 0 -> {
                if (getActivity() instanceof MainActivity) {
                    boolean isError = false;
                    FirebaseManager firebaseManager = ((MainActivity) getActivity()).getFirebaseManager();
                    if (firebaseManager == null) {
                        Log.w(TAG, "firebaseManager is null");
                        Toast.makeText(requireContext(), R.string.lobby_have_not_created_yet, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    try {
                        updateInitMessage(firebaseManager);
                    } catch (InitMessageUpdateFailedException e) {
                        if (e.getMessage().equals("question does not equals to INIT_MESSAGE")) {
                            try {
                                addQuestion(firebaseManager);
                            } catch (AddQuestionFailedException ignored) {
                                isError = true;
                                Toast.makeText(requireContext(), "Firebase error", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    if (!isError) {
                        Toast.makeText(requireContext(), "Успешно отправлено в группу", Toast.LENGTH_SHORT).show();
                    }
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

    private void addQuestion(FirebaseManager firebaseManager) throws AddQuestionFailedException {
        Question newQuestion = new Question(TAG, content, gameName);
        firebaseManager.addQuestion(newQuestion, new FirebaseManager.OnQuestionsUpdatedListener() {
            @Override
            public void onSuccess() {}

            @Override
            public void onFailure(String error) {
                Log.e(TAG, error);
                throw new AddQuestionFailedException(error);
            }
        });
    }

    private void updateInitMessage(FirebaseManager firebaseManager) throws LobbyIsNullException,
            InitMessageUpdateFailedException {
        firebaseManager.getQuestionByIndex(0, new FirebaseManager.OnQuestionGotListener() {
            @Override
            public void onSuccess(Question question) {
                LobbyFragment lobbyFragment = ((MainActivity) getActivity()).getLobbyFragment();
                if (lobbyFragment == null) {
                    Log.w(TAG, "Cannot share question with lobby because lobbyFragment is null");
                    Toast.makeText(requireContext(), R.string.lobby_have_not_created_yet, Toast.LENGTH_SHORT).show();
                    throw new LobbyIsNullException(null);
                }
                Question INIT_MESSAGE = lobbyFragment.getINIT_MESSAGE();
                if (question.equals(INIT_MESSAGE)) {
                    Question newQuestion = new Question(TAG, content, gameName);
                    firebaseManager.updateQuestionByIndex(0, newQuestion,
                            new FirebaseManager.OnQuestionsUpdatedListener() {
                        @Override
                        public void onSuccess() {}

                        @Override
                        public void onFailure(String error) {
                            Log.e(TAG, error);
                            throw new InitMessageUpdateFailedException("OnQuestionsUpdatedListener onFailure");
                        }
                    });
                } else {
                    throw new InitMessageUpdateFailedException("question does not equals to INIT_MESSAGE");
                }
            }

            @Override
            public void onFailure(String error) {
                Log.d(TAG, error);
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
                Toast.makeText(requireContext(), R.string.reaction_deleted, Toast.LENGTH_SHORT).show();
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
        Toast.makeText(requireContext(), R.string.reaction_saved, Toast.LENGTH_SHORT).show();
        dismiss();
    }
}
