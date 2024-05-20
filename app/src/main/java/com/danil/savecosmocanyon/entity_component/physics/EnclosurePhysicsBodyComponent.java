package com.danil.savecosmocanyon.entity_component.physics;

import com.danil.savecosmocanyon.GameWorld;
import com.danil.savecosmocanyon.entity_component.PhysicsBodyComponent;
import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.PolygonShape;
import com.google.fpl.liquidfun.Vec2;

public class EnclosurePhysicsBodyComponent extends PhysicsBodyComponent {
    private static final float THICKNESS = 1;

    private static final int VERTICES = 5;
    public static final Vec2[] left_bottom_vertices = new Vec2[VERTICES];
    public static final Vec2[] right_bottom_vertices = new Vec2[VERTICES];

    public EnclosurePhysicsBodyComponent(GameWorld gw, float xmin, float xmax, float ymin, float ymax) {
        this.gw = gw;

        // Left bottom enclosure coordinates
        for (int i = 0; i < VERTICES; i++) {
            left_bottom_vertices[i] = new Vec2();
        }
        left_bottom_vertices[0].set(-9f, -4f);
        left_bottom_vertices[1].set(-6.5f, -4.25f);
        left_bottom_vertices[2].set(0f, -4f);
        left_bottom_vertices[3].set(5f, -4.25f);
        left_bottom_vertices[4].set(9.8f, -4f);

        // Right bottom enclosure coordinates
        for (int i = 0; i < VERTICES; i++) {
            right_bottom_vertices[i] = new Vec2();
        }
        right_bottom_vertices[0].set(9f, -4f);
        right_bottom_vertices[1].set(6.5f, -4.25f);
        right_bottom_vertices[2].set(0f, -4f);
        right_bottom_vertices[3].set(-5f, -4.25f);
        right_bottom_vertices[4].set(-10.1f, -4f);

        BodyDef bdef = new BodyDef();
        this.body = gw.getWorld().createBody(bdef);
        body.setUserData(this);

        PolygonShape box = new PolygonShape();

        // Left
        float centerY = ymin + (ymax-ymin)/2 - ymax - 4.5f;
        box.setAsBox(THICKNESS, ymax-ymin, xmin, centerY, 0);
        body.createFixture(box, 0);

        // Right
        box.setAsBox(THICKNESS, ymax-ymin, xmax, centerY, 0);
        body.createFixture(box, 0);

        // Bottom-left
        FixtureDef fixturedef = new FixtureDef();
        for (int i = 0; i < VERTICES - 1; i++) {
            float x1 = - 15f + left_bottom_vertices[i].getX();
            float y1 = 13.5f + left_bottom_vertices[i].getY();
            float x3 = - 15f + left_bottom_vertices[i+1].getX();
            float y3 = 13.5f + left_bottom_vertices[i+1].getY();
            box.setAsTriangle(x1, y1, -15f, 13.5f, x3, y3);
            fixturedef.setShape(box);
            fixturedef.setFriction(0.8f);
            fixturedef.setDensity(1f);
            fixturedef.setRestitution(0f);
            body.createFixture(fixturedef);
        }

        // Bottom-right
        for (int i = 0; i < VERTICES - 1; i++) {
            float x1 = 15f + right_bottom_vertices[i].getX();
            float y1 = 13.5f + right_bottom_vertices[i].getY();
            float x3 = 15f + right_bottom_vertices[i+1].getX();
            float y3 = 13.5f + right_bottom_vertices[i+1].getY();
            box.setAsTriangle(x1, y1, 15f, 13.5f, x3, y3);
            fixturedef.setShape(box);
            fixturedef.setFriction(0.8f);
            fixturedef.setDensity(1f);
            fixturedef.setRestitution(0f);
            body.createFixture(fixturedef);
        }

        // Invisible screen bottom (used to destroy and manage objects that fall into the canyon)
        box.setAsBox(xmax-xmin, THICKNESS, xmin+(xmax-xmin)/2, ymax+2.5f, 0);
        body.createFixture(box, 0);

        bdef.delete();
        box.delete();
        fixturedef.delete();
    }

    public Body getBody() {
        return this.body;
    }
}
