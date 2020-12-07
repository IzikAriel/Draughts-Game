package com.example.draughts;

import android.util.Pair;
import com.example.draughts.DraughtsPiece.PieceType;
import java.util.ArrayList;

public class BotPlayer
{
    DraughtsBoard board;
    int numberOfTurns;//Number of moves to predict

    BotPlayer(DraughtsBoard board, int maxTurns)
    {
        this.board = board;
        this.numberOfTurns = maxTurns;
    }

    public DraughtsMove getBestMove() {
        return getBestMoveScore(this.board, 0, numberOfTurns).second;
    }

    private Pair<Integer, DraughtsMove> getBestMoveScore(DraughtsBoard board, int turnNo, int maxTurns) {
        int score;
        DraughtsMove move = null;

        if (turnNo < maxTurns) {
            ArrayList<DraughtsPiece> pieces = board.getPieces(board.get_turn());
            DraughtsMove currentMove = new DraughtsMove();
            DraughtsPosition pos = new DraughtsPosition();
            int rowDir;
            boolean toMaximize;
            if (board.get_turn() == PieceType.RED_PIECE) {//if predict computer move
                score = Integer.MIN_VALUE;
                toMaximize = true;
                rowDir = -1;
            } else {//if predict player move
                score = Integer.MAX_VALUE;
                toMaximize = false;
                rowDir = 1;
            }
            for (DraughtsPiece piece : pieces) {
                pos.setRow(piece.get_position().get_row());
                pos.setCol(piece.get_position().get_col());
                currentMove.setStart(pos);
                ArrayList<Integer> muls = new ArrayList<Integer>();
                int[] colIncs = {1, -1, 2, -2};// all the option to move
                int[] rowIncs = {rowDir, rowDir, rowDir * 2, rowDir * 2};//direction of the move
                muls.add(1);
                if (piece.is_crowned()) muls.add(-1); // if piece is crowned try moves in the opposite direction too
                for (int mul : muls) {
                    for (int i = 0; i < colIncs.length; i++) {
                        currentMove.setEnd(new DraughtsPosition(pos.get_row() + mul * rowIncs[i], pos.get_col() + colIncs[i])); // to move diagonally one step
                        if (board.isMoveValid(currentMove) == DraughtsBoard.MOVE.MOVE_VALID) {
                            Pair<Integer, DraughtsMove> tempMoveScore = getBestMoveScore(doMove(board, currentMove), turnNo + 1, maxTurns);// copy the board after the move and do again
                            if ((tempMoveScore.first > score && toMaximize) || (tempMoveScore.first < score && !toMaximize)) {// find highest score and the best move
                                score = tempMoveScore.first;
                                move = new DraughtsMove(currentMove); // copy move
                            }
                        }
                    }
                }
            }
        } else {
    score = EvaluateScore(board);
}

        return new Pair<Integer, DraughtsMove>(score, move);
    }

    // returns the board that results by performing move  on board
    private DraughtsBoard doMove(DraughtsBoard board, DraughtsMove currentMove)
    {
        DraughtsBoard boardNext = new DraughtsBoard(board); // make a copy of the old board
        boardNext.move(currentMove); // perform move
        return boardNext;
    }

    //  a state score based on the number of pieces of each color
    private int EvaluateScore(DraughtsBoard board)
    {
        int score2 = 0;
        score2 += board.getPieceCount(PieceType.RED_PIECE);
        score2 -= board.getPieceCount(PieceType.BLUE_PIECE);
        score2 += 2 * board.getNumCrowned(PieceType.RED_PIECE);
        score2 -= 2 * board.getNumCrowned(PieceType.BLUE_PIECE);
        return score2;
    }
}