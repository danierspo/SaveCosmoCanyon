package com.danil.savecosmocanyon.entity_component.drawables.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.danil.savecosmocanyon.GameWorld;
import com.danil.savecosmocanyon.entity_component.ComponentType;
import com.danil.savecosmocanyon.entity_component.DrawableComponent;
import com.danil.savecosmocanyon.entity_component.GameObject;
import com.danil.savecosmocanyon.entity_component.alives.ScraperAliveComponent;

public class UIScraperHPBarDrawableComponent extends DrawableComponent {
    private static final float width = 6.5f, height = 0.25f;
    private static float screen_posX, screen_posY;
    private static float screen_semi_width, screen_semi_height;

    private final RectF dest = new RectF();
    private final RectF scraperHPBar = new RectF();
    private final GameObject scraper;
    private final ScraperAliveComponent scraperAliveComponent;

    private final Canvas canvas;
    private final Paint paint;

    public UIScraperHPBarDrawableComponent(GameWorld gw, GameObject scraper) {
        this.gw = gw;
        this.name = scraper.toString() + "HPBar";
        this.scraper = scraper;
        this.scraperAliveComponent = (ScraperAliveComponent) scraper.getComponent(ComponentType.ALIVE);

        this.canvas = new Canvas(gw.getBuffer());
        this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        screen_posX = gw.toPixelsX(scraper.body.getPositionX());
        screen_posY = gw.toPixelsY(scraper.body.getPositionY());
        screen_semi_width = gw.toPixelsXLength(width)/2;
        screen_semi_height = gw.toPixelsYLength(height)/2;

        dest.left = screen_posX;
        dest.bottom = screen_posY + screen_semi_height;
        dest.right = screen_posX + screen_semi_width;
        dest.top = screen_posY;

        scraperHPBar.left = screen_posX;
        scraperHPBar.bottom = screen_posY + screen_semi_height;
        scraperHPBar.right = screen_posX + screen_semi_width;
        scraperHPBar.top = screen_posY;
    }

    @Override
    public void draw(Bitmap buffer, float x, float y, float angle) {
        canvas.save();
        paint.setColor(Color.GRAY);
        screen_posX = gw.toPixelsX(scraper.body.getPositionX() - width/2);
        screen_posY = gw.toPixelsY(scraper.body.getPositionY() - 1.75f);
        dest.left = screen_posX;
        dest.bottom = screen_posY + screen_semi_height;
        dest.right = screen_posX + screen_semi_width*2;
        dest.top = screen_posY;
        canvas.drawRect(dest, paint);

        paint.setColor(Color.RED);
        scraperHPBar.left = screen_posX;
        scraperHPBar.bottom = screen_posY + screen_semi_height;
        scraperHPBar.right = screen_posX + (screen_semi_width * 2) * ((float) scraperAliveComponent.getCurrentHealth() / scraperAliveComponent.getInitialHealth());
        scraperHPBar.top = screen_posY;
        canvas.drawRect(scraperHPBar, paint);
        canvas.restore();
    }
}
