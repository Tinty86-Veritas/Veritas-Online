package com.veritas.veritas.Fragments.SpecialFragments;

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

import java.util.ArrayList;

public class LobbyFragment extends Fragment {
    private static final String TAG = "LobbyFragment";
    private static final String GROUPS_KEY = "groups";

    private Question INIT_MESSAGE;

    private FragmentWorking fw;

    private OnBackPressedCallback customOnBackPressedCallback;

    private DatabaseReference fireGroupsRef;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentQuestions = new ArrayList<>();

        fireGroupsRef = FirebaseDatabase.getInstance().getReference(GROUPS_KEY);

        if (!isRevived) {
            INIT_MESSAGE = new Question(TAG, getString(R.string.init_session_message), "init");
            currentGroup = createLobby();
        }
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

        lobbyQuestionRV = view.findViewById(R.id.lobby_questions_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        lobbyQuestionRV.setLayoutManager(layoutManager);

//        if (currentGroup != null) {
//            ArrayList<Question> questions = currentGroup.getQuestions();
//            if (questions.size() == 1 && questions.contains(INIT_MESSAGE)) {
//                adapter = new LobbyRecyclerAdapter(requireContext(), currentGroup.getQuestions(), true);
//            }
//            /*
//            * This else is actually redundant to my mind
//            * because it seems like init() can be called only with INIT_QUESTION
//            * because LobbyFragment won't be created with pre-added questions
//             */
//            else {
//                Log.d(TAG, "Somehow init() has been reached unreachable else");
//                adapter = new LobbyRecyclerAdapter(currentGroup.getQuestions());
//            }
//
//        } else {
//            adapter = new LobbyRecyclerAdapter(new ArrayList<>());
//        }

        if (currentGroup != null && !isDataLoaded) {
            currentQuestions.clear();
            currentQuestions.add(INIT_MESSAGE);
            isDataLoaded = true;
        }

        adapter = new LobbyRecyclerAdapter(requireContext(), currentQuestions, true);
        lobbyQuestionRV.setAdapter(adapter);

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

        setupChildEventListener();
    }

    private Group createLobby() {

        GroupParticipant host = new GroupParticipant("1");
        ArrayList<GroupParticipant> participants = new ArrayList<>();
        participants.add(host);

        ArrayList<Question> questions = new ArrayList<>();
        questions.add(INIT_MESSAGE);

        currentGroupRef = fireGroupsRef.push();
        String groupId = currentGroupRef.getKey();

        Group group = new Group(groupId, host, participants, questions);

        currentGroupRef.setValue(group)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Group successfully saved to Firebase with ID: " + groupId);
                    initializeFirebaseManager(groupId);
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
