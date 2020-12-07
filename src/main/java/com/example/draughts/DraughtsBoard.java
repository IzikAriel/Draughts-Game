package com.example.draughts;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import com.example.draughts.DraughtsPiece.PieceType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DraughtsBoard
{
    private ArrayList<DraughtsPiece> bluePieces;
    private ArrayList<DraughtsPiece> redPieces;
    private DraughtsPiece nonePiece;//empty places
    private Rect dstRect;//Graphics
    final static int NUM_ROWS = 8;
    final static int NUM_COLS = 8;
    final static int NUM_PIECES = 12;

    public enum MOVE {//enum of all the moves
        MOVE_VALID, WRONG_NUM_STEPS, MOVING_WRONG_PIECE, MOVE_OUT_OF_BOUNDS, WRONG_DIRECTION, NOT_KILLING_MOVE;

        @NonNull
        @Override
        public String toString() {
            return MoveToString(this);
        }
    };
    private DraughtsPiece jumpPiece; // if just jumped, this is the piece that made the jump
    private PieceType turn;
    private PieceType winner;
    private ArrayList<MoveListener> moveListeners;//array of all board positions

    public void addMoveListener(MoveListener moveListener) {
        this.moveListeners.add(moveListener);
    }

    public void initVars()//init begin game
    {
        this.bluePieces = new ArrayList<DraughtsPiece>();
        this.redPieces = new ArrayList<DraughtsPiece>();
        nonePiece = new DraughtsPiece(PieceType.NON_PIECE);
        nonePiece.setPos(-1, -1);
        jumpPiece = nonePiece;
        dstRect = Commons.getBounds();
        turn = PieceType.BLUE_PIECE;
        winner = PieceType.NON_PIECE;
        moveListeners = new ArrayList<MoveListener>();
    }

    public Rect getDstRect()
    {
        return dstRect;
    }

    public void setDstRect(int left, int top, int right, int bottom)
    {
        dstRect = new Rect(left, top, right, bottom);
    }

    public DraughtsBoard()
    {
        this.initVars();
        this.boardInit();
    }

    public PieceType get_turn()
    {
        return turn;
    }

    public DraughtsBoard(DraughtsBoard board)
    {
        initVars();
        DraughtsPiece currentPiece;
        for (DraughtsPiece piece : board.redPieces)
        {
            currentPiece = new DraughtsPiece(piece);
            this.redPieces.add(currentPiece);
            if (piece == board.jumpPiece) jumpPiece = currentPiece;
        }
        for (DraughtsPiece piece : board.bluePieces)
        {
            currentPiece = new DraughtsPiece(piece);
            this.bluePieces.add(currentPiece);
            if (piece == board.jumpPiece) jumpPiece = currentPiece;
        }
        this.turn = board.turn;
    }

    @SuppressLint("ResourceType")
    private static String MoveToString(MOVE mo)
    {
        if (mo == MOVE.MOVE_VALID)
        {
            return GameWin.MOVE_VALID;
        }
        else if(mo == MOVE.MOVE_OUT_OF_BOUNDS)
        {
            return GameWin.MOVE_OUT_OF_BOUNDS;
        }
        else  if (mo == MOVE.WRONG_NUM_STEPS )
        {
            return GameWin.WRONG_NUM_STEPS;
        }
        else if (mo == MOVE.WRONG_DIRECTION)
        {
            return GameWin.WRONG_DIRECTION;
        }
        else if (mo == MOVE.MOVING_WRONG_PIECE)
        {
            return GameWin.MOVING_WRONG_PIECE;
        }
        else if  (mo == MOVE.NOT_KILLING_MOVE)
        {
            return GameWin.NOT_KILLING_MOVE;
        }
        return " ";
    }

    DraughtsPiece getPieceAt(int row, int col)//get piece in the board
    {
        for (DraughtsPiece x : this.redPieces) {
            if (x.get_position().get_row() == row && x.get_position().get_col() == col) return x;
        }
        for (DraughtsPiece x : this.bluePieces) {
            if (x.get_position().get_row() == row && x.get_position().get_col() == col) return x;
        }
        return this.nonePiece;
    }

    void boardInit()//init first board
    {
        int row = 0;
        int col = 1;
        DraughtsPiece temp;
        // add blue pieces
        for (int i = 0; i < NUM_PIECES; i++)
        {
            temp = new DraughtsPiece(PieceType.BLUE_PIECE);
            temp.setPos(row, col);
            this.bluePieces.add(temp);
            col += 2;
            if (col >= NUM_COLS)
            {
                col = (col + 1) % 2;
                row++;
            }
        }
        row = NUM_ROWS / 2 + 1;
        col = 0;
        // add red pieces
        for (int i = 0; i < NUM_PIECES; i++)
        {
            temp = new DraughtsPiece(PieceType.RED_PIECE);
            temp.setPos(row, col);
            this.redPieces.add(temp);
            col += 2;
            if (col >= NUM_COLS)
            {
                col = (col + 1) % 2;
                row++;
            }
        }
    }

    void move(DraughtsMove currentMove)//getting new move and refresh the board after this move
    {
        DraughtsPiece currentPiece = getPieceAt(currentMove.get_start().get_row(), currentMove.get_start().get_col());
        if (isMoveValid(currentMove) == MOVE.MOVE_VALID)
        {
            jumpPiece = nonePiece;
            currentPiece.setPos(currentMove.get_end().get_row(), currentMove.get_end().get_col()); // update piece position
            DraughtsPosition cStart = currentMove.get_start();
            DraughtsPosition cEnd = currentMove.get_end();
            int rowDiff = cEnd.get_row() - cStart.get_row();
            int colDiff = cEnd.get_col() - cStart.get_col();

            if (rowDiff == 2 || rowDiff == -2) // if killing a piece
            {
                DraughtsPiece cap = getPieceAt(cStart.get_row() + rowDiff / 2, cStart.get_col() + colDiff / 2); // get piece being killed
                cap.set_captured();
                if (canKill(currentPiece)) {
                    jumpPiece = currentPiece; // get another turn to kill, if can kill
                }
            }

            if (jumpPiece == nonePiece)
            {
                if(turn == PieceType.RED_PIECE)  turn = PieceType.BLUE_PIECE;
                else turn = PieceType.RED_PIECE;
            }

            if (currentPiece.getPieceType() == PieceType.RED_PIECE && cEnd.get_row() == 0)
                currentPiece.setCrowned(true);
            else if (currentPiece.getPieceType() == PieceType.BLUE_PIECE && cEnd.get_row() == NUM_ROWS - 1) currentPiece.setCrowned(true);
            for (MoveListener moveListener : moveListeners) {
                moveListener.onMove(currentMove);
            }
        }
    }

    public ArrayList<DraughtsPiece> getPieces(PieceType pieceType)
    {
        DraughtsPiece currentPiece;
        ArrayList<DraughtsPiece> toReturn = new ArrayList<DraughtsPiece>();
        for (int i = 0; i < NUM_ROWS; i++)
        {
            for (int j = 0; j < NUM_COLS; j++)
            {
                currentPiece = getPieceAt(i, j);
                if (!currentPiece.is_none_piece() && pieceType == currentPiece.getPieceType())
                {
                    toReturn.add(currentPiece);
                }
            }
        }
        return toReturn;
    }

    public int getPieceCount(PieceType pieceType) // get the amount of current piece type
    {
        int count = 0;
        ArrayList<DraughtsPiece> pieces;
        if (pieceType == PieceType.RED_PIECE) pieces = this.redPieces;
        else pieces = this.bluePieces;
        for (DraughtsPiece cp : pieces)
        {
            if (!cp.is_none_piece() && !cp.is_captured()) count++;
        }
        return count;
    }

    public PieceType getWinner()//get the winner of the game
    {
        boolean atleastOneLightPiece = false;
        boolean atleastOneDarkPiece = false;
        if (getPieceCount(PieceType.BLUE_PIECE) > 0) atleastOneLightPiece = true;
        if (getPieceCount(PieceType.RED_PIECE) > 0) atleastOneDarkPiece = true;
        if (atleastOneDarkPiece && !atleastOneLightPiece || winner == PieceType.RED_PIECE) return PieceType.RED_PIECE;
        else if (atleastOneLightPiece && !atleastOneDarkPiece || winner == PieceType.BLUE_PIECE) return PieceType.BLUE_PIECE;
        else return PieceType.NON_PIECE;
    }

    public int getNumCrowned(PieceType pieceType)
    {
        int count = 0;
        ArrayList<DraughtsPiece> pieces;
        if (pieceType == PieceType.RED_PIECE) pieces = this.redPieces;
        else pieces = this.bluePieces;
        for (DraughtsPiece cp : pieces)
        {
            if (!cp.is_none_piece() && !cp.is_captured()) count++;
        }
        return count;
    }

    public boolean canKill(DraughtsPiece currentPiece)//getting place and kill the other enemy
    {
        DraughtsPosition cpos;
        DraughtsMove move = new DraughtsMove();
        move.setStart(currentPiece.get_position());
        if ((currentPiece.getPieceType() == PieceType.BLUE_PIECE || currentPiece.is_crowned()) && currentPiece.get_position().get_row() + 2 < NUM_ROWS) {
            if(currentPiece.get_position().get_col() + 2 < NUM_COLS) {
                cpos = new DraughtsPosition(currentPiece.get_position().get_row() + 2, currentPiece.get_position().get_col() + 2);
                move.setEnd(cpos);
                if (isKillingMove(move))
                    return true;
            }
            if (currentPiece.get_position().get_col() - 2 >= 0) {
                cpos = new DraughtsPosition(currentPiece.get_position().get_row() + 2, currentPiece.get_position().get_col() - 2);
                move.setEnd(cpos);
                if (isKillingMove(move))
                    return true;
            }
        }
        if ((currentPiece.getPieceType() == PieceType.RED_PIECE || currentPiece.is_crowned()) && currentPiece.get_position().get_row() - 2 >= 0) {
            if (currentPiece.get_position().get_col() + 2 < NUM_COLS) {
                cpos = new DraughtsPosition(currentPiece.get_position().get_row() - 2, currentPiece.get_position().get_col() + 2);
                move.setEnd(cpos);
                if (isKillingMove(move))
                    return true;
            }
            if (currentPiece.get_position().get_col() - 2 >= 0) {
                cpos = new DraughtsPosition(currentPiece.get_position().get_row() - 2, currentPiece.get_position().get_col() - 2);
                move.setEnd(cpos);
                if (isKillingMove(move))
                    return true;
            }
        }
        return false;
    }

    public boolean isKillingMove(DraughtsMove move)//check if the current move is kill one
    {
        DraughtsPiece endPiece = getPieceAt(move.get_end().get_row(), move.get_end().get_col());
        int dCol = move.get_end().get_col() - move.get_start().get_col();
        int dRow = move.get_end().get_row() - move.get_start().get_row();
        if (Math.abs(dCol) != 2 || Math.abs(dRow) != 2) return false;
        int midR = move.get_start().get_row() + dRow / 2;
        int midC = move.get_start().get_col() + dCol / 2;
        DraughtsPiece midPiece = getPieceAt(midR, midC); // get the piece that's one step away
        return (midPiece.getPieceType() != turn && midPiece.getPieceType() != PieceType.NON_PIECE && endPiece.getPieceType() == PieceType.NON_PIECE );
    }

    public MOVE isMoveValid(DraughtsMove move) // check if the current move is valid
    {
        DraughtsPiece startPiece = getPieceAt(move.get_start().get_row(), move.get_start().get_col());
        DraughtsPiece endPiece = getPieceAt(move.get_end().get_row(), move.get_end().get_col());
        DraughtsPosition[] poss = {move.get_start(), move.get_end()};
        for (DraughtsPosition pos : poss) {
            if (pos.get_row() < 0 || pos.get_row() >= NUM_ROWS ||pos.get_col() < 0 || pos.get_col() >= NUM_COLS)
                return  MOVE.MOVE_OUT_OF_BOUNDS;
        }

        if ((startPiece.getPieceType() != turn) || endPiece.getPieceType() != PieceType.NON_PIECE)
            return MOVE.MOVING_WRONG_PIECE; // moving wrong piece or moving on top of a piece
        else if (jumpPiece.getPieceType() != PieceType.NON_PIECE &&  startPiece != jumpPiece)
            return  MOVE.MOVING_WRONG_PIECE; // moving wrong piece
        else {
            int dCol = move.get_end().get_col() - move.get_start().get_col();
            int dRow = move.get_end().get_row() - move.get_start().get_row();
            if (!startPiece.is_crowned() &&
               ((startPiece.getPieceType() == PieceType.RED_PIECE && dRow > 0) ||
               (startPiece.getPieceType() == PieceType.BLUE_PIECE && dRow < 0)))
                     return  MOVE.WRONG_DIRECTION; // moving in wrong direction

            boolean playerCankill = false;
            List<DraughtsPiece> pieces;
            if (jumpPiece.getPieceType() != PieceType.NON_PIECE) pieces = Arrays.asList(jumpPiece);
            else pieces = getPieces(startPiece.getPieceType());
            for (DraughtsPiece piece : pieces) {
                playerCankill = playerCankill || canKill(piece);
            }
            if (playerCankill) {
                if (!isKillingMove(move)) return MOVE.NOT_KILLING_MOVE; // isKillingMove also checks if the number of steps taken was 2
            } else {
                if (Math.abs(dRow) != 1 || Math.abs(dCol) != 1) return MOVE.WRONG_NUM_STEPS;
            }
        }
        return MOVE.MOVE_VALID;
    }
}