package com.danil.savecosmocanyon;

import android.util.SparseArray;

import com.danil.savecosmocanyon.entity_component.ComponentType;
import com.danil.savecosmocanyon.entity_component.GameObject;
import com.danil.savecosmocanyon.entity_component.ais.ScraperAIComponent;
import com.danil.savecosmocanyon.entity_component.alives.ScraperAliveComponent;
import com.danil.savecosmocanyon.entity_component.physics.ScraperWheelPhysicsBodyComponent;

import java.util.ConcurrentModificationException;

public class AIThread implements Runnable {
    private final GameWorld gameworld;
    private Thread AI_thread = null;
    private volatile boolean running = false;

    public AIThread(GameWorld gw) {
        this.gameworld = gw;
    }

    public void resume() {
        running = true;
        AI_thread = new Thread(this);
        AI_thread.start();
    }

    public void pause() {
        running = false;
        while(true) {
            try {
                AI_thread.join();
                break;
            } catch (InterruptedException e) {
                // Just retry
            }
        }
    }

    public void run() {
        try {
            handleAI();
        } catch (ConcurrentModificationException ignored) {

        }
    }

    // If the scraper has 0 HPs, it will take 5 seconds to restore them, staying still. Otherwise, handle AI
    private void handleAI() {
        long startTime = System.nanoTime(), fpsTime = startTime;

        while (running) {
            long currentTime = System.nanoTime();
            float fpsDeltaTime = (currentTime-fpsTime) / 1000000000f;

            if (fpsDeltaTime > 0.25) { // Every 0.25 second
                SparseArray<GameObject> scrapersList = gameworld.scrapersList;
                for (int i = 0; i < scrapersList.size(); i++) {
                    GameObject go = scrapersList.valueAt(i);
                    ScraperAliveComponent scraperAliveComponent = ((ScraperAliveComponent) go.getComponent(ComponentType.ALIVE));
                    if (scraperAliveComponent.getCurrentHealth() == 0) {
                        if (go.body.getPositionX() <= 0) { // Left scraper wheels
                            SparseArray<GameObject> scraperWheelsList = gameworld.scraperWheelsList;
                            for (int j = 0; j < scraperWheelsList.size(); j++) {
                                GameObject wheel = scraperWheelsList.valueAt(j);
                                if (wheel.body.getPositionX() <= 0) {
                                    ScraperWheelPhysicsBodyComponent scraperWheelPhysicsBodyComponent = (ScraperWheelPhysicsBodyComponent) wheel.getComponent(ComponentType.PHYSICS_BODY);
                                    scraperWheelPhysicsBodyComponent.joint.setMotorSpeed(0);
                                }
                            }
                        } else { // Right scraper wheels
                            SparseArray<GameObject> scraperWheelsList = gameworld.scraperWheelsList;
                            for (int j = 0; j < scraperWheelsList.size(); j++) {
                                GameObject wheel = scraperWheelsList.valueAt(j);
                                if (wheel.body.getPositionX() > 0) {
                                    ScraperWheelPhysicsBodyComponent scraperWheelPhysicsBodyComponent = (ScraperWheelPhysicsBodyComponent) wheel.getComponent(ComponentType.PHYSICS_BODY);
                                    scraperWheelPhysicsBodyComponent.joint.setMotorSpeed(0);
                                }
                            }
                        }
                        scraperAliveComponent.restoreHPs++;
                        if (scraperAliveComponent.restoreHPs == 20) {
                            scraperAliveComponent.setHealth(scraperAliveComponent.getInitialHealth());
                            scraperAliveComponent.restoreHPs = 0;
                        }
                    } else {
                        ScraperAIComponent scraperAIComponent = (ScraperAIComponent) go.getComponent(ComponentType.AI);
                        if (scraperAIComponent != null) {
                            scraperAIComponent.handleAI(go.body.getPositionX());
                        }
                        scraperAliveComponent.restoreHPs = 0;
                    }
                }

                fpsTime = currentTime;
            }
        }
    }
}
