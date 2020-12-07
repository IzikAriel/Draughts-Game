package com.example.draughts;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RenderView extends SurfaceView
{
    private Bitmap board;
    public Bitmap redPiece;
    public Bitmap bluePiece;
    public Bitmap redCrowned;
    public Bitmap blueCrowned;
    private SurfaceHolder holder;
    private boolean init;
    private double baseLength;
    ScheduledThreadPoolExecutor step;// step for delay
    GameWin gameWin;
    public DrawableDraughtsPiece[][] drawableDraughtsPieces;// draw the piece in the array
    private class DrawableDraughtsPiece implements MoveListener{

        private DraughtsPiece currentPiece;
        // for animation
        private Queue<Float> velX, velY; // we'll have a queue of velocities, corresponding to the pending animations (in-case more than one move is performed suddenly)
        private final int timeSteps = 8;
        private int currTimeStep;
        private Rect dstRect;
        int lastRow, lastCol;

        public DrawableDraughtsPiece(DraughtsPiece currentPiece) {
            this.currentPiece = currentPiece;
            dstRect = new Rect();
            velX = new ArrayDeque<Float>();
            velY = new ArrayDeque<Float>();
            currTimeStep = 0;
            lastRow = currentPiece.get_position().get_row();
            lastCol = currentPiece.get_position().get_col();
            gameWin.getBoard().addMoveListener(this);
        }

        private void updateDstRect(){
            Rect bounds = Commons.getBounds();
            DraughtsPosition pos = currentPiece.get_position();
            // if piece has changed position
            if (lastRow != pos.get_row() || lastCol != pos.get_col()) {
                float sX = (pos.get_col() - lastCol) * (bounds.right - bounds.left) / DraughtsBoard.NUM_COLS ;
                float sY = (pos.get_row() - lastRow) * (bounds.bottom - bounds.top) / DraughtsBoard.NUM_ROWS ;
                this.velX.add(sX/ timeSteps);
                this.velY.add(sY/ timeSteps);
                lastRow = pos.get_row();
                lastCol = pos.get_col();
            }
            // animate piece to it's position
            if (currTimeStep >= timeSteps) {
                if (!velX.isEmpty()) velX.remove();
                if (!velY.isEmpty()) velY.remove();
                currTimeStep = 0;
            }
            if (velX.isEmpty()) {
                dstRect.left = (int) ((bounds.right - bounds.left) / DraughtsBoard.NUM_COLS * pos.get_col());
                dstRect.right = (int) ((int) (dstRect.left + (bounds.right - bounds.left) / ((float) DraughtsBoard.NUM_COLS) ));
                dstRect.top = (int) ((bounds.bottom - bounds.top) / DraughtsBoard.NUM_ROWS * (pos.get_row()) + bounds.top);
                dstRect.bottom = (int) ((int) (dstRect.top + (bounds.bottom - bounds.top) / ((float) DraughtsBoard.NUM_ROWS) ));
            } else {
                currTimeStep += 1;
                dstRect.left += velX.peek();
                dstRect.right += velX.peek();
                dstRect.top += velY.peek();
                dstRect.bottom += velY.peek();
            }
        }

        public Rect GetDstRect()
        {
            updateDstRect();
            return dstRect;
        }

        @Override
        public void onMove(DraughtsMove currentMove) {
            DraughtsPiece currentPiece = gameWin.getBoard().getPieceAt(currentMove.get_end().get_row(), currentMove.get_end().get_col());
            if (currentPiece == this.currentPiece) updateDstRect();
        }
    }

    public void setGameWin(GameWin gw)
    {
        if (ManagementScreen.mode == ManagementScreen.Mode.BOT)
        {
            this.gameWin = (GameWinAgainstBot) gw;
        }
        else this.gameWin = (GameWinAgainstUser) gw;

        for (int i = 0; i < DraughtsBoard.NUM_ROWS; i++) {
            for (int j = 0; j < DraughtsBoard.NUM_COLS; j++) {
                drawableDraughtsPieces[i][j] = new DrawableDraughtsPiece(gw.getBoard().getPieceAt(i, j));
            }
        }
    }

    public RenderView(Context context, AttributeSet as)
    {
        super(context, as);
        this.gameWin = gameWin;
        AssetManager assets = context.getAssets();
        InputStream iS = null;
        baseLength = 800;
        init = false;
        drawableDraughtsPieces = new DrawableDraughtsPiece[DraughtsBoard.NUM_ROWS][DraughtsBoard.NUM_COLS];

        try// get the image of the piece
        {
            iS = assets.open("images/checkersBoard.png");
            board = BitmapFactory.decodeStream(iS);
            iS = assets.open("images/dark_piece.png");
            redPiece = BitmapFactory.decodeStream(iS);
            iS = assets.open("images/light_piece.png");
            bluePiece = BitmapFactory.decodeStream(iS);
            iS = assets.open("images/dark_crowned.png");
            redCrowned = BitmapFactory.decodeStream(iS);
            iS = assets.open("images/light_crowned.png");
            blueCrowned = BitmapFactory.decodeStream(iS);
            holder = this.getHolder();
            this.setZOrderOnTop(true);
            holder.setFormat(PixelFormat.TRANSPARENT);
        }
        catch (Exception e)
        {

        }
    }

    public void draw()// draw the first time and update every time
    {
        if (holder.getSurface().isValid())
        {
           final  Canvas canvas = holder.lockCanvas();

            if (!init)
            {
                double scale = canvas.getWidth() / baseLength;
                int height = (int) (800 * scale);
                int top = (canvas.getHeight() - height) / 2;
                int bottom = top + height;
                int right = (int) (800 * scale);
                Commons.setBounds(new Rect(0, top, right, bottom));
                gameWin.getBoard().setDstRect(0, top, right, bottom);
                init = true;
            }

            canvas.drawBitmap(board, null, gameWin.getBoard().getDstRect(), null);
            Rect temp;

            for (int i = 0; i < DraughtsBoard.NUM_ROWS; i++)
            {
                for (int j = 0; j < DraughtsBoard.NUM_COLS; j++)
                {
                    DrawableDraughtsPiece drawablePiece = drawableDraughtsPieces[i][j];
                    DraughtsPiece currentPiece = drawablePiece.currentPiece;

                    if (!currentPiece.is_none_piece())
                    {
                        temp = drawablePiece.GetDstRect();
                        if (currentPiece.getPieceType() == DraughtsPiece.PieceType.RED_PIECE)
                        {
                            if (currentPiece.is_crowned()) canvas.drawBitmap(redCrowned, null, temp, null);
                            else canvas.drawBitmap(redPiece, null, temp, null);
                        }
                        else
                        {
                            if (currentPiece.is_crowned()) canvas.drawBitmap(blueCrowned, null, temp, null);
                            else canvas.drawBitmap(bluePiece, null, temp, null);
                        }
                    }
                }
            }
            holder.unlockCanvasAndPost(canvas);
        }
    }

    public void pause()
    {
        step.shutdownNow();
        step.purge();
    }

    public void resume()
    {
        step = new ScheduledThreadPoolExecutor(1);
        step.scheduleWithFixedDelay(new Runnable()
        {
            public void run()
            {
                RenderView.this.draw();
            }
        }, 200, 60, TimeUnit.MILLISECONDS);

    }
}
