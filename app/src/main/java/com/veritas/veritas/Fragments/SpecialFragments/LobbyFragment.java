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
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.veritas.veritas.Activities.MainActivity;
import com.veritas.veritas.Adapters.LobbyRecyclerAdapter;
import com.veritas.veritas.DB.Firebase.entity.Group;
import com.veritas.veritas.DB.Firebase.entity.GroupParticipant;
import com.veritas.veritas.DB.Firebase.entity.Question;
import com.veritas.veritas.R;
import com.veritas.veritas.Util.FragmentWorking;

import java.util.ArrayList;

public class LobbyFragment extends Fragment {
    private static final String TAG = "LobbyFragment";
    private static final String GROUPS_KEY = "Groups";

    private static Question INIT_QUESTION;

    private FragmentWorking fw;

    private OnBackPressedCallback customOnBackPressedCallback;

    private DatabaseReference fireGroupsRef;

    private Group currentGroup = null;

    private RecyclerView lobbyQuestionRV;
    private LobbyRecyclerAdapter adapter;

    private boolean isRevived = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "OnCreate");

        fireGroupsRef = FirebaseDatabase.getInstance().getReference(GROUPS_KEY);

        if (!isRevived) {
            INIT_QUESTION = new Question(TAG, getString(R.string.init_session_message), "init");
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
        fw = new FragmentWorking(TAG, getParentFragmentManager());

        lobbyQuestionRV = view.findViewById(R.id.lobby_questions_rv);
        if (currentGroup != null) {
            ArrayList<Question> questions = currentGroup.getQuestions();
            if (questions.size() == 1 && questions.contains(INIT_QUESTION)) {
                adapter = new LobbyRecyclerAdapter(requireContext(), currentGroup.getQuestions(), true);
            }
            /*
            * This else is actually redundant to my mind
            * because it seems like init() can be called only with INIT_QUESTION
            * because LobbyFragment won't be created with pre-added questions
             */
            else {
                Log.d(TAG, "Somehow init() has been reached unreachable else");
                adapter = new LobbyRecyclerAdapter(currentGroup.getQuestions());
            }

        } else {
            adapter = new LobbyRecyclerAdapter(new ArrayList<>());
        }

        lobbyQuestionRV.setAdapter(adapter);

        customOnBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getActivity() instanceof MainActivity main) {
                    main.setLobbyFragment(null);
                    fw.setFragment(main.getGroupFragment());
                }
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), customOnBackPressedCallback);
    }

    private Group createLobby() {
        GroupParticipant host = new GroupParticipant("1");
        ArrayList<GroupParticipant> participants = new ArrayList<>();
        participants.add(host);

        ArrayList<Question> questions = new ArrayList<>();
        questions.add(INIT_QUESTION);

        DatabaseReference newLobbyRef = fireGroupsRef.push();

        Group group = new Group(newLobbyRef.getKey(), host, participants, questions);
        newLobbyRef.setValue(group);

        return group;
    }

    public void setIsRevived(boolean revived) {
        isRevived = revived;
    }
}
