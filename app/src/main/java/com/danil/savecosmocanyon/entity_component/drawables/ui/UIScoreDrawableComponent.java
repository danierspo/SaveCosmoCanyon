package com.danil.savecosmocanyon.entity_component.drawables.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.danil.savecosmocanyon.GameWorld;
import com.danil.savecosmocanyon.entity_component.DrawableComponent;

public class UIScoreDrawableComponent extends DrawableComponent {
    private static float screen_posX, screen_posY;
    public static int score;

    private final Canvas canvas;
    private final Paint paint;

    public UIScoreDrawableComponent(GameWorld gw, float x, float y) {
        this.gw = gw;
        this.name = "Score";
        score = 0;
        this.canvas = new Canvas(gw.getBuffer());
        this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLUE);
        paint.setTextSize(64f);
        Typeface tf = Typeface.createFromAsset(gw.getActivity().getAssets(),"fonts/in-game_font.ttf");
        paint.setTypeface(tf);

        screen_posX = gw.toPixelsX(x);
        screen_posY = gw.toPixelsY(y);
    }

    @Override
    public void draw(Bitmap buffer, float x, float y, float angle) {
        canvas.save();
        canvas.drawText("SCORE: " + score, screen_posX, screen_posY, paint);
        canvas.restore();
    }
}
