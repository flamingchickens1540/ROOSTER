package org.team1540.base.templates;

/**
 * Generic PID-controlled drive train. This interface should be implemented by drivetrain
 * subsystems.
 */
public interface Drive extends SubsystemAttached {
  /**
   * Gets the current sensor velocity of the left-side motors (<i>not</i> the current velocity to
   * PID to.)
   *
   * @return The current velocity of the left-side motors.
   */
  public double getLeftVelocity();
  /**
   * Gets the current sensor velocity of the right-side motors (<i>not</i> the current velocity to
   * PID to.)
   *
   * @return The current velocity of the right-side motors.
   */
  public double getRightVelocity();
  /**
   * Sets the velocity on the left-side motors. This is usually accomplished with closed-loop
   * velocity control on CANTalons but may be more involved with other methods.
   *
   * @param velocity The motor velocity, in revolutions per second.
   */
  public void setLeftVelocity(double velocity);
  /**
   * Sets the throttle on the left-side motors.
   *
   * @param throttle The motor throttle, from -1.0 to 1.0 inclusive.
   */
  public void setLeftThrottle(double throttle);
  /**
   * Sets the throttle on the right-side motors.
   *
   * @param throttle The motor throttle, from -1.0 to 1.0 inclusive.
   */
  public void setRightThrottle(double throttle);
  /**
   * Sets the velocity on the right-side motors. This is usually accomplished with closed-loop
   * velocity control on CANTalons but may be more involved with other methods.
   *
   * @param velocity The motor velocity, in revolutions per second.
   */
  public void setRightVelocity(double velocity);
}
