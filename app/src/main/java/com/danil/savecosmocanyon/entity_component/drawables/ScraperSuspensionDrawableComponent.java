package com.danil.savecosmocanyon.entity_component.drawables;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import com.danil.savecosmocanyon.GameWorld;
import com.danil.savecosmocanyon.R;
import com.danil.savecosmocanyon.entity_component.DrawableComponent;
import com.danil.savecosmocanyon.entity_component.GameObject;
import com.danil.savecosmocanyon.entity_component.physics.ScraperSuspensionPhysicsBodyComponent;

public class ScraperSuspensionDrawableComponent extends DrawableComponent {
    private static float screen_semi_width, screen_semi_height;
    private static int instances = 0;

    private final Canvas canvas;
    private final Rect src = new Rect();
    private final RectF dest = new RectF();
    private final Bitmap bitmap;

    public ScraperSuspensionDrawableComponent(GameWorld gw, GameObject scraper) {
        this.gw = gw;
        this.name = scraper.toString() + "Suspension" + instances;
        instances++;

        this.canvas = new Canvas(gw.getBuffer());
        screen_semi_width = gw.toPixelsXLength(ScraperSuspensionPhysicsBodyComponent.getWidth()/2);
        screen_semi_height = gw.toPixelsYLength(ScraperSuspensionPhysicsBodyComponent.getHeight()/2);

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;
        bitmap = BitmapFactory.decodeResource(gw.getActivity().getResources(), R.drawable.suspension, o);
        src.set(0, 0, 30, 62);
    }

    @Override
    public void draw(Bitmap buffer, float x, float y, float angle) {
        canvas.save();
        canvas.rotate((float) Math.toDegrees(angle), x, y);
        dest.left = x - screen_semi_width;
        dest.bottom = y + screen_semi_height;
        dest.right = x + screen_semi_width;
        dest.top = y - screen_semi_height;
        canvas.drawBitmap(bitmap, src, dest, null);
        canvas.restore();
    }
}
