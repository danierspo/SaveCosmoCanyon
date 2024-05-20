package com.danil.savecosmocanyon.entity_component.collidables;

import com.danil.savecosmocanyon.GameActivity;
import com.danil.savecosmocanyon.GameWorld;
import com.danil.savecosmocanyon.entity_component.CollidableComponent;
import com.danil.savecosmocanyon.entity_component.ComponentType;
import com.danil.savecosmocanyon.entity_component.Entity;
import com.danil.savecosmocanyon.entity_component.GameObject;
import com.danil.savecosmocanyon.entity_component.PhysicsBodyComponent;
import com.google.fpl.liquidfun.Body;

public class ScraperWheelCollidableComponent extends CollidableComponent {
    public boolean fell = false;
    private final GameObject suspension;

    public ScraperWheelCollidableComponent(GameObject suspension) {
        this.suspension = suspension;
    }

    @Override
    public void doAtCollision(Entity collider) {
        // If this wheel previously fell in the canyon, return
        if (fell) {
            return;
        }

        Body wheelBody = ((PhysicsBodyComponent) this.owner.getComponent(ComponentType.PHYSICS_BODY)).body;
        float y = wheelBody.getPositionY();
        if (collider.getComponent(ComponentType.GROUND) != null) {
            // If this wheel falls into the canyon, remove it from the view
            if (!fell && y >= (GameActivity.YMAX - 3f)) {
                fell = true;
                GameObject thisWheel = (GameObject) this.getOwner();
                GameWorld gw = thisWheel.getGameWorld();
                gw.removeGameObject(thisWheel);
                gw.getScraperWheelsList().remove(thisWheel.hashCode());
            }
        }

        ((ScraperSuspensionCollidableComponent) (suspension.getComponent(ComponentType.COLLIDABLE))).doAtCollision(collider);
    }
}
