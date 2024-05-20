package com.danil.savecosmocanyon.entity_component;

public abstract class Component {
    protected Entity owner;
    public abstract ComponentType getType();

    public Entity getOwner() {
        return owner;
    }

    public void setOwner(Entity newOwner) {
        owner = newOwner;
    }
}
