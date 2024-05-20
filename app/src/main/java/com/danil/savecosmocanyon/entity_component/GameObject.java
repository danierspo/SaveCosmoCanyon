package com.danil.savecosmocanyon.entity_component;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.danil.savecosmocanyon.Box;
import com.danil.savecosmocanyon.GameWorld;
import com.google.fpl.liquidfun.Body;

/**
 * Created by mfaella on 27/02/16.
 */
public class GameObject extends Entity {
    public Body body;
    protected String name;
    protected GameWorld gw;

    public GameObject(GameWorld gw) {
        this.gw = gw;
    }

    public boolean draw(Bitmap buffer) {
        if (body != null) {
            // Physical position of the center
            float x = body.getPositionX(),
                  y = body.getPositionY(),
                  angle = body.getAngle();

            // Cropping
            Box view = gw.getCurrentView();
            if (x > view.xmin && x < view.xmax && y > view.ymin && y < view.ymax) {
                // Screen position
                float screen_x = gw.toPixelsX(x),
                      screen_y = gw.toPixelsY(y);
                super.draw(buffer, screen_x, screen_y, angle);

                return true;
            } else {
                return false;
            }
        } else {
            super.draw(buffer, 0, 0, 0);
            return true;
        }
    }

    @Override
    public void addComponent(Component component) {
        super.addComponent(component);
        ComponentType type = component.getType();
        if (type == ComponentType.DRAWABLE) {
            this.name = ((DrawableComponent) this.getComponent(ComponentType.DRAWABLE)).name;
        } else {
            if (type == ComponentType.PHYSICS_BODY) {
                this.body = ((PhysicsBodyComponent) this.getComponent(ComponentType.PHYSICS_BODY)).body;
            }
        }
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

    public GameWorld getGameWorld() {
        return gw;
    }
}
