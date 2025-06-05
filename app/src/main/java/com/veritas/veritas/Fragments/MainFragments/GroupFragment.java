package com.veritas.veritas.Fragments.MainFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.veritas.veritas.Activities.MainActivity;
import com.veritas.veritas.Fragments.Dialogs.BottomSheetDialogs.JoinViaCodeBottomSheetDialog;
import com.veritas.veritas.Fragments.SpecialFragments.LobbyFragment;
import com.veritas.veritas.R;
import com.veritas.veritas.Util.FragmentWorking;

public class GroupFragment extends Fragment {
    private static final String TAG = "GroupFragment";

    private FragmentWorking fw;

    private AppCompatButton joinViaCodeBt;
    private AppCompatButton createGroupBt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.group_fragment, container, false);

        // I think it is better to check is current activity an instance of MainActivity
        if (requireActivity() instanceof MainActivity) {
            fw = new FragmentWorking(TAG, getParentFragmentManager(),
                    (MainActivity) requireActivity());
        } else {
            Log.wtf(TAG, "MainActivity somehow is not current Activity");
            throw new RuntimeException("MainActivity is not current Activity");
        }

        joinViaCodeBt = view.findViewById(R.id.join_via_code_bt);
        createGroupBt = view.findViewById(R.id.create_group_bt);

        joinViaCodeBt.setOnClickListener(v -> {
            FragmentManager fm = getParentFragmentManager();
            JoinViaCodeBottomSheetDialog bottomSheetDialog = new JoinViaCodeBottomSheetDialog();
            bottomSheetDialog.show(fm, TAG);
        });

        createGroupBt.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) requireActivity();

            if (!mainActivity.canCreateLobby()) {
                return;
            }

            fw.setFragment(new LobbyFragment(true));
        });

        return view;
    }
}