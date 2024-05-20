package com.danil.savecosmocanyon;

import com.danil.savecosmocanyon.entity_component.CollidableComponent;
import com.danil.savecosmocanyon.entity_component.ComponentType;
import com.danil.savecosmocanyon.entity_component.Entity;
import com.danil.savecosmocanyon.entity_component.GameObject;
import com.danil.savecosmocanyon.entity_component.PhysicsBodyComponent;
import com.google.fpl.liquidfun.Contact;
import com.google.fpl.liquidfun.ContactListener;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by mfaella on 01/03/16.
 * Modified by adding a collisions pool by danierspo on 29/05/2022.
 */
public class MyContactListener extends ContactListener {
    private static final int POOL_MAX_SIZE = 10;
    public final CollisionsPool collisionsPool;
    private final Collection<Collision> cache = new HashSet<>();

    public MyContactListener() {
        this.collisionsPool = new CollisionsPool(Collision::new, POOL_MAX_SIZE);
    }

    public Collection<Collision> getCollisions() {
        Collection<Collision> result = new HashSet<>(cache);
        cache.clear();
        return result;
    }

    /** Warning: this method runs inside world.step
     *  Hence, it cannot change the physical world.
     */
    @Override
    public void beginContact(Contact contact) {
        Entity entityA = ((PhysicsBodyComponent) contact.getFixtureA().getBody().getUserData()).getOwner();
        Entity entityB = ((PhysicsBodyComponent) contact.getFixtureB().getBody().getUserData()).getOwner();
        CollidableComponent collidableComponentA = (CollidableComponent) entityA.getComponent(ComponentType.COLLIDABLE);
        CollidableComponent collidableComponentB = (CollidableComponent) entityB.getComponent(ComponentType.COLLIDABLE);
        if (collidableComponentA != null) {
            collidableComponentA.doAtCollision(entityB);
        }
        if (collidableComponentB != null) {
            collidableComponentB.doAtCollision(entityA);
        }

        cache.add(collisionsPool.newObject((GameObject) entityA, (GameObject) entityB));
    }
}
