package com.danil.savecosmocanyon.entity_component;

public abstract class TouchableComponent extends Component {
    public abstract void doAtTouch();

    @Override
    public ComponentType getType() {
        return ComponentType.TOUCHABLE;
    }
}
