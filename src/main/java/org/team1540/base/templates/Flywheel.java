package org.team1540.base.templates;

/**
 * Generic velocity-controlled PID flywheel. This interface should be implemented by subsystems
 * such as, well, flywheels, that need to PID to a specific velocity. Subsystems implementing this
 * interface can be passed to flywheel-related command creation methods.
 * <p>
 * Note that subsystems implementing this interface should handle the actual PID control
 * internally, whether through CANTalon-based closed-loop control or open-loop PIDs handled in robot
 * code.
 */
public interface Flywheel extends SubsystemAttached {
  /**
   * Gets the current sensor velocity of the flywheel (<i>not</i> the current velocity to PID to.)
   *
   * @return The current velocity of the actuator.
   */
  public double getVelocity();
  /**
   * Sets the velocity of this flyhweel.
   *
   * @param velocity The velocity to PID to.
   */
  public void setVelocity(double velocity);
}