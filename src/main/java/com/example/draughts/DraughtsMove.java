package com.example.draughts;

public class DraughtsMove
{
    private DraughtsPosition start, end;//start position and end position of move

    public void setStart(DraughtsPosition start)
    {
        this.start = start;
    }

    public void setEnd(DraughtsPosition end)
    {
        this.end = end;
    }

    public void initVars()
    {
        start = new DraughtsPosition();
        end = new DraughtsPosition();
    }

    public DraughtsMove()
    {
        initVars();
        start.setRow(-1);
        start.setCol(-1);
        end.setRow(-1);
        end.setCol(-1);
    }

    public DraughtsMove(DraughtsMove currentMove)
    {
        initVars();
        start.setRow(currentMove.get_start().get_row());
        start.setCol(currentMove.get_start().get_col());
        end.setRow(currentMove.get_end().get_row());
        end.setCol(currentMove.get_end().get_col());
    }

    public DraughtsPosition get_start()
    {
        return start;
    }

    public DraughtsPosition get_end()
    {
        return end;
    }
}
