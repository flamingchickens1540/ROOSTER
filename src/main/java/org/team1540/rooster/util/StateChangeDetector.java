package org.team1540.rooster.util;

public class StateChangeDetector {

    public boolean oldState;

    public StateChangeDetector(boolean startState) {
        this.oldState = startState;
    }

    public boolean getOldState() {
        return oldState;
    }

    public boolean didChange(boolean currentState) {
        if (currentState != oldState) {
            oldState = currentState;
            return true;
        }
        return false;
    }

    public boolean didChangeToTrue(boolean currentState) {
        if (!oldState && currentState) {
            oldState = true;
            return true;
        }
        return false;
    }

    public boolean didChangeToFalse(boolean currentState) {
        if (oldState && !currentState) {
            oldState = false;
            return true;
        }
        return false;
    }
}
