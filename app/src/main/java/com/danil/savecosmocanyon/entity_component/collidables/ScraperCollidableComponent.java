package com.danil.savecosmocanyon.entity_component.collidables;

import com.danil.savecosmocanyon.GameActivity;
import com.danil.savecosmocanyon.GameWorld;
import com.danil.savecosmocanyon.entity_component.CollidableComponent;
import com.danil.savecosmocanyon.entity_component.ComponentType;
import com.danil.savecosmocanyon.entity_component.Entity;
import com.danil.savecosmocanyon.entity_component.GameObject;
import com.danil.savecosmocanyon.entity_component.PhysicsBodyComponent;
import com.danil.savecosmocanyon.entity_component.drawables.ui.UIGameOverWindowDrawableComponent;
import com.danil.savecosmocanyon.entity_component.drawables.ui.UIScoreDrawableComponent;
import com.danil.savecosmocanyon.entity_component.drawables.ui.UITimeLeftDrawableComponent;
import com.google.fpl.liquidfun.Body;

public class ScraperCollidableComponent extends CollidableComponent {
    public boolean fell = false;
    private final float startingX;

    public ScraperCollidableComponent(float x) {
        startingX = x;
    }

    @Override
    public void doAtCollision(Entity collider) {
        // If this scraper previously fell in the canyon, return
        if (fell) {
            return;
        }

        // If it's one of this scraper's wheel/suspension, just return
        if (collider.toString().contains(this.owner.toString())) {
            return;
        }

        Body scraperBody = ((PhysicsBodyComponent) this.owner.getComponent(ComponentType.PHYSICS_BODY)).body;
        float y = scraperBody.getPositionY();

        // If this scraper has collided with the enclosure
        if (collider.getComponent(ComponentType.GROUND) != null) {
            /*
             If this scraper falls into the canyon, remove it from the view and add +1000 to the
             score.
             */
            if (!fell && y >= (GameActivity.YMAX - 3f)) {
                GameObject thisScraper = (GameObject) this.getOwner();
                UIScoreDrawableComponent.score += 1000;
                fell = true;
                if (startingX <= 0) {
                    GameWorld.leftScraperHPBar.removeComponent(ComponentType.DRAWABLE);

                } else {
                    GameWorld.rightScraperHPBar.removeComponent(ComponentType.DRAWABLE);
                }

                GameWorld gw = thisScraper.getGameWorld();
                gw.removeGameObject(thisScraper);
                gw.scrapersList.remove(thisScraper.hashCode());

                if (gw.getScrapersList().size() == 0) {
                    UIScoreDrawableComponent.score += (100 * UITimeLeftDrawableComponent.timeLeft);

                    if (!UITimeLeftDrawableComponent.gameOver) {
                        GameWorld.gameoverWindow.addComponent(
                                new UIGameOverWindowDrawableComponent(gw, true, UIScoreDrawableComponent.score)
                        );
                    }
                }
            }
        } else { // If this scraper has collided with a meteor, call meteor's doAtCollision() method
            ((MeteorCollidableComponent) collider.getComponent(ComponentType.COLLIDABLE)).doAtCollision(this.owner);
        }
    }
}
