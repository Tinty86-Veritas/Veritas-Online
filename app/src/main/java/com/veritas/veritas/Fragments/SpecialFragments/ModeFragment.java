package com.veritas.veritas.Fragments.SpecialFragments;

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
import com.veritas.veritas.Fragments.Dialogs.ReactionsBottomSheetDialog;
import com.veritas.veritas.R;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModeFragment extends Fragment
        implements RecyclerAdapter.RecyclerAdapterOnLongItemClickListener {

    private static final String TAG = "ModeFragment";

    private String gameName;

    private AIRequest aiRequest;

    private RecyclerView questionsRecycler;

    private SwipeRefreshLayout pullToRefresh;

    ArrayList<String> contentList = new ArrayList<>();
    RecyclerAdapter adapter = new RecyclerAdapter(contentList);

    private String modeName;

    public ModeFragment(String modeName, String gameName) {
        this.gameName = gameName;
        this.modeName = modeName;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mode_fragment, container, false);

        questionsRecycler = view.findViewById(R.id.questions_recycler);

        adapter.setOnClickListener(this);

        questionsRecycler.setAdapter(adapter);

        pullToRefresh = view.findViewById(R.id.pullToRefresh);

        try {
            aiRequest = new AIRequest(requireContext(), modeName, gameName);
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

                if(isAdded()) {
                    Pattern pattern = Pattern.compile("<start>(.*?)<end>", Pattern.DOTALL);
                    Matcher matcher = pattern.matcher(content);

                    contentList.clear();
                    while (matcher.find()) {
                        contentList.add(matcher.group(1).trim());
                    }

                    if (contentList.isEmpty()) {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "Please refresh the list", Toast.LENGTH_SHORT).show();
                        });
                        return;
                    }

                    Log.d(TAG, content);

                    requireActivity().runOnUiThread(() -> {
                        adapter.notifyDataSetChanged();
                        pullToRefresh.setRefreshing(false);
                    });

                    Log.i(TAG, contentList.toString());
                } else {
                    Log.w(TAG, "Fragment " + TAG + " not attached to activity on onSuccess callback. UI will not be updated.");
                }
            }

            @Override
            public void onFailure(String error) {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                    });
                } else {
                    Log.w(TAG, "Fragment " + TAG + " not attached to activity on onFailure callback. Toast will not be shown.");
                }
            }
        });
    }

    @Override
    public void onLongItemClick(View view, int position) {
        String content = contentList.get(position);
        ReactionsBottomSheetDialog bottomSheetDialog =
                new ReactionsBottomSheetDialog(gameName, modeName, content);
        bottomSheetDialog.show(getParentFragmentManager(), TAG);
    }
}
