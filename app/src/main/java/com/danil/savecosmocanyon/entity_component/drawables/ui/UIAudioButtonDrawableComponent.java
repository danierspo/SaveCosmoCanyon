package com.danil.savecosmocanyon.entity_component.drawables.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import com.danil.savecosmocanyon.GameWorld;
import com.danil.savecosmocanyon.R;
import com.danil.savecosmocanyon.entity_component.DrawableComponent;

public class UIAudioButtonDrawableComponent extends DrawableComponent {
    private static float screen_posX, screen_posY;
    private static final float height = 2, width = 2;
    private static float screen_semi_width, screen_semi_height;

    public static final Rect src = new Rect();
    public static final RectF dest = new RectF();
    public static Bitmap bitmap;
    private final Canvas canvas;

    public UIAudioButtonDrawableComponent(GameWorld gw, float x, float y) {
        this.gw = gw;
        this.name = "AudioButton";

        screen_semi_width = gw.toPixelsXLength(width)/2;
        screen_semi_height = gw.toPixelsYLength(height)/2;
        screen_posX = gw.toPixelsX(x);
        screen_posY = gw.toPixelsY(y);

        this.canvas = new Canvas(gw.getBuffer());
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;
        bitmap = BitmapFactory.decodeResource(gw.getActivity().getResources(), R.drawable.audio_on, o);

        src.set(0, 0, 255, 255);
    }

    @Override
    public void draw(Bitmap buffer, float x, float y, float angle) {
        canvas.save();
        dest.left = screen_posX - screen_semi_width;
        dest.bottom = screen_posY + screen_semi_height;
        dest.right = screen_posX + screen_semi_width;
        dest.top = screen_posY - screen_semi_height;
        canvas.drawBitmap(bitmap, src, dest, null);
        canvas.restore();
    }
}
