package org.team1540.rooster.util;

/**
 * Interface for actions that can be passed to a {@link SimpleCommand}.
 */
@FunctionalInterface
public interface Executable {

  /**
   * Perform the required action.
   */
  void execute();
}
