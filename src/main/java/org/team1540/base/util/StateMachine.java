package org.team1540.base.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;

public class StateMachine<T extends Enum<T>> {

  private Map<T, State> states = new HashMap<>();

  private T currentState;

  public StateMachine(T initialState) {
    this.currentState = initialState;
  }

  public void run() {
    State current = states.get(currentState);
    // check if we are in a place to transition out of this state
    for (Transition<T> transition : current.transitions) {
      if (transition.condition.getAsBoolean()) {
        if (current.onExit != null) {
          current.onExit.execute();
        }

        currentState = transition.transitionTo;

        current = states.get(currentState);
        if (current.onEntry != null) {
          current.onEntry.execute();
        }

        break;
      }
    }

    if (current.periodic != null) {
      current.periodic.execute();
    }
  }

  @SafeVarargs
  public final void putState(T state, Transition<T>... transitions) {
    putState(state, null, null, null, transitions);
  }

  @SafeVarargs
  public final void putState(T state, Executable onEntry, Executable onExit,
      Transition<T>... transitions) {
    putState(state, onEntry, onExit, null, transitions);
  }

  @SafeVarargs
  public final void putState(T state, Executable periodic, Transition<T>... transitions) {
    putState(state, null, null, periodic, transitions);
  }

  @SafeVarargs
  public final void putState(T state, Executable onEntry, Executable onExit, Executable periodic,
      Transition<T>... transitions) {
    states.put(state,
        new State(onEntry, onExit, periodic, new HashSet<>(Arrays.asList(transitions))));
  }

  private class State {

    public State(Executable onEntry, Executable onExit, Executable periodic,
        Set<Transition<T>> transitions) {
      this.onEntry = onEntry;
      this.onExit = onExit;
      this.periodic = periodic;
      this.transitions = transitions;
    }

    Executable onEntry;
    Executable onExit;
    Executable periodic;
    Set<Transition<T>> transitions;
  }

  public static class Transition<S extends Enum<S>> {

    private BooleanSupplier condition;
    private S transitionTo;

    public Transition(BooleanSupplier condition, S transitionTo) {
      this.condition = condition;
      this.transitionTo = transitionTo;
    }
  }
}
