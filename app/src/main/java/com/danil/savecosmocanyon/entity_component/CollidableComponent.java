package com.danil.savecosmocanyon.entity_component;

public abstract class CollidableComponent extends Component {
    public abstract void doAtCollision(Entity collider);

    @Override
    public ComponentType getType() {
        return ComponentType.COLLIDABLE;
    }
}
