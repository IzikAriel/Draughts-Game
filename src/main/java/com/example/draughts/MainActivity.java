package com.example.draughts;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    int progressStatus = 0;
    Handler handler;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ImageView sampleImageView = findViewById(R.id.main_image);
        Animation animation;
        animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_out);
        sampleImageView.startAnimation(animation);
        handler =new Handler();
        progressBar = findViewById(R.id.determinateBar);
        SetProgressBar();
    }
    private void  SetProgressBar()
    {
        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100)
                {
                    progressStatus += 5;
                    // Update the progress bar and display the
                    //current value in the text view
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);
                        }
                    });
                    try {
                        // Sleep for 200 milliseconds.
                        Thread.sleep(200);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Intent intent = new Intent(MainActivity.this, LoginScreen.class);
                startActivity(intent);
                finish();
            }
        }).start();
    }
}