package org.team1540.base.util;

/**
 * Interface for actions that can be passed to a {@link SimpleCommand}.
 */
@FunctionalInterface
public interface Executable {

  /**
   * Perform the required action.
   */
  public void execute();
}
