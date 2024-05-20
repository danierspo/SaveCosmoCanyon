package com.danil.savecosmocanyon.entity_component.ais;

import android.content.Context;
import android.util.SparseArray;

import com.danil.savecosmocanyon.GameWorld;
import com.danil.savecosmocanyon.entity_component.AIComponent;
import com.danil.savecosmocanyon.entity_component.Action;
import com.danil.savecosmocanyon.entity_component.ComponentType;
import com.danil.savecosmocanyon.entity_component.FSM;
import com.danil.savecosmocanyon.entity_component.FSMParser;
import com.danil.savecosmocanyon.entity_component.GameObject;
import com.danil.savecosmocanyon.entity_component.collidables.MeteorCollidableComponent;
import com.danil.savecosmocanyon.entity_component.physics.ScraperWheelPhysicsBodyComponent;

public class ScraperAIComponent extends AIComponent {
    protected final FSM fsm;
    private int currentMovement = 0;
    private static final int motorSpeed = 3;

    public ScraperAIComponent(Context context, String jsonFile, GameWorld gw) {
        this.gw = gw;
        this.fsm = new FSMParser(context).createFSM(jsonFile);
    }

    @Override
    public void handleAI(float x) {
        Action action = this.fsm.stepAndGetAction(gw);
        if (action != null) {
            int movement = searchMeteor(x);
            if (action.equals(Action.pushed)) {
                if (currentMovement != movement) {
                    SparseArray<GameObject> scraperWheelsList = gw.getScraperWheelsList();
                    if (x <= 0) { // Left scraper wheels
                        for (int i = 0; i < scraperWheelsList.size(); i++) {
                            GameObject go = scraperWheelsList.valueAt(i);
                            if (go.body.getPositionX() <= 0) {
                                ScraperWheelPhysicsBodyComponent scraperWheelPhysicsBodyComponent = (ScraperWheelPhysicsBodyComponent) go.getComponent(ComponentType.PHYSICS_BODY);
                                scraperWheelPhysicsBodyComponent.joint.setMotorSpeed(movement*motorSpeed);
                            }
                        }
                    } else { // Right scraper wheels
                        for (int i = 0; i < scraperWheelsList.size(); i++) {
                            GameObject go = scraperWheelsList.valueAt(i);
                            if (go.body.getPositionX() > 0) {
                                ScraperWheelPhysicsBodyComponent scraperWheelPhysicsBodyComponent = (ScraperWheelPhysicsBodyComponent) go.getComponent(ComponentType.PHYSICS_BODY);
                                scraperWheelPhysicsBodyComponent.joint.setMotorSpeed(movement*motorSpeed);
                            }
                        }
                    }
                    currentMovement = movement * motorSpeed;
                }
            } else {
                if (action.equals(Action.waited)) {
                    SparseArray<GameObject> scraperWheelsList = gw.getScraperWheelsList();
                    if (x <= 0) { // Left scraper wheels
                        for (int i = 0; i < scraperWheelsList.size(); i++) {
                            GameObject go = scraperWheelsList.valueAt(i);
                            if (go.body.getPositionX() <= 0) {
                                ScraperWheelPhysicsBodyComponent scraperWheelPhysicsBodyComponent = (ScraperWheelPhysicsBodyComponent) go.getComponent(ComponentType.PHYSICS_BODY);
                                scraperWheelPhysicsBodyComponent.joint.setMotorSpeed(motorSpeed);
                            }
                        }
                    } else { // Right scraper wheels
                        for (int i = 0; i < scraperWheelsList.size(); i++) {
                            GameObject go = scraperWheelsList.valueAt(i);
                            if (go.body.getPositionX() > 0) {
                                ScraperWheelPhysicsBodyComponent scraperWheelPhysicsBodyComponent = (ScraperWheelPhysicsBodyComponent) go.getComponent(ComponentType.PHYSICS_BODY);
                                scraperWheelPhysicsBodyComponent.joint.setMotorSpeed(-motorSpeed);
                            }
                        }
                    }
                }
            }
        }
    }

    private int searchMeteor(float x) {
        if (x <= 0) { // Left scraper
            SparseArray<GameObject> leftMeteorsList = gw.getLeftMeteorsList();
            if (leftMeteorsList.size() != 0) {
                for (int i = 0; i < leftMeteorsList.size(); i++) {
                    GameObject meteor = leftMeteorsList.valueAt(i);

                    if (!((MeteorCollidableComponent) meteor.getComponent(ComponentType.COLLIDABLE)).isOnTheGround()) {
                        continue;
                    }

                    if (meteor.body.getPositionX() >= x) {
                        return -1; // Left scraper will move to the right
                    }
                }
            }
            return 2; // Left scraper will move to the left with doubled speed
        } else { // Right scraper
            SparseArray<GameObject> rightMeteorsList = gw.getRightMeteorsList();
            if (rightMeteorsList.size() != 0) {
                for (int i = 0; i < rightMeteorsList.size(); i++) {
                    GameObject meteor = rightMeteorsList.valueAt(i);

                    if (!((MeteorCollidableComponent) meteor.getComponent(ComponentType.COLLIDABLE)).isOnTheGround()) {
                        continue;
                    }

                    if (x > meteor.body.getPositionX()) {
                        return 1; // Right scraper will move to the left
                    }
                }
            }
            return -2; // Right scraper will move to the right with doubled speed
        }
    }
}
