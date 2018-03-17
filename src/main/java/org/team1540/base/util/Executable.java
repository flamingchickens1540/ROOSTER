package org.team1540.base.util;

/**
 * Interface for simple no-argument, no-return-value actions such as those used by a
 * {@link SimpleCommand}.
 */
@FunctionalInterface
public interface Executable {

  /**
   * Perform the required action.
   */
  public void execute();
}
