package com.example.draughts;

public class DraughtsPiece
{
    public enum PieceType {
        BLUE_PIECE, RED_PIECE, NON_PIECE
    };

    private boolean crowned, captured;//captured is place that was kill there
    private PieceType pieceType;
    private DraughtsPosition pos;

    public DraughtsPiece(PieceType pieceType)
    {
        pos = new DraughtsPosition();
        piece_init();
        this.pieceType = pieceType;
    }

    public DraughtsPiece(DraughtsPiece currentPiece)
    {
        pos = new DraughtsPosition();
        this.setPos(currentPiece.get_position().get_row(), currentPiece.get_position().get_col());
        if (currentPiece.is_captured()) this.set_captured();
        else this.captured = false;
        this.setCrowned(currentPiece.is_crowned());
        this.pieceType = currentPiece.pieceType;
    }

    private void piece_init()
    {
        this.crowned = false;
        this.captured = false;
        this.pos.setRow(0);
        this.pos.setRow(0);
    }

    public void setPos(int row, int col)
    {
        this.pos.setCol(col);
        this.pos.setRow(row);
    }

    boolean is_none_piece()//check if the current piece is empty piece
    {
        if (this.pos.get_row()==-1 || this.pos.get_col()==-1||this.pieceType== PieceType.NON_PIECE )
            return true;
        else return false;
    }

    boolean is_captured()
    {
        return this.captured;
    }

    boolean is_crowned()
    {
        return this.crowned;
    }

    PieceType getPieceType() {return this.pieceType;}

    DraughtsPosition get_position()
    {
        return this.pos;
    }

    void set_captured()//set piece after kill in the place
    {
        setPos(-1, -1);
        this.pieceType = PieceType.NON_PIECE;
        captured = true;
    }

    void setCrowned(boolean val)
    {
        crowned = val;
    }
}