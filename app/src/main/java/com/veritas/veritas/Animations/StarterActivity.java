package com.veritas.veritas.Animations;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.veritas.veritas.MainActivity;
import com.veritas.veritas.R;

public class StarterActivity extends AppCompatActivity {

    // Флаг, указывающий, завершилась ли загрузка приложения
    private boolean isAppLoaded = false;
    // Таймаут, чтобы не ждать бесконечно (например, 10 секунд)
    private static final long MAX_WAIT_TIME = 10000;
    private final Handler handler = new Handler();

    // Руннабл для перехода в MainActivity
    private final Runnable startMainActivityRunnable = new Runnable() {
        @Override
        public void run() {
            proceedToMain();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.starter_activity);

        final VideoView starterAnimation = findViewById(R.id.starter_animation);
        final ImageView staticImage = findViewById(R.id.starter_image);

        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.starter_animaton_mp4;
        Uri uri = Uri.parse(videoPath);
        starterAnimation.setVideoURI(uri);

        starterAnimation.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (isAppLoaded) {
                    proceedToMain();
                } else {
                    // Если нет, показываем статичное изображение и ждем загрузки
                    staticImage.setVisibility(ImageView.VISIBLE);
                    handler.postDelayed(startMainActivityRunnable, MAX_WAIT_TIME);
                }
            }
        });

        starterAnimation.start();
        startAppLoading();
    }

    /**
     * Имитация процесса загрузки приложения.
     * Здесь можно запускать асинхронную задачу, получать данные, инициализировать модули и т.д.
     */

    private void startAppLoading() {
        // Пример: имитация загрузки через задержку (например, 5 секунд)
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isAppLoaded = true;
                // Если статичная картинка уже видна, переходим в MainActivity
                if (findViewById(R.id.starter_image).getVisibility() == View.VISIBLE) {
                    proceedToMain();
                }
            }
        }, 5000);
    }

    /**
     * Переход к MainActivity и завершение SplashActivity.
     */
    private void proceedToMain() {
        // Убираем все отложенные задачи, чтобы не было повторного запуска
        handler.removeCallbacks(startMainActivityRunnable);
        startActivity(new Intent(StarterActivity.this, MainActivity.class));
        finish();
    }
}
