package com.danil.savecosmocanyon.entity_component;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
public class Entity {
    private Map<ComponentType, Component> components;

    public Entity() {
        components = new HashMap<>();
    }

    public void addComponent(Component component) {
        component.setOwner(this);
        components.put(component.getType(), component);
    }

    public Component getComponent(ComponentType type) {
        return components.get(type);
    }

    public void removeComponent(ComponentType type) {
        components.remove(type);
    }

    public void removeComponent(Component component) {
        components.remove(component.getType());
    }

    public Map<ComponentType, Component> getComponents() {
        return components;
    }

    public void setComponents(Map<ComponentType, Component> components) {
        this.components = components;
    }

    public void draw(Bitmap buffer, float x, float y, float angle)  {
        try {
            ((DrawableComponent) Objects.requireNonNull(this.components.get(ComponentType.DRAWABLE))).draw(buffer, x, y, angle);
        } catch (NullPointerException ignored) {

        }
    }
}
