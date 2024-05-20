package com.danil.savecosmocanyon.entity_component.collidables;

import com.danil.savecosmocanyon.GameActivity;
import com.danil.savecosmocanyon.GameWorld;
import com.danil.savecosmocanyon.entity_component.CollidableComponent;
import com.danil.savecosmocanyon.entity_component.ComponentType;
import com.danil.savecosmocanyon.entity_component.Entity;
import com.danil.savecosmocanyon.entity_component.GameObject;
import com.danil.savecosmocanyon.entity_component.PhysicsBodyComponent;
import com.google.fpl.liquidfun.Body;

public class ScraperSuspensionCollidableComponent extends CollidableComponent {
    public boolean fell = false;
    private final GameObject scraper;

    public ScraperSuspensionCollidableComponent(GameObject scraper) {
        this.scraper = scraper;
    }

    @Override
    public void doAtCollision(Entity collider) {
        // If this suspension previously fell in the canyon, return
        if (fell) {
            return;
        }

        Body suspensionBody = ((PhysicsBodyComponent) this.owner.getComponent(ComponentType.PHYSICS_BODY)).body;
        float y = suspensionBody.getPositionY();
        if (collider.getComponent(ComponentType.GROUND) != null) {
            // If this suspension falls into the canyon, remove it from the view
            if (!fell && y >= (GameActivity.YMAX - 3f)) {
                fell = true;
                GameObject thisSuspension = (GameObject) this.getOwner();
                GameWorld gw = thisSuspension.getGameWorld();
                gw.removeGameObject(thisSuspension);
            }
        }

        ((ScraperCollidableComponent) (scraper.getComponent(ComponentType.COLLIDABLE))).doAtCollision(collider);
    }
}
