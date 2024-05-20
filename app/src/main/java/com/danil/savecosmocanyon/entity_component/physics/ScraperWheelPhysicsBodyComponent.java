package com.danil.savecosmocanyon.entity_component.physics;

import com.danil.savecosmocanyon.GameWorld;
import com.danil.savecosmocanyon.entity_component.ComponentType;
import com.danil.savecosmocanyon.entity_component.GameObject;
import com.danil.savecosmocanyon.entity_component.PhysicsBodyComponent;
import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.CircleShape;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.RevoluteJoint;
import com.google.fpl.liquidfun.RevoluteJointDef;

public class ScraperWheelPhysicsBodyComponent extends PhysicsBodyComponent {
    private static final float diameter = 1.25f;
    public RevoluteJoint joint;
    public GameObject suspension;

    public ScraperWheelPhysicsBodyComponent(GameWorld gw, float x, float y, GameObject suspension, float motorSpeed) {
        super.gw = gw;
        this.suspension = suspension;

        BodyDef bdef = new BodyDef();
        bdef.setPosition(x, y);
        bdef.setType(BodyType.dynamicBody);

        this.body = gw.getWorld().createBody(bdef);
        body.setSleepingAllowed(false);
        body.setUserData(this);

        CircleShape circleShape = new CircleShape();
        FixtureDef fixturedef = new FixtureDef();
        circleShape.setRadius(diameter/2);
        fixturedef.setShape(circleShape);
        fixturedef.setFriction(0.25f);
        fixturedef.setRestitution(0.35f);
        fixturedef.setDensity(0.9f);
        body.createFixture(fixturedef);

        // RevoluteJoint between this wheel and its suspension
        RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
        Body suspensionBody = ((ScraperSuspensionPhysicsBodyComponent) suspension.getComponent(ComponentType.PHYSICS_BODY)).body;
        revoluteJointDef.setBodyA(this.body);
        revoluteJointDef.setBodyB(suspensionBody);
        revoluteJointDef.setLocalAnchorA(0,0);
        revoluteJointDef.setLocalAnchorB(x - suspensionBody.getPositionX(),y - suspensionBody.getPositionY());
        revoluteJointDef.setEnableMotor(true);
        revoluteJointDef.setMotorSpeed(motorSpeed);
        revoluteJointDef.setMaxMotorTorque(8f);
        revoluteJointDef.setCollideConnected(false);
        this.joint = gw.getWorld().createRevoluteJoint(revoluteJointDef);


        fixturedef.delete();
        bdef.delete();
        circleShape.delete();
        revoluteJointDef.delete();
    }

    public static float getWidth() {
        return diameter;
    }

    public static float getHeight() {
        return diameter;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }
}
