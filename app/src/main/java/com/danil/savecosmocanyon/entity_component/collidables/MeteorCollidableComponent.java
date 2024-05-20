package com.danil.savecosmocanyon.entity_component.collidables;

import android.graphics.BitmapFactory;

import com.danil.savecosmocanyon.CollisionSounds;
import com.danil.savecosmocanyon.GameActivity;
import com.danil.savecosmocanyon.GameWorld;
import com.danil.savecosmocanyon.R;
import com.danil.savecosmocanyon.entity_component.AliveComponent;
import com.danil.savecosmocanyon.entity_component.CollidableComponent;
import com.danil.savecosmocanyon.entity_component.Component;
import com.danil.savecosmocanyon.entity_component.ComponentType;
import com.danil.savecosmocanyon.entity_component.Entity;
import com.danil.savecosmocanyon.entity_component.GameObject;
import com.danil.savecosmocanyon.entity_component.PhysicsBodyComponent;
import com.danil.savecosmocanyon.entity_component.alives.ScraperAliveComponent;
import com.danil.savecosmocanyon.entity_component.drawables.MeteorDrawableComponent;
import com.danil.savecosmocanyon.entity_component.drawables.MeteorType;
import com.danil.savecosmocanyon.entity_component.drawables.ui.UIScoreDrawableComponent;
import com.danil.savecosmocanyon.entity_component.physics.MeteorPhysicsBodyComponent;
import com.google.fpl.liquidfun.Body;

public class MeteorCollidableComponent extends CollidableComponent {
    private static final int DAMAGE = 25;
    private boolean isOnTheGround = false;
    private boolean hasCollided = false;

    @Override
    public void doAtCollision(Entity collider) {
        Body meteorBody = ((PhysicsBodyComponent) this.owner.getComponent(ComponentType.PHYSICS_BODY)).body;
        MeteorType meteorType = ((MeteorPhysicsBodyComponent) this.owner.getComponent(ComponentType.PHYSICS_BODY)).meteorType;
        Body colliderBody = ((PhysicsBodyComponent) collider.getComponent(ComponentType.PHYSICS_BODY)).body;

        float x = meteorBody.getPositionX(), y = meteorBody.getPositionY();

        // If this meteor has collided with the enclosure
        if (collider.getComponent(ComponentType.GROUND) != null) {
            GameObject thisMeteor = (GameObject) this.getOwner();
            /*
             If this meteor falls into the canyon or beyond the left/right enclosure, remove it
             from the view.
             */
            if (y >= (GameActivity.YMAX - 3.5f) || x <= GameActivity.XMIN || x >= GameActivity.XMAX) {
                GameWorld gw = thisMeteor.getGameWorld();
                gw.removeGameObject(thisMeteor);
                if (((MeteorPhysicsBodyComponent) thisMeteor.getComponent(ComponentType.PHYSICS_BODY)).startingX <= 0) {
                    gw.getLeftMeteorsList().remove(thisMeteor.hashCode());
                } else {
                    gw.getRightMeteorsList().remove(thisMeteor.hashCode());
                }
                return;
            }

            // If this meteor falls on the ground (useful for Sound and AI)
            if ((y >= (GameActivity.YMAX - 5.5f)) && !isOnTheGround) {
                CollisionSounds.meteorToGround.play(0.7f);
                isOnTheGround = true;
                if (x<=0) {
                    thisMeteor.getGameWorld().getLeftMeteorsList().put(thisMeteor.hashCode(), thisMeteor);
                } else {
                    thisMeteor.getGameWorld().getRightMeteorsList().put(thisMeteor.hashCode(), thisMeteor);
                }
            }
            return;
        }

        // If this meteor has collided with a scraper
        Component component = collider.getComponent(ComponentType.ALIVE);
        AliveComponent aliveComponent;
        if (!hasCollided && component != null) {
            aliveComponent = (AliveComponent) component;
            if (colliderBody != null) {
                ScraperAliveComponent scraperAliveComponent = (ScraperAliveComponent) aliveComponent;
                GameObject thisMeteor = (GameObject) this.getOwner();
                GameWorld gw = thisMeteor.getGameWorld();
                switch (meteorType) {
                    case NORMAL:
                        int scraperCurrentHealth = scraperAliveComponent.getCurrentHealth();
                        if (!isOnTheGround) {
                            if (scraperCurrentHealth > 0) {
                                UIScoreDrawableComponent.score += DAMAGE;
                                // Modify meteor's bitmap
                                BitmapFactory.Options o = new BitmapFactory.Options();
                                o.inScaled = false;
                                MeteorDrawableComponent meteorDrawableComponent = (MeteorDrawableComponent) this.owner.getComponent(ComponentType.DRAWABLE);
                                meteorDrawableComponent.setBitmap(BitmapFactory.decodeResource(meteorDrawableComponent.getWorld().getActivity().getResources(), R.drawable.collided_meteor, o));
                            }
                            scraperAliveComponent.setHealth(Math.max(scraperCurrentHealth - DAMAGE, 0));
                        }
                        CollisionSounds.meteorToScraper.play(1f);
                        break;
                    case MAGIC:
                        scraperAliveComponent.setHealth(scraperAliveComponent.getInitialHealth());
                        CollisionSounds.magicMeteorToScraper.play(1f);
                        gw.removeGameObject(thisMeteor);
                        if (((MeteorPhysicsBodyComponent) thisMeteor.getComponent(ComponentType.PHYSICS_BODY)).startingX <= 0) {
                            gw.getLeftMeteorsList().remove(thisMeteor.hashCode());
                        } else {
                            gw.getRightMeteorsList().remove(thisMeteor.hashCode());
                        }
                        hasCollided = true;
                        break;
                    case SUMMON:
                        scraperAliveComponent.setHealth(0);
                        CollisionSounds.summonMeteorToScraper.play(1f);
                        gw.removeGameObject(thisMeteor);
                        if (((MeteorPhysicsBodyComponent) thisMeteor.getComponent(ComponentType.PHYSICS_BODY)).startingX <= 0) {
                            gw.getLeftMeteorsList().remove(thisMeteor.hashCode());
                        } else {
                            gw.getRightMeteorsList().remove(thisMeteor.hashCode());
                        }
                        hasCollided = true;
                        break;
                }
            }
        }

        if (meteorType == MeteorType.NORMAL) {
            hasCollided = true;
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isOnTheGround() {
        return isOnTheGround;
    }
}
