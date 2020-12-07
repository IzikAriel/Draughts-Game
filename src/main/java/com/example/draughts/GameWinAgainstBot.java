package com.example.draughts;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class GameWinAgainstBot extends GameWin {

    int difficulty;//level
    BotPlayer BotPlayer;
    public  TextView player1, computer,level;
    SharedPreferences sp;//dataBase
    long curTime;//time

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        LayoutInflater l = getLayoutInflater();
        View root = l.inflate(R.layout.game_win, null);
        view = root.findViewById(R.id.renderView);
        move = null;
        player1 = root.findViewById(R.id.player1);
        computer = root.findViewById(R.id.player2);
        level = root.findViewById(R.id.level);
        level.setVisibility(View.VISIBLE);
        board = new DraughtsBoard();
        difficulty = this.getIntent().getIntExtra("difficulty", -1);
        player1.setText(this.getIntent().getStringExtra("player1"));
        computer.setText(getString(R.string.com));
        view.setGameWin(this);
        view.setOnTouchListener(this);
        view.getHolder().addCallback(this);
        this.setContentView(root);
        simpleChronometer = findViewById(R.id.simpleChronometer); // initiate a chronometer
        simpleChronometer.setVisibility(View.VISIBLE);//visible  the timer
        checkLevel();
        initMediaPlayer();
        SoundController();
        SetMessage();

    }
    private void checkLevel()//change the level to label
    {
      if(this.difficulty ==2)
          level.setText(R.string.easy_level);
      else if(this.difficulty==3)
          level.setText(R.string.medium_level);
      else level.setText(R.string.difficult_level);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {//when touch on the board
        if (MOVEMENT_THRESHOLD == -1) {
            MOVEMENT_THRESHOLD = Commons.getBounds().width() / DraughtsBoard.NUM_COLS / 2;  // moved only if moved greater than half the width of a block
        }
        if (view == this.view) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                x = (int) event.getX();
                y = (int) event.getY();
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (Math.sqrt(Math.pow(x - event.getX(), 2) + Math.pow(y - event.getY(), 2)) < MOVEMENT_THRESHOLD) {
                    int col = (int) (event.getX() / (board.getDstRect().width() / DraughtsBoard.NUM_COLS));
                    int row = (int) ((event.getY() - Commons.getBounds().top) / (board.getDstRect().height() / DraughtsBoard.NUM_ROWS));
                    if (move == null) {//first touch on board
                        if (!board.getPieceAt(row, col).is_none_piece()) {
                            move = new DraughtsMove();
                            move.setStart(new DraughtsPosition(row, col));
                        }
                    } else {//second touch one board in same turn
                        move.setEnd(new DraughtsPosition(row, col));
                        DraughtsBoard.MOVE moveValidity = board.isMoveValid(move);
                        if (moveValidity == DraughtsBoard.MOVE.MOVE_VALID) {
                            board.move(move); // perform move
                            endGame(board.getWinner(), player1.getText().toString(), computer.getText().toString()); // checks for game end conditions and takes appropriate actions

                            while (board.get_turn() == DraughtsPiece.PieceType.RED_PIECE) {//computer  turn
                                BotPlayer = new BotPlayer(board, difficulty);
                                DraughtsMove currentMove = BotPlayer.getBestMove();//get the move of computer
                                if (currentMove == null) {
                                    endGame(DraughtsPiece.PieceType.BLUE_PIECE, player1.getText().toString(), computer.getText().toString());
                                    break;
                                } else {
                                    board.move(currentMove);
                                    endGame(board.getWinner(), player1.getText().toString(), computer.getText().toString());
                                }
                            }
                        } else {
                           Toast toast = Toast.makeText(this.getApplicationContext(), moveValidity.toString(),Toast.LENGTH_SHORT);//show when you do wrong move
                            toast.show();
                        }
                        move = null;
                    }
                }
            }
            return true;
        }
        return false;
    }

    public void endGame(DraughtsPiece.PieceType winner, final String player1, String player2) {// when the game is end
        String msg = getString(R.string.winner);
        if (winner == DraughtsPiece.PieceType.NON_PIECE) return;
        if (winner == DraughtsPiece.PieceType.RED_PIECE) msg = player2 + " " + msg;
        else msg = player1 + " " + msg;
        if (winner == DraughtsPiece.PieceType.BLUE_PIECE && ManagementScreen.mode == ManagementScreen.Mode.BOT) {//same the score ,because  player is won
            SaveWinner(new Winner(player1, Integer.toString(difficulty), simpleChronometer.getText().toString()));
            curTime = SystemClock.elapsedRealtime();
            simpleChronometer.stop();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.Game_End));
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {//create new game

                Intent intent = new Intent(GameWinAgainstBot.this, GameWinAgainstBot.class);
                intent.putExtra("player1",GameWinAgainstBot.this.player1.getText());
                intent.putExtra("difficulty", GameWinAgainstBot.this.difficulty);
                startActivity(intent);
                dialog.cancel();
                GameWinAgainstBot.this.finish();
            }
        });
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                GameWinAgainstBot.this.onBackPressed();
            }
        });
        builder.create();
        builder.show();
    }

    public void SaveWinner(Winner winner ) {//save the score in DataBase
        sp = getSharedPreferences(winner.level, MODE_PRIVATE);
        SharedPreferences.Editor edit;
        edit = sp.edit();
        String curTime = sp.getString(winner.user_name, "Error");
        if (curTime.equals("Error")) {
            edit.putString(winner.user_name, winner.time);
            edit.commit();

        } else if (TimeCompare(curTime, winner.time) != -1) {
            edit.putString(winner.user_name, winner.time);
            edit.commit();
        }

    }
    public  int TimeCompare(String t1 , String t2)//compare the time in hours and min
    {
        String pattern = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            Date date1 = sdf.parse(t1);
            Date date2 = sdf.parse(t2);
            int i = date1.compareTo(date2);
            return i;
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        return 0;
    }
    @Override
    public void onPause() {
        super.onPause();
        curTime = SystemClock.elapsedRealtime();
        simpleChronometer.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(curTime!=0)
        {
            simpleChronometer.setBase(simpleChronometer.getBase() +SystemClock.elapsedRealtime() - curTime);
        }
        else
        {
            simpleChronometer.setBase(SystemClock.elapsedRealtime());
        }
        simpleChronometer.start();
    }

}