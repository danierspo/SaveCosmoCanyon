package com.danil.savecosmocanyon.entity_component;

import com.danil.savecosmocanyon.GameWorld;
import com.google.fpl.liquidfun.Body;

public abstract class PhysicsBodyComponent extends Component {
    public GameWorld gw;
    public Body body;
    public float x, y, width, height;

    @Override
    public ComponentType getType() {
        return ComponentType.PHYSICS_BODY;
    }
}
