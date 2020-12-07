package com.example.draughts;

import javax.security.auth.callback.Callback;

public interface MoveListener extends Callback {

    void onMove(DraughtsMove currentMove);
}