package com.danil.savecosmocanyon.entity_component.drawables.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.danil.savecosmocanyon.GameActivity;
import com.danil.savecosmocanyon.GameWorld;
import com.danil.savecosmocanyon.entity_component.DrawableComponent;

public class UITimeLeftDrawableComponent extends DrawableComponent {
    private static float screen_posX, screen_posY;
    public static int timeLeft;
    public static boolean gameOver;
    public static boolean starting;
    private final GameActivity activity;

    private final Canvas canvas;
    private final Paint paint;

    public UITimeLeftDrawableComponent(GameWorld gw, float x, float y, int gameTime) {
        this.gw = gw;
        timeLeft = gameTime;
        this.activity = (GameActivity) gw.getActivity();
        this.name = "TimeLeft";
        this.canvas = new Canvas(gw.getBuffer());
        this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLUE);
        paint.setTextSize(64f);
        Typeface tf = Typeface.createFromAsset(gw.getActivity().getAssets(),"fonts/in-game_font.ttf");
        paint.setTypeface(tf);

        screen_posX = gw.toPixelsX(x);
        screen_posY = gw.toPixelsY(y);

        gameOver = false;
        starting = true;
    }

    @Override
    public void draw(Bitmap buffer, float x, float y, float angle) {
        float fpsDeltaTime = activity.getRenderView().getFpsDeltaTime();
        if (!starting && !gameOver && fpsDeltaTime > 1 && !activity.isPaused()) {
            timeLeft--;
            if (timeLeft == 0) {
                GameWorld.gameoverWindow.addComponent(
                        new UIGameOverWindowDrawableComponent(this.gw,false, UIScoreDrawableComponent.score)
                );
            }
        }

        canvas.save();
        canvas.drawText("TIME LEFT: " + timeLeft, screen_posX, screen_posY, paint);
        canvas.restore();
    }
}
