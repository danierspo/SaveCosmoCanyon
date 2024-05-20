package com.danil.savecosmocanyon.entity_component.drawables.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;

import com.danil.savecosmocanyon.GameActivity;
import com.danil.savecosmocanyon.GameWorld;
import com.danil.savecosmocanyon.entity_component.DrawableComponent;

public class UIATBGaugeDrawableComponent extends DrawableComponent {
    private static final float width = 5f, height = 0.75f;
    private static float screen_posX, screen_posY;
    private static float screen_semi_width;

    private final GameActivity activity;
    private static boolean yourTurn = true;
    private final RectF dest = new RectF();
    private final RectF atbGauge = new RectF();
    private static float deltaTime = 0f;
    private static float currentDeltaTime = 0f;
    public static float delay = 0f;

    public static boolean needToRecomputePausedTime = false;
    public static float pausedTimeStart = 0f;
    public static float pausedTimeStop = 0f;
    private static float pausedTime = 0f;

    private final Canvas canvas;
    private final Paint paint;

    public UIATBGaugeDrawableComponent(GameWorld gw, float x, float y) {
        this.gw = gw;
        this.activity = (GameActivity) gw.getActivity();
        this.name = "ATBGauge";
        this.canvas = new Canvas(gw.getBuffer());
        this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setTextSize(64f);
        Typeface tf = Typeface.createFromAsset(gw.getActivity().getAssets(),"fonts/in-game_font.ttf");
        paint.setTypeface(tf);

        screen_posX = gw.toPixelsX(x);
        screen_posY = gw.toPixelsY(y);
        screen_semi_width = gw.toPixelsXLength(width)/2;
        float screen_semi_height = gw.toPixelsYLength(height)/2;

        dest.left = screen_posX;
        dest.bottom = screen_posY + screen_semi_height;
        dest.right = screen_posX + screen_semi_width;
        dest.top = screen_posY;

        atbGauge.left = screen_posX;
        atbGauge.bottom = screen_posY + screen_semi_height;
        atbGauge.right = screen_posX + screen_semi_width;
        atbGauge.top = screen_posY;
    }

    public static boolean getYourTurn() {
        return yourTurn;
    }

    public static void setYourTurn(boolean newTurn) {
        yourTurn = newTurn;
    }

    public static void resetATBGauge() {
        yourTurn = true;
        deltaTime = 0f;
        currentDeltaTime = 0f;
        needToRecomputePausedTime = false;
        pausedTimeStart = 0f;
        pausedTimeStop = 0f;
        pausedTime = 0f;
    }

    @Override
    public void draw(Bitmap buffer, float x, float y, float angle) {
        if (!activity.isPaused()) {
            canvas.save();
            float startTime = activity.getRenderView().getStartTime();
            float currentTime = activity.getRenderView().getCurrentTime();
            deltaTime = (currentTime - startTime) / 1000000000f;

            if (needToRecomputePausedTime) {
                pausedTime = (pausedTimeStop - pausedTimeStart) / 1000000000f;
                needToRecomputePausedTime = false;
            }

            if (yourTurn) {
                pausedTime = 0; pausedTimeStart = 0; pausedTimeStop = 0; delay = 0;
                currentDeltaTime = deltaTime;
                paint.setColor(Color.GRAY);
                canvas.drawRect(dest, paint);
                paint.setColor(Color.GREEN);
                canvas.drawText("GO!", screen_posX, screen_posY - 10, paint);
                canvas.drawRect(atbGauge, paint);
            } else {
                float turnTime = deltaTime - currentDeltaTime - pausedTime - delay;
                float TURN_WAIT_SECONDS = 1f;
                if ((turnTime / TURN_WAIT_SECONDS) <= 1) {
                    atbGauge.right = screen_posX + (screen_semi_width * (turnTime / TURN_WAIT_SECONDS));
                    paint.setColor(Color.GRAY);
                    canvas.drawRect(dest, paint);
                    paint.setColor(Color.RED);
                    canvas.drawText("WAIT!", screen_posX, screen_posY - 10, paint);
                    canvas.drawRect(atbGauge, paint);
                } else {
                    yourTurn = true;
                    delay = turnTime - TURN_WAIT_SECONDS;
                    /*
                    This fixes a bug where, if you click exactly after TURN_WAIT_SECONDS but before
                    TURN_WAIT_SECONDS + delay, a second throw would be available for the player.
                     */
                }
            }
            canvas.restore();
        } else {
            canvas.save();
            if (yourTurn) {
                paint.setColor(Color.GRAY);
                canvas.drawRect(dest, paint);
                paint.setColor(Color.GREEN);
                canvas.drawText("GO!", screen_posX, screen_posY - 10, paint);
                canvas.drawRect(atbGauge, paint);
            } else {
                paint.setColor(Color.GRAY);
                canvas.drawRect(dest, paint);
                paint.setColor(Color.RED);
                canvas.drawText("WAIT!", screen_posX, screen_posY - 10, paint);
                canvas.drawRect(atbGauge, paint);
            }
            canvas.restore();
        }
    }
}
