package com.danil.savecosmocanyon.entity_component.drawables.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.preference.PreferenceManager;

import com.danil.savecosmocanyon.GameActivity;
import com.danil.savecosmocanyon.GameWorld;
import com.danil.savecosmocanyon.R;
import com.danil.savecosmocanyon.entity_component.DrawableComponent;

public class UIGameOverWindowDrawableComponent extends DrawableComponent {
    public static int returnTime;
    private final int highscore;
    public static int final_score = 0;
    private final GameActivity activity;

    private final Canvas canvas;
    private final Paint paint;
    private static Bitmap bitmap;

    private final Rect src = new Rect();
    private final RectF dest = new RectF();

    public UIGameOverWindowDrawableComponent(GameWorld gw, boolean success, int score) {
        this.gw = gw;
        this.activity = (GameActivity) gw.getActivity();
        this.name = "GameOverWindow";
        this.canvas = new Canvas(gw.getBuffer());
        this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(64f);
        Typeface tf = Typeface.createFromAsset(gw.getActivity().getAssets(),"fonts/in-game_font.ttf");
        paint.setTypeface(tf);

        this.highscore = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext()).getInt("HIGHSCORE", 0);
        returnTime = 10;
        final_score = score;

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;
        if (success) {
            bitmap = BitmapFactory.decodeResource(gw.getActivity().getResources(), R.drawable.gameover_success, o);
            ((GameActivity) this.gw.getActivity()).setMusic("ff7_victory.ogg");
        } else {
            bitmap = BitmapFactory.decodeResource(gw.getActivity().getResources(), R.drawable.gameover_fail, o);
            ((GameActivity) this.gw.getActivity()).setMusic("ff7_gameover.ogg");
        }
        src.set(0, 0, 1001, 411);

        float screen_posX = gw.toPixelsX(0);
        float screen_posY = gw.toPixelsY(0);

        dest.left = screen_posX - 501;
        dest.bottom = screen_posY + 206;
        dest.right = screen_posX + 501;
        dest.top = screen_posY - 206;

        UITimeLeftDrawableComponent.gameOver = true;
        gw.getTouchConsumer().gameOver = true;


    }

    @Override
    public void draw(Bitmap buffer, float x, float y, float angle) {
        float fpsDeltaTime = activity.getRenderView().getFpsDeltaTime();
        if (fpsDeltaTime > 1 && !activity.isPaused()) { // Prints every second
            returnTime--;
            if (returnTime == 0) {
                Intent data = new Intent();
                data.putExtra("Score", final_score);
                activity.setResult(GameActivity.RESULT_OK, data);
                activity.finish();
                return;
            }
        }

        canvas.save();
        canvas.drawBitmap(bitmap, src, dest, null);
        canvas.drawText("Returning in " + returnTime, dest.left + 25, dest.bottom - 100, paint);
        if (final_score > highscore) {
            canvas.drawText("NEW HIGHSCORE! Final score: " + final_score + ". Saving...", dest.left + 25, dest.bottom - 50, paint);
        } else {
            canvas.drawText("Final score: " + final_score, dest.left + 25, dest.bottom - 50, paint);
        }
        canvas.restore();
    }
}
