package com.veritas.veritas.Fragments.SpecialFragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.res.ResourcesCompat;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.veritas.veritas.AI.AIRequest;
import com.veritas.veritas.Activities.MainActivity;
import com.veritas.veritas.Adapters.RecyclerAdapter;
import com.veritas.veritas.Exceptions.EmptyUsersList;
import com.veritas.veritas.Exceptions.NotEnoughPlayers;
import com.veritas.veritas.Fragments.Dialogs.BottomSheetDialogs.ReactionsBottomSheetDialog;
import com.veritas.veritas.R;
import com.veritas.veritas.Util.FragmentWorking;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModeFragment extends Fragment
        implements RecyclerAdapter.RecyclerAdapterOnLongItemClickListener {

    private static final String TAG = "ModeFragment";

    private OnBackPressedCallback customOnBackPressedCallback;

    private FragmentWorking fw;

    private String gameName;

    private AIRequest aiRequest;

    private RecyclerView questionsRecycler;

    private SwipeRefreshLayout pullToRefresh;
    private CircularProgressIndicator initialLoadingIndicator;

    ArrayList<String> contentList = new ArrayList<>();
    RecyclerAdapter adapter;

    private String modeName;
    private boolean isFirstLoad = true;

    private boolean isRevived = false;

    public ModeFragment(String gameName, String modeName) {
        this.gameName = gameName;
        this.modeName = modeName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mode_fragment, container, false);

        init(view);

        recyclerViewHandle();

        if (!isRevived) {
            try {
                aiRequest = new AIRequest(requireContext(), modeName, gameName);
                pullToRefresh.setOnRefreshListener(this::APIHandle);

                if (isFirstLoad) {
                    initialLoadingIndicator.setVisibility(View.VISIBLE);
                    pullToRefresh.setEnabled(false);
                }

                APIHandle();

            } catch (EmptyUsersList e) {
                Toast.makeText(requireContext(), "Empty list of players", Toast.LENGTH_SHORT).show();
                if (isFirstLoad) {
                    initialLoadingIndicator.setVisibility(View.GONE);
                    pullToRefresh.setEnabled(true);
                }
            } catch (NotEnoughPlayers e) {
                Toast.makeText(requireContext(), "At least 2 players are required to play", Toast.LENGTH_SHORT).show();
                if (isFirstLoad) {
                    initialLoadingIndicator.setVisibility(View.GONE);
                    pullToRefresh.setEnabled(true);
                }
            }
        } else {
            pullToRefresh.setOnRefreshListener(this::APIHandle);
        }

        return view;
    }

    @Override
    public void onLongItemClick(View view, int position) {
        String content = contentList.get(position);
        ReactionsBottomSheetDialog bottomSheetDialog =
                new ReactionsBottomSheetDialog(gameName, modeName, content);
        bottomSheetDialog.show(getParentFragmentManager(), TAG);
    }

    public void setIsRevived(boolean isRevived) {
        this.isRevived = isRevived;
    }

    private void init(View view) {
        fw = new FragmentWorking(TAG, getParentFragmentManager());

        questionsRecycler = view.findViewById(R.id.questions_recycler);
        initialLoadingIndicator = view.findViewById(R.id.initial_loading_indicator);

        pullToRefresh = view.findViewById(R.id.pullToRefresh);

        customOnBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getActivity() instanceof MainActivity main) {
                    main.setModeFragment(null);
                    fw.setFragment(main.getGameSelectionFragment());
                }
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), customOnBackPressedCallback);
    }

    private void recyclerViewHandle() {
        final Typeface font = ResourcesCompat.getFont(requireContext(), R.font.montserrat_medium);

        adapter = new RecyclerAdapter(contentList, false, font);
        adapter.setOnClickListener(this);
        questionsRecycler.setAdapter(adapter);
    }

    private void APIHandle() {
        aiRequest.sendPOST(new AIRequest.ApiCallback() {
            @Override
            public void onSuccess(String content) {
                if (isAdded()) {
                    Pattern pattern = Pattern.compile("<start>(.*?)<end>", Pattern.DOTALL);
                    Matcher matcher = pattern.matcher(content);

                    contentList.clear();
                    while (matcher.find()) {
                        contentList.add(matcher.group(1).trim());
                    }

                    if (contentList.isEmpty()) {
                        requireActivity().runOnUiThread(() -> {
                            pullToRefresh.setRefreshing(false);

                            if (isFirstLoad) {
                                initialLoadingIndicator.setVisibility(View.GONE);
                                isFirstLoad = false;
                                pullToRefresh.setEnabled(true);
                            }
                            Toast.makeText(requireContext(), "Please refresh the list", Toast.LENGTH_LONG).show();
                        });
                        return;
                    }

                    Log.d(TAG, content);

                    requireActivity().runOnUiThread(() -> {
                        adapter.notifyDataSetChanged();
                        pullToRefresh.setRefreshing(false);
                        if (isFirstLoad) {
                            initialLoadingIndicator.setVisibility(View.GONE);
                            isFirstLoad = false;
                            pullToRefresh.setEnabled(true);
                        }
                    });

                    Log.i(TAG, contentList.toString());
                } else {
                    Log.w(TAG, "Fragment " + TAG + " not attached to activity on onSuccess callback. UI will not be updated.");
                }
            }

            @Override
            public void onFailure(String error) {
                Log.w(TAG, "onFailure:\n" + error);
                if (isAdded()) {
                    if (error.equals("code 429")) {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), "Reached limit", Toast.LENGTH_LONG).show());
                    } else if (error.equals("timeout")) {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), "Response time is up", Toast.LENGTH_LONG).show());
                    } else {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), "Error: " + error, Toast.LENGTH_LONG).show());
                    }
                    pullToRefresh.setRefreshing(false);
                    if (isFirstLoad) {
                        initialLoadingIndicator.setVisibility(View.GONE);
                        isFirstLoad = false;
                        pullToRefresh.setEnabled(true);
                    }
                } else {
                    Log.w(TAG, "Fragment " + TAG + " not attached to activity on onFailure callback. Toast will not be shown.");
                }
            }
        });
    }
}
