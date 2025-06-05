package com.veritas.veritas.Fragments.SpecialFragments;

import static com.veritas.veritas.DB.Firebase.Util.FirebaseManager.GROUPS_KEY;
import static com.veritas.veritas.DB.Firebase.Util.FirebaseManager.GROUPS_MAP_KEY;
import static com.veritas.veritas.DB.Firebase.Util.FirebaseManager.JOIN_CODE_KEY;
import static com.veritas.veritas.Util.CodeGenerator.generateCode;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.veritas.veritas.Activities.MainActivity;
import com.veritas.veritas.Adapters.LobbyRecyclerAdapter;
import com.veritas.veritas.DB.Firebase.Util.FirebaseManager;
import com.veritas.veritas.DB.Firebase.entity.Group;
import com.veritas.veritas.DB.Firebase.entity.GroupParticipant;
import com.veritas.veritas.DB.Firebase.entity.Question;
import com.veritas.veritas.R;
import com.veritas.veritas.Util.FragmentWorking;
import com.veritas.veritas.Util.TokenStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/* TODO:
    Обратите внимание
    Возможности обмена кода подтверждения на токены зависят от архитектуры вашего приложения и от того, какие параметры для поддержки PKCE сервис передал в SDK. */

/* TODO:
    App should store somewhere current group because after MainActivity onDestroy() fragment will be lost
*/

public class LobbyFragment extends Fragment {
    private static final String TAG = "LobbyFragment";

    private Question INIT_MESSAGE;

    private FragmentWorking fw;

    private OnBackPressedCallback customOnBackPressedCallback;

    private DatabaseReference fireGroupsRef;
    private DatabaseReference fireGroupsMapRef;
    private FirebaseManager firebaseManager;
    private DatabaseReference currentGroupRef;

    private ChildEventListener childEventListener;

    private Group currentGroup = null;

    private RecyclerView lobbyQuestionRV;
    private LobbyRecyclerAdapter adapter;
    private ArrayList<Question> currentQuestions;

    private MaterialButton exitBT;

    private long participantsCount;
    private TextView participantsCountTV;

    private FragmentActivity activity;

    private boolean isHost;

    private boolean isRevived = false;

    private String joinCode;

    private String groupId;

    public LobbyFragment(boolean isHost) {
        this.isHost = isHost;
    }

    public LobbyFragment(boolean isHost, String groupId) {
        this.isHost = isHost;

        this.currentGroupRef = FirebaseDatabase.getInstance()
                .getReference(GROUPS_KEY)
                .child(groupId);

        this.groupId = groupId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lobby_fragment, container, false);

        init(view);

        return view;
    }

    private void init(View view) {
        activity = requireActivity();
        fw = new FragmentWorking(TAG, getParentFragmentManager());

        // !!!!!!DEV ONLY!!!!!!
//        customOnBackPressedCallback = new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                if (activity instanceof MainActivity main) {
//                    main.setLobbyFragment(null);
//                    fw.setFragment(main.getGroupFragment());
//                }
//            }
//        };
//        activity.getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), customOnBackPressedCallback);

        currentQuestions = new ArrayList<>();

        fireGroupsRef = FirebaseDatabase.getInstance().getReference(GROUPS_KEY);
        fireGroupsMapRef = FirebaseDatabase.getInstance().getReference(GROUPS_MAP_KEY);

        Bundle args = getArguments();
        if (args != null) {
            isRevived = args.getBoolean("REVIVED_MODE", false);
        }

        if (!isHost) {
            initializeFirebaseManager(groupId);
            currentGroupRef.child(JOIN_CODE_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        joinCode = snapshot.getValue(String.class);
                        Log.d(TAG, "joinCode:\n" + joinCode);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, error.getMessage());
                }
            });
        }

        if (!isRevived && isHost) {
            INIT_MESSAGE = new Question(TAG, getString(R.string.init_session_message), "init");
            currentGroup = createLobby();
        }

        // !!!workaround!!!
        TextView lobbyText = view.findViewById(R.id.lobby_title);
        lobbyText.setText(joinCode);

        lobbyQuestionRV = view.findViewById(R.id.lobby_questions_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        lobbyQuestionRV.setLayoutManager(layoutManager);

        adapter = new LobbyRecyclerAdapter(requireContext(), currentQuestions, true);
        lobbyQuestionRV.setAdapter(adapter);

        setupChildEventListener();

        exitBT = view.findViewById(R.id.exit_lobby);
        exitBT.setOnClickListener(v -> {
            // Removing current group from Realtime Database
            currentGroupRef.removeValue();
            if (joinCode == null) {
                Toast.makeText(activity, "Попробуйте позже", Toast.LENGTH_SHORT).show();
                return;
            }
            fireGroupsMapRef.child(joinCode).removeValue();
            if (activity instanceof MainActivity main) {
                main.setLobbyFragment(null);
                fw.setFragment(main.getGroupFragment());
            }
        });
        participantsCountTV = view.findViewById(R.id.participants_count);
        participantsCountTV.setText(String.valueOf(participantsCount));
    }

    private Group createLobby() {
        TokenStorage tokenStorage = new TokenStorage(requireContext());

        // LobbyFragment won't be called if accessToken is null
        long userId = tokenStorage.getUserId();

        joinCode = generateCode();
        Log.d(TAG, joinCode);

        GroupParticipant host = new GroupParticipant(userId);
        ArrayList<GroupParticipant> participants = new ArrayList<>();
        participants.add(host);

        ArrayList<Question> questions = new ArrayList<>();
        questions.add(INIT_MESSAGE);

        currentGroupRef = fireGroupsRef.push();
        String groupId = currentGroupRef.getKey();

        Group group = new Group(groupId, host, participants, questions);
        group.setJoinCode(joinCode);

        currentGroupRef.setValue(group)
                .addOnSuccessListener(ignored -> {
                    Log.d(TAG, "Group successfully saved to Firebase with ID: " + groupId);
                    initializeFirebaseManager(groupId);
                    Map<String, Object> update = new HashMap<>();
                    update.put(joinCode, groupId);
                    fireGroupsMapRef.updateChildren(update)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("Firebase", "Данные успешно добавлены");
                            })
                            .addOnFailureListener(exception -> {
                                Log.e("Firebase", "Ошибка при добавлении данных", exception);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to save group to Firebase", e);
                });

        return group;
    }

    private void initializeFirebaseManager(String groupId) {
        firebaseManager = new FirebaseManager(groupId);
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).setFirebaseManager(firebaseManager);
        } else {
            Log.wtf(TAG, "MainActivity somehow is not current Activity");
            throw new RuntimeException("MainActivity is not current Activity");
        }

        // Participants count fetch
        firebaseManager.startTracking(groupId, new FirebaseManager.OnCountUpdateListener() {
            @Override
            public void onCountUpdated(long count) {
                participantsCount = count;
                participantsCountTV.setText(String.valueOf(count));
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, error);
            }
        });
    }

    // TODO: Переписать и перепроверить код этой функции.
    private void setupChildEventListener() {
        childEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                Question item = snapshot.getValue(Question.class);
                // Проверяем, не является ли это INIT_MESSAGE, которое уже добавлено
//                boolean isInitMessage = item.getType() != null && item.getType().equals("init");
//                if (isInitMessage && isDataLoaded) {
//                    // INIT_MESSAGE уже добавлено, пропускаем
//                    return;
//                }

                // Проверяем, нет ли уже элемента с таким ключом
                int existingIndex = findItemIndex(item.getKey());
                if (existingIndex == -1) {
                    currentQuestions.add(item);
                    adapter.notifyItemInserted(currentQuestions.size() - 1);
                    Log.d(TAG, "Item added at position: " + (currentQuestions.size() - 1));
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {
                Question updatedItem = snapshot.getValue(Question.class);
                if (updatedItem != null) {
                    updatedItem.setKey(snapshot.getKey());
                    String key = snapshot.getKey();
                    int index = findItemIndex(key); // реализуйте этот метод
                    if (index != -1) {
                        currentQuestions.set(index, updatedItem);
                        adapter.notifyItemChanged(index);
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String key = snapshot.getKey();
                int index = findItemIndex(key);
                if (index != -1) {
                    currentQuestions.remove(index);
                    adapter.notifyItemRemoved(index);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {
                // Обработка перемещения элементов
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error: " + error.getMessage());
            }
        };

        if (currentGroupRef != null) {
            currentGroupRef.child("questions").addChildEventListener(childEventListener);
        }
    }

    private int findItemIndex(String key) {
        int size = currentQuestions.size();
        for (int i = 0; i < size; i++) {
            Question item = currentQuestions.get(i);
            if (item.getKey() != null && item.getKey().equals(key)) {
                return i;
            }
        }
        return -1;
    }

//    public void setIsRevived(boolean revived) {
//        isRevived = revived;
//    }

    public Question getINIT_MESSAGE() {
        return INIT_MESSAGE;
    }

    @Override
    public void onDestroyView() {
        // Очищаем ссылки для предотвращения утечек памяти
        if (childEventListener != null && currentGroupRef != null) {
            currentGroupRef.child("questions").removeEventListener(childEventListener);
        }
        firebaseManager.stopTracking();
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("SAVED_REVIVED_STATE", isRevived);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            isRevived = savedInstanceState.getBoolean("SAVED_REVIVED_STATE", false);
        }
    }
}
