package com.danil.savecosmocanyon.entity_component.drawables;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.danil.savecosmocanyon.GameWorld;
import com.danil.savecosmocanyon.entity_component.DrawableComponent;

public class EnclosureDrawableComponent extends DrawableComponent {
    private static final float THICKNESS = 1;

    private final Canvas canvas;
    private final Paint paint = new Paint();
    private final float screen_xmin;
    private final float screen_xmax;
    private final float screen_ymin;
    private final float screen_ymax;

    public EnclosureDrawableComponent(GameWorld gw, float xmin, float xmax, float ymin, float ymax) {
        this.gw = gw;
        this.screen_xmin = gw.toPixelsX(xmin+THICKNESS);
        this.screen_xmax = gw.toPixelsX(xmax-THICKNESS);
        this.screen_ymin = gw.toPixelsY(ymin+THICKNESS);
        this.screen_ymax = gw.toPixelsY(ymax-THICKNESS);
        this.name = "Enclosure";

        this.canvas = new Canvas(gw.getBuffer());
    }

    @Override
    public void draw(Bitmap buffer, float x, float y, float angle) {
        // Unaesthetic. For testing purposes only
        /*
        canvas.save();
        paint.setARGB(255, 0, 0, 255);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        canvas.drawRect(screen_xmin, screen_ymin, screen_xmax, screen_ymax, paint);

        canvas.restore();
         */
    }
}
