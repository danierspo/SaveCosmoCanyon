package com.danil.savecosmocanyon;

import com.danil.savecosmocanyon.entity_component.GameObject;

import java.util.ArrayList;
import java.util.List;

public class CollisionsPool {
    public interface PoolCollisionFactory {
        Collision createObject(GameObject a, GameObject b);
    }

    private final List<Collision> freeObjects;
    private final PoolCollisionFactory factory;
    private final int maxSize;

    public CollisionsPool(PoolCollisionFactory factory, int maxSize) {
        this.factory = factory;
        this.maxSize = maxSize;
        this.freeObjects = new ArrayList<>(maxSize);
    }

    public Collision newObject(GameObject a, GameObject b) {
        Collision object = null;

        if (freeObjects.isEmpty()) {
            object = factory.createObject(a, b);
        }
        else {
            object = freeObjects.remove(freeObjects.size() - 1);
        }

        return object;
    }

    public void free(Collision collision) {
        if (freeObjects.size() < maxSize)
            freeObjects.add(collision);
    }
}
