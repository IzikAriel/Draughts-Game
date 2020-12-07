package com.example.draughts;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class GameWinAgainstUser extends GameWin {
    TextView player1;
    TextView player2;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        SetMessage();
        move = null;
        board = new DraughtsBoard();
        LayoutInflater l = getLayoutInflater();
        View root = l.inflate(R.layout.game_win, null);
        view = root.findViewById(R.id.renderView);
        player1 = root.findViewById(R.id.player1);
        player2 = root.findViewById(R.id.player2);
        player1.setText(getIntent().getStringExtra("player1"));
        player2.setText(getIntent().getStringExtra("player2"));
        view.setGameWin(this);
        view.setOnTouchListener(this);
        view.getHolder().addCallback(this);
        this.setContentView(root);
        initMediaPlayer();
        SoundController();
        simpleChronometer = findViewById(R.id.simpleChronometer); // initiate a chronometer
        simpleChronometer.setVisibility(View.INVISIBLE);

    }

    @Override
    public boolean onTouch(View view, MotionEvent event)//active every time that touch in board
    {
        if (MOVEMENT_THRESHOLD == -1)
        {
            MOVEMENT_THRESHOLD = Commons.getBounds().width() / DraughtsBoard.NUM_COLS / 2;  // moved only if moved greater than half the width of a block
        }
        if (view == this.view)
        {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
            {
                x = (int) event.getX();
                y = (int) event.getY();
            }
            if (event.getAction() == MotionEvent.ACTION_UP)
            {
                if (Math.sqrt(Math.pow(x - event.getX(), 2) + Math.pow(y - event.getY(), 2)) < MOVEMENT_THRESHOLD)
                {
                    int col = (int) (event.getX() / (board.getDstRect().width() / DraughtsBoard.NUM_COLS));
                    int row = (int) ((event.getY() - Commons.getBounds().top) / (board.getDstRect().height() / DraughtsBoard.NUM_ROWS));
                    if (move == null)//first touch in board
                    {
                        if (!board.getPieceAt(row, col).is_none_piece())
                        {
                            move = new DraughtsMove();
                            move.setStart(new DraughtsPosition(row, col));
                        }
                    }
                    else//second touch in same turn
                    {
                        move.setEnd(new DraughtsPosition(row, col));
                        DraughtsBoard.MOVE moveValidity = board.isMoveValid(move);
                        if (moveValidity == DraughtsBoard.MOVE.MOVE_VALID)
                        {
                            board.move(move); // perform move
                            endGame(board.getWinner(),player1.getText().toString(),player2.getText().toString()); // checks for game end conditions and takes appropriate actions
                            if(board.get_turn()== DraughtsPiece.PieceType.RED_PIECE)//change the turn
                                Toast.makeText(this.getApplicationContext(), getString(R.string.red_turn), Toast.LENGTH_SHORT).show();
                            else Toast.makeText(this.getApplicationContext(), getString(R.string.blue_turn), Toast.LENGTH_SHORT).show();

                        }
                        else//if you do wrong move this show
                        {
                           Toast.makeText(this.getApplicationContext(), moveValidity.toString(),Toast.LENGTH_SHORT).show();
                        }
                        move = null;
                    }
                }
            }
            return true;
        }

        return false;
    }
    public void endGame(DraughtsPiece.PieceType winner,String player1,String player2)//finish the game
    {
        String msg = getString(R.string.winner);
        if (winner == DraughtsPiece.PieceType.NON_PIECE) return;
        if (winner == DraughtsPiece.PieceType.RED_PIECE) msg = player2 + " "+ msg;
        else msg = player1 + " " + msg;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.Game_End));
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id) {//create a new game with same players

                Intent intent = new Intent(GameWinAgainstUser.this, GameWinAgainstUser.class);
                intent.putExtra("player1",GameWinAgainstUser.this.player1.getText());
                intent.putExtra("player2",GameWinAgainstUser.this.player2.getText());
                startActivity(intent);
                dialog.cancel();
                finish();
            }
        });
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
                GameWinAgainstUser.this.onBackPressed();
            }
        });
        builder.create();
        builder.show();
    }
}
