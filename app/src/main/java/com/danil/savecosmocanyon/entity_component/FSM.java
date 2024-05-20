package com.danil.savecosmocanyon.entity_component;

import android.util.SparseArray;

import com.danil.savecosmocanyon.GameWorld;

public class FSM {
    private State state;

    public FSM(State state){
        this.state = state;
    }

    public Action stepAndGetAction(GameWorld gw){
        Transition transitionTrigger = null;

        SparseArray<Transition> outputTransitions = state.getOutputTransitions();
        for (int i = 0; i < outputTransitions.size(); i++) {
            Transition transition = outputTransitions.valueAt(i);
            if (transition.isTriggered(gw)){
                transitionTrigger = transition;
                break;
            }
        }

        if (transitionTrigger != null) {
            state = transitionTrigger.getTargetState();
            return transitionTrigger.action;
        } else {
            return state.action;
        }
    }
}

class State {
    protected String name;
    protected Action action;
    protected SparseArray<Transition> outputTransitions;

    public State(String name, Action activeAction) {
        this.name = name;
        this.action = activeAction;
        this.outputTransitions = new SparseArray<>();
    }

    public Action getAction() {
        return action;
    }

    public SparseArray<Transition> getOutputTransitions() {
        return outputTransitions;
    }

    public void addOutputTransition(Transition transition) {
        this.outputTransitions.put(transition.hashCode(), transition);
    }
}

class Transition {
    protected Action action;
    protected State fromState;
    protected State targetState;

    public Transition(State fromState, State targetState, Action action) {
        this.action = action;
        this.fromState = fromState;
        this.targetState = targetState;
    }

    public boolean isTriggered(GameWorld gw) {
       Action action = targetState.getAction();

        if (action.equals(Action.pushed)) {
            return (gw.getLeftMeteorsList().size() + gw.getRightMeteorsList().size()) != 0;
        } else {
            if (action.equals(Action.waited)) {
                return (gw.getLeftMeteorsList().size() + gw.getRightMeteorsList().size()) == 0;
            }
        }

        return false;
    }

    public State getTargetState() {
        return targetState;
    }
}
