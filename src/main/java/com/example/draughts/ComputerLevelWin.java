package com.example.draughts;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class ComputerLevelWin extends Activity
{
    private AlertDialog confirmationDialog;
    private String player1;//Player name

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle( getString(R.string.Exit_Application));
        builder.setMessage(getString(R.string.Are_You));
        builder.setCancelable(false);
        player1 = getIntent().getStringExtra("player1");
        builder.setPositiveButton(getString(R.string.yes), new OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {// build the dialog
                ComputerLevelWin.this.finish();
                Intent intent = new Intent(ComputerLevelWin.this, ManagementScreen.class);
                intent.putExtra("user_name",ComputerLevelWin.this.player1);
                startActivity(intent);
            }
        });

        builder.setNegativeButton(getString(R.string.no), new OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
            }
        });
        confirmationDialog = builder.create();
        this.setContentView(R.layout.computer_level_screen);
    }

    public void onClick(View view)//when click on this move the level and the name of Player
    {
        player1 = getIntent().getStringExtra("player1");
        int difficulty = Integer.parseInt(view.getTag().toString());
        Intent i = new Intent(this, GameWinAgainstBot.class);
        i.putExtra("difficulty", difficulty);
        i.putExtra("player1", player1);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent kE)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            confirmationDialog.show();
            return true;
        }
        return false;
    }
}