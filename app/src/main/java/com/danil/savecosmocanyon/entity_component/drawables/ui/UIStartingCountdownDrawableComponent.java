package com.danil.savecosmocanyon.entity_component.drawables.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.danil.savecosmocanyon.GameActivity;
import com.danil.savecosmocanyon.GameWorld;
import com.danil.savecosmocanyon.entity_component.ComponentType;
import com.danil.savecosmocanyon.entity_component.DrawableComponent;

public class UIStartingCountdownDrawableComponent extends DrawableComponent {
    private static float screen_posX, screen_posY;
    private int countdown = 3;
    private final GameActivity activity;

    private final Canvas canvas;
    private final Paint paint;

    public UIStartingCountdownDrawableComponent(GameWorld gw) {
        this.gw = gw;
        this.activity = (GameActivity) gw.getActivity();
        this.name = "StartingCountdown";
        this.canvas = new Canvas(gw.getBuffer());
        this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setTextSize(128f);
        Typeface tf = Typeface.createFromAsset(gw.getActivity().getAssets(),"fonts/in-game_font.ttf");
        paint.setTypeface(tf);

        screen_posX = gw.toPixelsX(0);
        screen_posY = gw.toPixelsY(0);
    }

    @Override
    public void draw(Bitmap buffer, float x, float y, float angle) {
        float fpsDeltaTime = activity.getRenderView().getFpsDeltaTime();
        if (fpsDeltaTime > 1 && !activity.isPaused()) {
            countdown--;
            if (countdown == 0) {
                gw.getTouchConsumer().starting = false;
                UITimeLeftDrawableComponent.starting = false;
                this.owner.removeComponent(ComponentType.DRAWABLE);
            }
        }

        canvas.save();
        canvas.drawText("STARTING IN", screen_posX - 172, screen_posY, paint);
        canvas.drawText("" + countdown, screen_posX, screen_posY + 64, paint);
        canvas.restore();
    }
}
