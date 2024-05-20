package com.danil.savecosmocanyon.entity_component;

import android.content.Context;
import android.util.SparseArray;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class FSMParser {
    private SparseArray<State> states;
    private final Context context;

    public FSMParser(Context context) {
        this.context = context;
    }

    public FSM createFSM(String fileJSON){
        FSM fsm = null;

        try {
            InputStream inputStream = context.getAssets().open(fileJSON);
            JsonObject jsonResults = new JsonParser().parse(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).getAsJsonObject();

            // States creation
            JsonArray jsonArrayState = jsonResults.getAsJsonArray("state");
            states = new SparseArray<>();
            for (int i=0; i<jsonArrayState.size(); i++) {
                createStates(jsonArrayState.get(i).getAsJsonObject());
            }

            // Transitions creation
            JsonArray jsonArrayTransition = jsonResults.getAsJsonArray("transition");
            for (int i=0; i<jsonArrayTransition.size(); i++){
                createTransitions(jsonArrayTransition.get(i).getAsJsonObject());
            }

            JsonElement jsonElementStart = jsonResults.get("initial_state");
            State initialState = null;

            if (jsonElementStart != null) {
                for (int i = 0; i < states.size(); i++) {
                    State state = states.valueAt(i);
                    if (state.name.equals(jsonElementStart.getAsString())) {
                        initialState = state;
                    }
                }
            }

            if (initialState != null) {
                fsm = new FSM(initialState);
            } else {
                throw new IllegalArgumentException("Incorrect state");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fsm;
    }


    private void createStates(JsonObject jsonObject){
        String name = jsonObject.get("name").getAsString();
        String action = jsonObject.get("action").getAsString();
        State state;

        if (name != null && action != null) {
            switch (action) {
                case "waited":
                    state = new State(name, Action.waited);
                    break;

                case "pushed":
                    state = new State(name, Action.pushed);
                    break;

                default:
                    throw new IllegalStateException("Action '" + action + "' not supported");
            }

            states.put(state.hashCode(), state);
        }
    }

    private void createTransitions(JsonObject jsonObject) {
        String fromState = jsonObject.get("from").getAsString();
        String targetState = jsonObject.get("to").getAsString();
        String action = jsonObject.get("action").getAsString();
        Transition transition = null;

        if (fromState != null && targetState != null) {
            State fState = null;
            State tState = null;

            for (int i = 0; i < states.size(); i++) {
                State state = states.valueAt(i);
                if (state.name.equals(fromState)) {
                    fState = state;
                } else {
                    if (state.name.equals(targetState)) {
                        tState = state;
                    }
                }
            }

            if (fState != null && tState != null) {
                if (action != null) {
                    switch (action) {
                        case "waited":
                            transition = new Transition(fState, tState,Action.waited);
                            break;

                        case "pushed":
                            transition = new Transition(fState, tState,Action.pushed);
                            break;
                    }
                } else {
                    transition = new Transition(fState, tState,null);
                }

                fState.addOutputTransition(transition);
            }
        }
    }
}
