package com.veritas.veritas.Fragments.SpecialFragments;

import static com.veritas.veritas.Application.App.getAccessToken;
import static com.veritas.veritas.Application.App.getVKID;
import static com.veritas.veritas.Util.CodeGenerator.generateCode;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.veritas.veritas.Activities.MainActivity;
import com.veritas.veritas.Adapters.LobbyRecyclerAdapter;
import com.veritas.veritas.DB.Firebase.Util.FirebaseManager;
import com.veritas.veritas.DB.Firebase.entity.Group;
import com.veritas.veritas.DB.Firebase.entity.GroupParticipant;
import com.veritas.veritas.DB.Firebase.entity.Question;
import com.veritas.veritas.R;
import com.veritas.veritas.Util.FragmentWorking;
import com.vk.id.AccessToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/* TODO:
    Обратите внимание
    Возможности обмена кода подтверждения на токены зависят от архитектуры вашего приложения и от того, какие параметры для поддержки PKCE сервис передал в SDK. */

public class LobbyFragment extends Fragment {
    private static final String TAG = "LobbyFragment";
    private static final String GROUPS_KEY = "groups";
    private static final String GROUPS_MAP_KEY = "groupsMap";

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

    private FragmentActivity activity;

    private boolean isRevived = false;
    private boolean isDataLoaded = false;

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

        // TODO: It should work in a different way
        customOnBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (activity instanceof MainActivity main) {
                    main.setLobbyFragment(null);
                    fw.setFragment(main.getGroupFragment());
                }
            }
        };

        activity.getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), customOnBackPressedCallback);

        currentQuestions = new ArrayList<>();

        fireGroupsRef = FirebaseDatabase.getInstance().getReference(GROUPS_KEY);
        fireGroupsMapRef = FirebaseDatabase.getInstance().getReference(GROUPS_MAP_KEY);

        if (!isRevived) {
            INIT_MESSAGE = new Question(TAG, getString(R.string.init_session_message), "init");
            currentGroup = createLobby();
        }

        lobbyQuestionRV = view.findViewById(R.id.lobby_questions_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        lobbyQuestionRV.setLayoutManager(layoutManager);

        if (currentGroup != null && !isDataLoaded) {
            currentQuestions.clear();
            currentQuestions.add(INIT_MESSAGE);
            isDataLoaded = true;
        }

        adapter = new LobbyRecyclerAdapter(requireContext(), currentQuestions, true);
        lobbyQuestionRV.setAdapter(adapter);

        setupChildEventListener();
    }

    private Group createLobby() {
        AccessToken accessToken = getAccessToken(getViewLifecycleOwner(), requireContext());

        // LobbyFragment won't be called if accessToken is null
        long userId = accessToken.getUserID();
        Log.d(TAG, "userId: " + userId);

        String code = generateCode();
        Log.d(TAG, code);

        GroupParticipant host = new GroupParticipant(userId);
        ArrayList<GroupParticipant> participants = new ArrayList<>();
        participants.add(host);

        ArrayList<Question> questions = new ArrayList<>();
        questions.add(INIT_MESSAGE);

        currentGroupRef = fireGroupsRef.push();
        String groupId = currentGroupRef.getKey();

        Group group = new Group(groupId, host, participants, questions);
        group.setJoinCode(code);

        currentGroupRef.setValue(group)
                .addOnSuccessListener(ignored -> {
                    Log.d(TAG, "Group successfully saved to Firebase with ID: " + groupId);
                    initializeFirebaseManager(groupId);
                    Map<String, Object> update = new HashMap<>();
                    update.put(code, groupId);
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
    }

    // TODO: Переписать и перепроверить код этой функции.
    private void setupChildEventListener() {
        childEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                Question item = snapshot.getValue(Question.class);
                // Проверяем, не является ли это INIT_MESSAGE, которое уже добавлено
                boolean isInitMessage = item.getType() != null && item.getType().equals("init");
                if (isInitMessage && isDataLoaded) {
                    // INIT_MESSAGE уже добавлено, пропускаем
                    return;
                }

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

    public void setIsRevived(boolean revived) {
        isRevived = revived;
    }

    public Question getINIT_MESSAGE() {
        return INIT_MESSAGE;
    }

    @Override
    public void onDestroyView() {
        // Очищаем ссылки для предотвращения утечек памяти
        if (childEventListener != null && currentGroupRef != null) {
            currentGroupRef.child("questions").removeEventListener(childEventListener);
        }
        super.onDestroyView();
    }
}
