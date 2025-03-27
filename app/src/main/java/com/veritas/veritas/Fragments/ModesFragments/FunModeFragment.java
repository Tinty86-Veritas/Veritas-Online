package com.veritas.veritas.Fragments.ModesFragments;

import static com.veritas.veritas.AI.AIRequest.sendPOST;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.veritas.veritas.AI.AIRequest;
import com.veritas.veritas.DB.PlayersTable;
import com.veritas.veritas.R;

import org.json.JSONObject;

public class FunModeFragment extends Fragment {

    private static final String TAG = "FunModeFragment";

    private TextView answerTV;

    private ImageButton refreshBt;

    private String request_result;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fun_mode_fragment, container, false);

        PlayersTable playersTable = new PlayersTable(requireContext());

        refreshBt = view.findViewById(R.id.refresh_bt);
        answerTV = view.findViewById(R.id.output_tv);

        refreshBt.setOnClickListener(v -> {
            APIHandle();
        });

        APIHandle();

        return view;
    }

    private void APIHandle() {
        sendPOST(new AIRequest.ApiCallback() {
            @Override
            public void onSuccess(String content) {
                // Важно: обновление UI должно быть в главном потоке
                requireActivity().runOnUiThread(() -> {
                    answerTV.setText(content);
                });
            }

            @Override
            public void onFailure(String error) {
                requireActivity().runOnUiThread(() -> {
                    answerTV.setText("Error: " + error);
                });
            }
        });
    }

}
