package com.danil.savecosmocanyon.entity_component.physics;

import com.danil.savecosmocanyon.GameWorld;
import com.danil.savecosmocanyon.entity_component.ComponentType;
import com.danil.savecosmocanyon.entity_component.GameObject;
import com.danil.savecosmocanyon.entity_component.PhysicsBodyComponent;
import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.DistanceJointDef;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.PolygonShape;
import com.google.fpl.liquidfun.PrismaticJointDef;

public class ScraperSuspensionPhysicsBodyComponent extends PhysicsBodyComponent {
    private static final float width = 0.5f, height = 0.75f;

    public ScraperSuspensionPhysicsBodyComponent(GameWorld gw, float x, float y, GameObject scraper) {
        super.gw = gw;

        BodyDef bdef = new BodyDef();
        bdef.setPosition(x, y);
        bdef.setType(BodyType.dynamicBody);

        this.body = gw.getWorld().createBody(bdef);
        body.setSleepingAllowed(false);
        body.setUserData(this);

        PolygonShape polygonShape = new PolygonShape();
        FixtureDef fixturedef = new FixtureDef();
        polygonShape.setAsBox(width/2, height/2);
        fixturedef.setShape(polygonShape);
        fixturedef.setFriction(0.25f);
        fixturedef.setRestitution(0.1f);
        fixturedef.setDensity(0.5f);
        body.createFixture(fixturedef);

        // PrismaticJoint between this suspension and its scraper
        Body scraperBody = ((ScraperPhysicsBodyComponent) scraper.getComponent(ComponentType.PHYSICS_BODY)).body;
        PrismaticJointDef prismaticJointDef = new PrismaticJointDef();
        prismaticJointDef.setBodyA(this.body);
        prismaticJointDef.setBodyB(scraperBody);
        prismaticJointDef.setLocalAnchorA(0,-height/2);
        prismaticJointDef.setLocalAnchorB(x - scraperBody.getPositionX(),(y - scraperBody.getPositionY())/2);
        prismaticJointDef.setLocalAxisA(0,-1);
        prismaticJointDef.setEnableMotor(false);
        prismaticJointDef.setEnableLimit(true); // doesn't make wheel and suspension float now.
        prismaticJointDef.setUpperTranslation(0);
        prismaticJointDef.setMaxMotorForce(70);
        prismaticJointDef.setCollideConnected(false);
        gw.getWorld().createJoint(prismaticJointDef);

        // DistanceJoint between this suspension and its scraper
        DistanceJointDef distanceJointDef = new DistanceJointDef();
        distanceJointDef.setBodyA(this.body);
        distanceJointDef.setBodyB(scraperBody);
        distanceJointDef.setLocalAnchorA(0,-height/2);
        distanceJointDef.setLocalAnchorB(x - scraperBody.getPositionX(),(y - scraperBody.getPositionY())/2);
        distanceJointDef.setLength(-0.1f);
        distanceJointDef.setFrequencyHz(4);
        distanceJointDef.setDampingRatio(0.1f);
        distanceJointDef.setCollideConnected(false);
        gw.getWorld().createJoint(distanceJointDef);

        fixturedef.delete();
        bdef.delete();
        polygonShape.delete();
        distanceJointDef.delete();
        prismaticJointDef.delete();
    }

    public static float getWidth() {
        return width;
    }

    public static float getHeight() {
        return height;
    }
}
