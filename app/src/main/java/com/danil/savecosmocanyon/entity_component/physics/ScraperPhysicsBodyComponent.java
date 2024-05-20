package com.danil.savecosmocanyon.entity_component.physics;

import com.danil.savecosmocanyon.GameWorld;
import com.danil.savecosmocanyon.entity_component.PhysicsBodyComponent;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.PolygonShape;
import com.google.fpl.liquidfun.Vec2;

public class ScraperPhysicsBodyComponent extends PhysicsBodyComponent {
    private static final float width = 6.5f, height = 3.5f;

    private static final int VERTICES = 21;
    private static final Vec2[] vertices = new Vec2[VERTICES];

    public ScraperPhysicsBodyComponent(GameWorld gw, float x, float y) {
        super.gw = gw;

        for (int i = 0; i < VERTICES; i++) {
            vertices[i] = new Vec2();
        }

        // Left scraper vertices coordinates
        vertices[0].set(-2.75f, -0.6f);
        vertices[1].set(-2.412f, -0.6f);
        vertices[2].set(-2.412f, -0.25f); // Before the "too small" issue: (-2.412f, -0.562f)
        vertices[3].set(-0.85f, -0.562f);
        vertices[4].set(-0.712f, -1.406f);
        vertices[5].set(0.04f, -1.406f);
        vertices[6].set(0.3f, -0.637f);
        vertices[7].set(0.533f, -0.637f);
        vertices[8].set(1.25f, -0.06f);
        vertices[9].set(1.423f, -0.58f);
        vertices[10].set(2.016f, 0.413f);
        vertices[11].set(2.471f, 0.188f);
        vertices[12].set(1.898f, 0.937f);
        vertices[13].set(2.688f, 1.2f);
        vertices[14].set(1.878f, 1.181f);
        vertices[15].set(0.988f, 0.637f);
        vertices[16].set(-2.471f, 0.637f);
        vertices[17].set(-2.867f, 0.113f);
        vertices[18].set(-2.867f, -0.206f);
        vertices[19].set(-2.75f, -0.206f);
        vertices[20].set(-2.75f, -0.6f);

        BodyDef bdef = new BodyDef();
        bdef.setPosition(x, y);
        bdef.setType(BodyType.dynamicBody);

        this.body = gw.getWorld().createBody(bdef);
        body.setSleepingAllowed(false);
        body.setUserData(this);

        PolygonShape box = new PolygonShape();

        /*
        Polygons not directly supported in JLiquidFun, but they can be created as a set of
        triangles. Here, for each triangle in this set, its second point is the centre of the
        polygon. If A=(x1,y1), B=(x,y), C=(x3,y3) are given, then AC is a segment which consists
        of a line of the polygon.
        The following code creates the polygon (scraper body without the wheels).
         */
        FixtureDef fixturedef = new FixtureDef();
        if (x<=0) {
            for (int i = 0; i < VERTICES - 1; i++) {
                float x1 = vertices[i].getX();
                float y1 = vertices[i].getY();
                float x3 = vertices[i+1].getX();
                float y3 = vertices[i+1].getY();
                box.setAsTriangle(x1, y1, 0, 0, x3, y3);
                fixturedef.setShape(box);
                fixturedef.setFriction(0.1f);
                fixturedef.setRestitution(0.1f);
                fixturedef.setDensity(0.25f);
                body.createFixture(fixturedef);
            }
        } else {
            for (int i = VERTICES - 1; i > 0; i--) {
                float x1 = -vertices[i].getX();
                float y1 = vertices[i].getY();
                float x3 = -vertices[i-1].getX();
                float y3 = vertices[i-1].getY();
                box.setAsTriangle(x1, y1, 0, 0, x3, y3);
                fixturedef.setShape(box);
                fixturedef.setFriction(0.1f);
                fixturedef.setRestitution(0.1f);
                fixturedef.setDensity(0.25f);
                body.createFixture(fixturedef);
            }
        }

        fixturedef.delete();
        bdef.delete();
        box.delete();
    }

    public static float getWidth() {
        return width;
    }

    public static float getHeight() {
        return height;
    }
}
