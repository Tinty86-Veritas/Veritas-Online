package com.veritas.veritas;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private AppCompatButton presets_option_button;
    private AppCompatButton direct_ai_using_option_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presets_option_button = findViewById(R.id.presets_option_button);
        direct_ai_using_option_button = findViewById(R.id.direct_ai_using_option_button);

        presets_option_button.setOnClickListener(view -> {

        });
    }
}