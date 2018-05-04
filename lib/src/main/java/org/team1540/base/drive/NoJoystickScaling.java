package org.team1540.base.drive;

/**
 * {@code JoystickScaling} implementation that does nothing; just returns the input.
 */
public class NoJoystickScaling implements JoystickScaling {

  @Override
  public double scale(double input) {
    return input;
  }
}
