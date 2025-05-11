package com.veritas.veritas.Fragments.ModesFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.veritas.veritas.AI.AIRequest;
import com.veritas.veritas.Adapters.RecyclerAdapter;
import com.veritas.veritas.Exceptions.EmptyUsersList;
import com.veritas.veritas.Exceptions.NotEnoughPlayers;
import com.veritas.veritas.R;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModeFragment extends Fragment {

    private static final String TAG = "ModeFragment";

    private String game_name;

    private AIRequest aiRequest;

    private RecyclerView questionsRecycler;

    private SwipeRefreshLayout pullToRefresh;

    ArrayList<String> questions = new ArrayList<>();
    RecyclerAdapter adapter = new RecyclerAdapter(questions);

    private String mode_name;

    public ModeFragment(String mode_name, String game_name) {
        this.game_name = game_name;
        this.mode_name = mode_name;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mode_fragment, container, false);

        questionsRecycler = view.findViewById(R.id.questions_recycler);

        questionsRecycler.setAdapter(adapter);

        pullToRefresh = view.findViewById(R.id.pullToRefresh);

        try {
            aiRequest = new AIRequest(requireContext(), mode_name, game_name);
            pullToRefresh.setOnRefreshListener(() -> {
                APIHandle();
                pullToRefresh.setRefreshing(true);
            });

            APIHandle();
        } catch (EmptyUsersList e) {
            Toast.makeText(requireContext(), "Empty list of players", Toast.LENGTH_SHORT).show();
        } catch (NotEnoughPlayers e) {
            Toast.makeText(requireContext(), "At least 2 players are required to play", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void APIHandle() {
        aiRequest.sendPOST(new AIRequest.ApiCallback() {
            @Override
            public void onSuccess(String content) {
                Pattern pattern = Pattern.compile("<start>(.*?)<end>", Pattern.DOTALL);
                Matcher matcher = pattern.matcher(content);

                questions.clear();
                while (matcher.find()) {
                    questions.add(matcher.group(1).trim());
                }

                Log.d(TAG, content);

                requireActivity().runOnUiThread(() -> {
                    if(isAdded()) {
    //                    adapter.notifyItemInserted(questions.size() - 1);
                        adapter.notifyDataSetChanged();
                        pullToRefresh.setRefreshing(false);
                    } else {
                        Toast.makeText(requireContext(), "Please refresh the list", Toast.LENGTH_SHORT).show();
                    }
                });

                Log.i(TAG, questions.toString());
            }

            @Override
            public void onFailure(String error) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
