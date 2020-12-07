package com.example.draughts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import java.io.IOException;
import javax.security.auth.callback.Callback;

public abstract class GameWin extends Activity implements Callback, View.OnTouchListener, SurfaceHolder.Callback {

    protected DraughtsBoard board;
    protected DraughtsMove move;
    protected MediaPlayer mediaPlayer = null;//Media
    protected static RenderView view;
    protected Chronometer simpleChronometer;// Timer
    protected int MOVEMENT_THRESHOLD = -1;
    protected int  x, y;
    protected Button button_sound;//sound
    int button_pos = 1;
    protected static String  MOVE_VALID, WRONG_NUM_STEPS, MOVING_WRONG_PIECE, MOVE_OUT_OF_BOUNDS, WRONG_DIRECTION, NOT_KILLING_MOVE ;

    public DraughtsBoard getBoard()
    {
        return board;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (view != null) view.pause();
        if (mediaPlayer != null) mediaPlayer.pause();
        if (this.isFinishing()) mediaPlayer.release();
    }

    protected void initMediaPlayer()
    {
        AssetManager manager;
        AssetFileDescriptor descriptor;
        try
        {
            this.setVolumeControlStream(android.media.AudioManager.STREAM_MUSIC);
            manager = this.getAssets();
            descriptor = manager.openFd("music/gamemusic.mp3");
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
            descriptor.close();
        }
        catch ( IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            descriptor = null;
            manager = null;
        }
    }
    public void SoundController()//controlling the sound
    {
       button_sound = findViewById(R.id.sound);
       button_sound.setBackground(getResources().getDrawable(R.drawable.on));
       button_sound.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("UseCompatLoadingForDrawables")
            public void onClick(View v) {
                if (button_pos == 1) {
                    button_sound.setBackground(getResources().getDrawable(R.drawable.off));
                    if (mediaPlayer != null) mediaPlayer.pause();
                    button_pos = 0;
                } else if (button_pos == 0) {
                    button_sound.setBackground(getResources().getDrawable(R.drawable.on));
                    if (mediaPlayer != null) mediaPlayer.start();
                    button_pos = 1;
                }
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (button_pos == 0) {
            mediaPlayer.pause();
        }
        else mediaPlayer.start();
//        if (mediaPlayer != null) mediaPlayer.start();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }

    public void surfaceCreated(SurfaceHolder holder) { view.resume(); }

    public void surfaceDestroyed(SurfaceHolder holder) {  }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {  }

    public void SetMessage()
    {
        MOVE_VALID =  getString(R.string.MOVE_VALID);
        WRONG_NUM_STEPS = getString(R.string.WRONG_NUM_STEPS);
        MOVING_WRONG_PIECE =  getString(R.string.MOVING_WRONG_PIECE);
        MOVE_OUT_OF_BOUNDS =  getString(R.string.MOVE_OUT_OF_BOUNDS);
        WRONG_DIRECTION = getString(R.string.WRONG_DIRECTION);
        NOT_KILLING_MOVE =  getString(R.string.NOT_KILLING_MOVE);
    }
}
