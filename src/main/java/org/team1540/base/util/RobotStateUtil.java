package org.team1540.base.util;

import edu.wpi.first.wpilibj.RobotState;

/**
 * Utility class for the {@link #getRobotState()} method, providing an easy way to get the robot's
 * current state (disabled/auto/teleop/test) as an enum.
 */
public class RobotStateUtil {

  /**
   * This class shouldn't be instantiated as it's only static methods.
   */
  private RobotStateUtil() {
  }

  /**
   * Gets the robot's current state.
   *
   * @return A {@link State} corresponding to the robot's current state according to the static
   * methods in the {@link RobotState} class.
   */
  public static State getRobotState() {
    if (RobotState.isEnabled()) {
      if (RobotState.isAutonomous()) {
        return State.AUTONOMOUS;
      } else if (RobotState.isTest()) {
        return State.TEST;
      } else { // RobotState.isOperatorControl() exists but I'm not 100% sure that actually is reflective of teleop or if it includes test mode
        return State.TELEOP;
      }
    } else {
      return State.DISABLED;
    }
  }

  public enum State {
    DISABLED, AUTONOMOUS, TELEOP, TEST;

    public boolean isEnabled() {
      return (this != DISABLED);
    }
  }
}
