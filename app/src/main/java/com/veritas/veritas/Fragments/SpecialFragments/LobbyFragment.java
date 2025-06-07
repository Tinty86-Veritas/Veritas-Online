package com.veritas.veritas.Fragments.SpecialFragments;

import static com.veritas.veritas.DB.Firebase.Util.FirebaseManager.GROUPS_KEY;
import static com.veritas.veritas.DB.Firebase.Util.FirebaseManager.GROUPS_MAP_KEY;
import static com.veritas.veritas.DB.Firebase.Util.FirebaseManager.JOIN_CODE_KEY;
import static com.veritas.veritas.DB.Firebase.Util.FirebaseManager.PARTICIPANTS_KEY;
import static com.veritas.veritas.Util.CodeGenerator.generateCode;

import android.content.Context;
import android.content.SharedPreferences;
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
    Add a progress indicator while questions is loading
* */

public class LobbyFragment extends Fragment {
    private static final String TAG = "LobbyFragment";

    public static final String CURRENT_GROUP_KEY = "current_group";
    public static final String GROUP_ID_KEY = "groupId";

    private Question INIT_MESSAGE;

    private FragmentWorking fw;
    private SharedPreferences sharedPreferences;

    private DatabaseReference fireGroupsRef;
    private DatabaseReference fireGroupsMapRef;
    private FirebaseManager firebaseManager;
    private DatabaseReference currentGroupRef;

    private ChildEventListener childEventListener;

    private RecyclerView lobbyQuestionRV;
    private LobbyRecyclerAdapter adapter;
    private ArrayList<Question> currentQuestions;

    private MaterialButton exitBT;

    private long participantsCount = 0;
    private TextView participantsCountTV;

    private FragmentActivity activity;

    private boolean isHost;

    private boolean isRevived = false;

    private String joinCode;

    private String groupId;

    private TextView lobbyText;

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
        lobbyText = view.findViewById(R.id.lobby_title);

        sharedPreferences = requireContext().getSharedPreferences(CURRENT_GROUP_KEY, Context.MODE_PRIVATE);

        fw = new FragmentWorking(TAG, getParentFragmentManager());

        exitBT = view.findViewById(R.id.exit_lobby);

        participantsCountTV = view.findViewById(R.id.participants_count);

        lobbyQuestionRV = view.findViewById(R.id.lobby_questions_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        lobbyQuestionRV.setLayoutManager(layoutManager);



        // !!!!!!DEV ONLY!!!!!!
        OnBackPressedCallback customOnBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // !!!!NORMAL DEBUG BEHAVIOUR!!!!
                if (activity instanceof MainActivity main) {
                    main.setLobbyFragment(null);
                    fw.setFragment(main.getGroupFragment());
                }

                // !!!!DELETING CURRENT GROUP FROM SHARED PREFERENCES!!!!
//                performExit();
            }
        };
        activity.getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), customOnBackPressedCallback);



        currentQuestions = new ArrayList<>();

        fireGroupsRef = FirebaseDatabase.getInstance().getReference(GROUPS_KEY);
        fireGroupsMapRef = FirebaseDatabase.getInstance().getReference(GROUPS_MAP_KEY);

        Bundle args = getArguments();
        String sharedGroupId = null;
        if (args != null) {
            isRevived = args.getBoolean("REVIVED_MODE", false);
            sharedGroupId = args.getString(GROUP_ID_KEY, null);
        }

        Log.d(TAG, "sharedGroupId: " + sharedGroupId);

        if (sharedGroupId == null) {
            // Group init
            groupInit();
        } else {
            groupId = sharedGroupId;
            currentGroupRef = fireGroupsRef.child(groupId);
            currentGroupRef.child(JOIN_CODE_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        joinCode = snapshot.getValue(String.class);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, error.getMessage());
                }
            });
            initializeFirebaseManager(groupId);
            updateLobbyText();
        }

        if (isRevived) {
            updateLobbyText();
        }

        /* TODO:
            CRITICAL MISTAKE:
            Now even not a host can delete group from DB after clicking on the exitBt
            Solution:
            If user is not a host delete only their data from DB
        */
        exitBT.setOnClickListener(v -> {
            if (isHost) {
                // Removing current group from Realtime Database
                currentGroupRef.removeValue();

                // I think this check is redundant
                if (joinCode == null) {
                    Toast.makeText(activity, "Попробуйте позже", Toast.LENGTH_SHORT).show();
                    return;
                }
                fireGroupsMapRef.child(joinCode).removeValue();
                performExit();
            } else {
                // Deleting participant data from Firebase
                currentGroupRef.child(PARTICIPANTS_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            TokenStorage tokenStorage = new TokenStorage(requireContext());
                            Long userId = tokenStorage.getUserId();
                            Long currentSnapshotId = childSnapshot.child("id").getValue(Long.class);
                            if (userId.equals(currentSnapshotId)) {
                                childSnapshot.getRef().removeValue();
                            }
                        }
                        performExit();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, error.getMessage());
                        Toast.makeText(activity, "Произошла ошибка. Попробуйте позже", Toast.LENGTH_LONG).show();
                        // no performExit() to prevent leaving group without deleting data from participants
                    }
                });

            }
        });

        if (groupId == null) {
            Log.e(TAG, "groupId is somehow null");
        } else if (sharedGroupId == null) {
            sharedPreferences.edit()
                    .putString(GROUP_ID_KEY, groupId)
                    .apply();
        }

        adapter = new LobbyRecyclerAdapter(requireContext(), currentQuestions, true);
        lobbyQuestionRV.setAdapter(adapter);

        setupChildEventListener();
        participantsCountTV.setText(String.valueOf(participantsCount));
    }

    private void performExit() {
        if (activity instanceof MainActivity main) {
            main.setLobbyFragment(null);
            fw.setFragment(main.getGroupFragment());
        }

        sharedPreferences.edit()
                .remove(GROUP_ID_KEY)
                .apply();
    }

    private DatabaseReference createLobby() {
        TokenStorage tokenStorage = new TokenStorage(requireContext());

        long userId = tokenStorage.getUserId();

        joinCode = generateCode();
        Log.d(TAG, joinCode);

        lobbyText.setText(joinCode);

        GroupParticipant host = new GroupParticipant(userId);
        ArrayList<GroupParticipant> participants = new ArrayList<>();
        participants.add(host);

        ArrayList<Question> questions = new ArrayList<>();
        questions.add(INIT_MESSAGE);

        DatabaseReference currentGroupRef = fireGroupsRef.push();
        groupId = currentGroupRef.getKey();

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

        return currentGroupRef;
    }

    private void groupInit() {
        if (!isHost) {
//            currentGroupRef.child(JOIN_CODE_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    if (snapshot.exists()) {
//                        joinCode = snapshot.getValue(String.class);
//                        Log.d(TAG, "joinCode:\n" + joinCode);
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    Log.e(TAG, error.getMessage());
//                }
//            });

            // workaround
            updateLobbyText();

            initializeFirebaseManager(groupId);
        }

        if (!isRevived && isHost) {
            INIT_MESSAGE = new Question(TAG, getString(R.string.init_session_message), "init");
            currentGroupRef = createLobby();
        }
    }

    private void updateLobbyText() {
        currentGroupRef.child(JOIN_CODE_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    joinCode = snapshot.getValue(String.class);
                    Log.d(TAG, "joinCode:\n" + joinCode);
                    activity.runOnUiThread(() -> {
                        // !!!workaround!!!
                        lobbyText.setText(joinCode);
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());
            }
        });
    }

    private void initializeFirebaseManager(String groupId) {
        Log.d(TAG, "initializeFirebaseManager");

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
