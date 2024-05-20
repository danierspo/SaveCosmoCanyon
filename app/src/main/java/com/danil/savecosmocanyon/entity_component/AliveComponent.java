package com.danil.savecosmocanyon.entity_component;

public class AliveComponent extends Component {
    private final int initialHealth;
    private int currentHealth;
    public int restoreHPs = 0;

    public AliveComponent(int health) {
        this.initialHealth = health;
        this.currentHealth = health;
    }

    @Override
    public ComponentType getType() {
        return ComponentType.ALIVE;
    }

    public int getInitialHealth() {
        return initialHealth;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public void setHealth(int health) {
        this.currentHealth = health;
    }
}
