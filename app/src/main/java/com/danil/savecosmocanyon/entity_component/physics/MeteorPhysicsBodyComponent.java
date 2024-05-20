package com.danil.savecosmocanyon.entity_component.physics;

import android.graphics.BitmapFactory;

import com.danil.savecosmocanyon.GameWorld;
import com.danil.savecosmocanyon.entity_component.PhysicsBodyComponent;
import com.danil.savecosmocanyon.entity_component.drawables.MeteorType;
import com.danil.savecosmocanyon.entity_component.drawables.ui.UINextMeteorDrawableComponent;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.CircleShape;
import com.google.fpl.liquidfun.FixtureDef;

public class MeteorPhysicsBodyComponent extends PhysicsBodyComponent {
    private static final float materia_width = 0.5f, materia_height = 0.5f;
    private static final float width = 1f, height = 1.2f;
    public final float startingX;

    public final MeteorType meteorType;

    public MeteorPhysicsBodyComponent(GameWorld gw, float x, float y) {
        this.gw = gw;

        this.startingX = x;

        BodyDef bdef = new BodyDef();
        bdef.setBullet(true);
        bdef.setPosition(x,y);
        bdef.setType(BodyType.dynamicBody);

        this.body = gw.getWorld().createBody(bdef);
        body.setSleepingAllowed(false);
        body.setUserData(this);

        CircleShape circleShape = new CircleShape();
        FixtureDef fixturedef = new FixtureDef();

        int random = UINextMeteorDrawableComponent.getNextMeteor();

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;
        if (random <= 100 && random > 30) {
            this.meteorType = MeteorType.NORMAL;
            circleShape.setRadius(width/2);
        } else if (random <= 30 && random > 15) {
            this.meteorType = MeteorType.MAGIC;
            circleShape.setRadius(materia_width/2);
        } else {
            this.meteorType = MeteorType.SUMMON;
            circleShape.setRadius(materia_width/2);
        }

        fixturedef.setShape(circleShape);
        fixturedef.setFriction(0.8f);
        fixturedef.setRestitution(0.1f);
        fixturedef.setDensity(0.7f);
        body.createFixture(fixturedef);

        fixturedef.delete();
        bdef.delete();
        circleShape.delete();
    }

    public static float getWidth() {
        return width;
    }

    public static float getHeight() {
        return height;
    }

    public static float getMateriaWidth() {
        return materia_width;
    }

    public static float getMateriaHeight() {
        return materia_height;
    }
}
