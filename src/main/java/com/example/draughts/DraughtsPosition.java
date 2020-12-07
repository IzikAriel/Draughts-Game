package com.example.draughts;

public class DraughtsPosition
{
    private int row, col;

    public void setRow(int r)
    {
        row = r;
    }

    public void setCol(int c){ col = c; }

    public DraughtsPosition(int r, int c)
    {
        row = r;
        col = c;
    }

    public DraughtsPosition()
    {
        row = -1;
        col = -1;
    }

    int get_row()
    {
        return this.row;
    }
    int get_col()
    {
        return this.col;
    }
}