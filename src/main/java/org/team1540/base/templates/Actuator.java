package org.team1540.base.templates;

/**
 * Generic position-PID-controlled actuator. This interface should be implemented by subsystems
 * such as claws and arms that need to move to a specific position. Subsystems implementing this
 * interface can be passed to actuator-related command creation methods.
 * <p>
 * Note that subsystems implementing this interface should handle the actual PID control internally,
 * whether through CANTalon-based closed-loop control or open-loop PIDs handled in robot code.
 */
public interface Actuator extends SubsystemAttached {
  /**
   * Gets the current sensor position of the actuator (<i>not</i> the current position to PID to.)
   *
   * @return The current positon of the actuator.
   */
  public double getPosition();
  /**
   * Sets the position of this actuator.
   *
   * @param position The position to move to.
   */
  public void setPosition(double position);
  /**
   * Zeros the position of the actuator. On {@code CANTalon}-based actuators this is as simple as
   * calling {@code setPosition(0)} on each of your non-slave Talons, but may be more involved
   * depending on the motor controller you are working with.
   */
  public void zeroPosition();
}
