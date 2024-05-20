package com.danil.savecosmocanyon.entity_component;

import com.danil.savecosmocanyon.GameWorld;

public abstract class AIComponent extends Component {
    protected GameWorld gw;

    public abstract void handleAI(float x);

    @Override
    public ComponentType getType() {
        return ComponentType.AI;
    }
}
