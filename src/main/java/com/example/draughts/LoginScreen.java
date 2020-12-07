package com.example.draughts;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LoginScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        final Button enter_btn  = findViewById(R.id.enter_btn);
        final Button exit_btn  = findViewById(R.id.exit_btn);
        final EditText name_edit =  findViewById(R.id.name_edit);

        enter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String user_name = name_edit.getText().toString();
                if (! user_name.equals(""))
                {
                    Intent intent = new Intent(LoginScreen.this, ManagementScreen.class);
                    intent.putExtra("user_name", user_name);
                    Animation animation =AnimationUtils.loadAnimation(LoginScreen.this, android.R.anim.slide_out_right);//animation of user name
                    view.startAnimation(animation);
                    startActivity(intent);
                }
                else {
                    String Error = getString(R.string.error_name);
                    Toast.makeText(LoginScreen.this, Error, Toast.LENGTH_SHORT).show();
                    Animation animation = AnimationUtils.loadAnimation(LoginScreen.this, R.anim.name_edit_anim);
                    name_edit.startAnimation(animation);
                }
            }
        });

        exit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { FinishApp(); }
        });
    }


    public void FinishApp()
    {
        final AlertDialog.Builder builder= new AlertDialog.Builder(LoginScreen.this);
        final View dialogView = getLayoutInflater().inflate(R.layout.rate_layout,null);//active rate dialog
        String con_text = getString(R.string.finish_dia);

        builder.setView(dialogView).setPositiveButton(con_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String answer = getString(R.string.bye);
                Toast.makeText(LoginScreen.this, answer, Toast.LENGTH_SHORT).show();
                finish();
            }
        }).show();

    }

    public void Action(View view) {
        Animation animation =AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        view.startAnimation(animation);
    }
}
