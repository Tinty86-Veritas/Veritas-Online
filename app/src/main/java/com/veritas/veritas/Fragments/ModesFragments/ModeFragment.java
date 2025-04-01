package com.veritas.veritas.Fragments.ModesFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.veritas.veritas.AI.AIRequest;
import com.veritas.veritas.R;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModeFragment extends Fragment {

    private static final String TAG = "ModeFragment";

    private AIRequest aiRequest;

    private RecyclerView questionsRecycler;

    private ImageButton refreshBt;

    ArrayList<String> questions = new ArrayList<>();
    RecyclerAdapter adapter = new RecyclerAdapter(questions);

    private String mode_name;

    public ModeFragment(String mode_name) {
        this.mode_name = mode_name;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mode_fragment, container, false);

        aiRequest = new AIRequest(requireContext(), mode_name);

        refreshBt = view.findViewById(R.id.refresh_bt);
        questionsRecycler = view.findViewById(R.id.questions_recycler);

        questionsRecycler.setAdapter(adapter);

        refreshBt.setOnClickListener(v -> {
            APIHandle();
        });

        refreshBt.setOnClickListener(v -> APIHandle());

        APIHandle();

        return view;
    }

    private void APIHandle() {
        aiRequest.sendPOST(new AIRequest.ApiCallback() {
            @Override
            public void onSuccess(String content) {
                // Важно: обновление UI должно быть в главном потоке

                // Регулярное выражение для поиска всего, что находится между <opstart> и <opend>
                // DOTALL нужен, чтобы символы перевода строки тоже учитывались
                Pattern pattern = Pattern.compile("<start>(.*?)<end>", Pattern.DOTALL);
                Matcher matcher = pattern.matcher(content);

                // Проходим по всем совпадениям и сохраняем содержимое в список
                questions.clear();
                while (matcher.find()) {
                    questions.add(matcher.group(1).trim());
                }

//                adapter = new RecyclerAdapter(questions);

                requireActivity().runOnUiThread(() -> {
                    adapter.notifyItemInserted(questions.size() - 1);
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
