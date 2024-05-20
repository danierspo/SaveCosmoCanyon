package com.danil.savecosmocanyon.entity_component;

import com.danil.savecosmocanyon.GameWorld;

import java.util.ArrayList;
import java.util.List;

public class GameObjectsPool {
    public interface PoolGameObjectFactory {
        GameObject createObject(GameWorld gw);
    }

    private final List<GameObject> freeObjects;
    private final PoolGameObjectFactory factory;
    private final int maxSize;

    public GameObjectsPool(PoolGameObjectFactory factory, int maxSize) {
        this.factory = factory;
        this.maxSize = maxSize;
        this.freeObjects = new ArrayList<>(maxSize);
    }

    public GameObject newObject(GameWorld gw) {
        GameObject object = null;

        if (freeObjects.isEmpty()) {
            object = factory.createObject(gw);
        }
        else {
            object = freeObjects.remove(freeObjects.size() - 1);
        }

        return object;
    }

    public void free(GameObject object) {
        if (freeObjects.size() < maxSize)
            freeObjects.add(object);
    }
}
