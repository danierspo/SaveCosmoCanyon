package com.danil.savecosmocanyon.entity_component.drawables;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import com.danil.savecosmocanyon.GameWorld;
import com.danil.savecosmocanyon.R;
import com.danil.savecosmocanyon.entity_component.DrawableComponent;
import com.danil.savecosmocanyon.entity_component.physics.MeteorPhysicsBodyComponent;

public class MeteorDrawableComponent extends DrawableComponent {
    private final float screen_semi_width, screen_semi_height;
    private static int instances = 0;

    private final Canvas canvas;

    private final Rect src = new Rect();
    private final RectF dest = new RectF();
    private Bitmap bitmap;

    public MeteorDrawableComponent(GameWorld gw, MeteorType meteorType) {
        this.gw = gw;
        this.canvas = new Canvas(gw.getBuffer());
        this.name = "Meteor" + instances;

        instances++;

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;
        if (meteorType == MeteorType.NORMAL) {
            bitmap = BitmapFactory.decodeResource(gw.getActivity().getResources(), R.drawable.meteor, o);
            src.set(0, 0, 84, 99);

            screen_semi_width = gw.toPixelsXLength(MeteorPhysicsBodyComponent.getWidth())/2;
            screen_semi_height = gw.toPixelsYLength(MeteorPhysicsBodyComponent.getHeight())/2;
        } else if (meteorType == MeteorType.MAGIC) {
            bitmap = BitmapFactory.decodeResource(gw.getActivity().getResources(), R.drawable.magic_materia, o);
            src.set(0, 0, 99, 99);

            screen_semi_width = gw.toPixelsXLength(MeteorPhysicsBodyComponent.getMateriaWidth())/2;
            screen_semi_height = gw.toPixelsYLength(MeteorPhysicsBodyComponent.getMateriaHeight())/2;
        } else {
            bitmap = BitmapFactory.decodeResource(gw.getActivity().getResources(), R.drawable.summon_materia, o);
            src.set(0, 0, 99, 99);

            screen_semi_width = gw.toPixelsXLength(MeteorPhysicsBodyComponent.getMateriaWidth())/2;
            screen_semi_height = gw.toPixelsYLength(MeteorPhysicsBodyComponent.getMateriaHeight())/2;
        }
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

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public GameWorld getWorld() {
        return this.gw;
    }
}
