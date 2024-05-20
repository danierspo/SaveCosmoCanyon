package com.danil.savecosmocanyon.entity_component.drawables.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import com.danil.savecosmocanyon.GameWorld;
import com.danil.savecosmocanyon.R;
import com.danil.savecosmocanyon.entity_component.DrawableComponent;

public class UIPauseWindowDrawableComponent extends DrawableComponent {
    private final Canvas canvas;
    private static Bitmap bitmap;

    private final Rect src = new Rect();
    private final RectF dest = new RectF();

    public UIPauseWindowDrawableComponent(GameWorld gw) {
        this.gw = gw;
        this.name = "PauseWindow";
        this.canvas = new Canvas(gw.getBuffer());

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;
        bitmap = BitmapFactory.decodeResource(gw.getActivity().getResources(), R.drawable.pause_window, o);
        src.set(0, 0, 501, 196);

        float screen_posX = gw.toPixelsX(0);
        float screen_posY = gw.toPixelsY(0);

        dest.left = screen_posX - 251;
        dest.bottom = screen_posY + 98;
        dest.right = screen_posX + 251;
        dest.top = screen_posY - 98;
    }

    @Override
    public void draw(Bitmap buffer, float x, float y, float angle) {
        canvas.save();
        canvas.drawBitmap(bitmap, src, dest, null);
        canvas.restore();
    }
}
