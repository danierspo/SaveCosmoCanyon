package com.danil.savecosmocanyon.entity_component.drawables.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;

import com.danil.savecosmocanyon.GameWorld;
import com.danil.savecosmocanyon.R;
import com.danil.savecosmocanyon.entity_component.DrawableComponent;
import com.danil.savecosmocanyon.entity_component.physics.MeteorPhysicsBodyComponent;

public class UINextMeteorDrawableComponent extends DrawableComponent {
    private static float screen_posX, screen_posY;
    private static float screen_semi_width, screen_semi_height;

    private static boolean redraw;

    private static int nextMeteor;

    private final Canvas canvas;
    private final Paint paint;
    private static Bitmap bitmap;

    private final Rect src = new Rect();
    private final RectF dest = new RectF();

    public UINextMeteorDrawableComponent(GameWorld gw, float x, float y) {
        this.gw = gw;
        this.name = "NextMeteor";
        this.canvas = new Canvas(gw.getBuffer());
        this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(64f);
        Typeface tf = Typeface.createFromAsset(gw.getActivity().getAssets(),"fonts/in-game_font.ttf");
        paint.setTypeface(tf);

        generateNextMeteor();

        screen_posX = gw.toPixelsX(x);
        screen_posY = gw.toPixelsY(y);
    }

    @Override
    public void draw(Bitmap buffer, float x, float y, float angle) {
        if (redraw) {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inScaled = false;
            if (nextMeteor <= 100 && nextMeteor > 30) {
                bitmap = BitmapFactory.decodeResource(gw.getActivity().getResources(), R.drawable.meteor, o);
                src.set(0, 0, 84, 99);
                paint.setColor(Color.GRAY);

                screen_semi_width = gw.toPixelsXLength(MeteorPhysicsBodyComponent.getWidth());
                screen_semi_height = gw.toPixelsYLength(MeteorPhysicsBodyComponent.getHeight());
            } else if (nextMeteor <= 30 && nextMeteor > 15) {
                bitmap = BitmapFactory.decodeResource(gw.getActivity().getResources(), R.drawable.magic_materia, o);
                src.set(0, 0, 99, 99);
                paint.setColor(Color.GREEN);

                screen_semi_width = gw.toPixelsXLength(MeteorPhysicsBodyComponent.getMateriaWidth());
                screen_semi_height = gw.toPixelsYLength(MeteorPhysicsBodyComponent.getMateriaHeight());
            } else {
                bitmap = BitmapFactory.decodeResource(gw.getActivity().getResources(), R.drawable.summon_materia, o);
                src.set(0, 0, 99, 99);
                paint.setColor(Color.RED);

                screen_semi_width = gw.toPixelsXLength(MeteorPhysicsBodyComponent.getMateriaWidth());
                screen_semi_height = gw.toPixelsYLength(MeteorPhysicsBodyComponent.getMateriaHeight());
            }

            redraw = false;
        }

        canvas.save();
        dest.left = screen_posX;
        dest.bottom = screen_posY + screen_semi_height;
        dest.right = screen_posX + screen_semi_width;
        dest.top = screen_posY;
        canvas.drawText("NEXT METEOR", screen_posX - 90, screen_posY - 10, paint);
        canvas.drawBitmap(bitmap, src, dest, null);
        canvas.restore();
    }

    public static int getNextMeteor() {
        return nextMeteor;
    }

    public static void generateNextMeteor() {
        redraw = true;
        nextMeteor = (int)(Math.random() * 100 + 1);
    }
}
