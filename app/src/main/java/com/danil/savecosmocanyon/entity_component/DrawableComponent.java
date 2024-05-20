package com.danil.savecosmocanyon.entity_component;

import android.graphics.Bitmap;

import com.danil.savecosmocanyon.GameWorld;

public abstract class DrawableComponent extends Component {
    protected String name;
    protected GameWorld gw;

    @Override
    public ComponentType getType() {
        return ComponentType.DRAWABLE;
    }

    public abstract void draw(Bitmap buffer, float x, float y, float angle);
}
