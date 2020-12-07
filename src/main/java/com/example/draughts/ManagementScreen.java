package com.example.draughts;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ManagementScreen extends AppCompatActivity {
    String player1,level;
    static Mode mode = Mode.BOT;
    ImageView imageTime ;//sun or moon image
    Handler handler;
    TextView name_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.management_screen);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        name_text = findViewById(R.id.name_text);
        player1 = getIntent().getStringExtra("user_name");
        String  message = getString(R.string.hello_text) + " " + player1 ;
        name_text.setText(message);
        Animation animation;
        animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.user_name_anim);//animation of user name
        name_text.startAnimation(animation);
        handler = new Handler();
        imageTime  = findViewById(R.id.day_view);

        SetImageByTime();
        SetAnimForSun();
        AnimToPlay();

    }

    public void MoveToRecordTable(View view) {// go to record table

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View dialogView = getLayoutInflater().inflate(R.layout.choose_level_to_table, null);
        Button easy = dialogView.findViewById(R.id.easy);
        Button medium = dialogView.findViewById(R.id.medium);
        Button difficult = dialogView.findViewById(R.id.difficult);
        Button[] Buttons = {easy, medium, difficult};
        for (int loc = 0; loc < 3; loc++) {
            Buttons[loc].setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View view) {
                    level = (view.getTag().toString());
                    Intent intent = new Intent(ManagementScreen.this, RecordTableScreen.class);
                    intent.putExtra("level", level);
                    startActivity(intent);
                }
            });
        }
        builder.setView(dialogView);
        builder.show();
    }

    public void ActionModeUser(View view) {//go to mode vs other user

        final AlertDialog.Builder builder= new AlertDialog.Builder(this);
        final View dialogView = getLayoutInflater().inflate(R.layout.select_mode_dialog,null);
        final EditText second = dialogView.findViewById(R.id.second_user_name);
        String con_text = getString(R.string.continue_key);
        mode = Mode.USER;

        builder.setView(dialogView).setPositiveButton(con_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                String player2 = second.getText().toString();
                if(!player2.equals(""))
                {
                    Intent intent = new Intent(ManagementScreen.this, GameWinAgainstUser.class);
                    intent.putExtra("player1",player1);
                    intent.putExtra("player2",player2);
                    startActivity(intent);
                    finish();
                }
                else {
                    String error_name_dialog = getString(R.string.error_name_dialog);
                    Toast.makeText(ManagementScreen.this, error_name_dialog, Toast.LENGTH_SHORT).show();
                }
            }


        }).show();
    }

    public void ActionModeBot(View view) {// go to mode vs computer

        mode = Mode.BOT;
        Intent intent = new Intent(this, ComputerLevelWin.class);
        intent.putExtra("player1",player1);
        startActivity(intent);
        finish();
    }

    public enum Mode {
        BOT,USER
    }
    public void AnimToPlay()// animate arrow to play
    {

        final ImageView dir_img = findViewById(R.id.arrow_image);
        final String let_text = getString(R.string.le);
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    public void run() {
                        dir_img.setVisibility(View.VISIBLE);
                        Animation animation;
                        animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.arrow_anim);
                        dir_img.startAnimation(animation);
                        name_text.setText(player1 +" "+ let_text);
                    }
                });
            }
        },10000);
    }


    private void SetImageByTime()// chose which image will be sun or moon
    {
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

            Date EndTime = dateFormat.parse("20:00");
            Date StartTime = dateFormat.parse("06:00");
            Date CurrentTime = dateFormat.parse(dateFormat.format(new Date()));

            if (CurrentTime.after(EndTime) || CurrentTime.before(StartTime))
            {
                imageTime.setImageResource((R.drawable.moon));
            }
        }
        catch (Exception e)
        {

        }
    }
    public void SetAnimForSun()
    {
        Drawable.ConstantState cs1 = imageTime.getDrawable().getConstantState();
        Drawable.ConstantState cs2 = getResources().getDrawable(R.drawable.sunny).getConstantState();
        if ( cs1 == cs2) {
            Animation animation;
            animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.sun_anim);
            imageTime.startAnimation(animation);
        }
    }
}
