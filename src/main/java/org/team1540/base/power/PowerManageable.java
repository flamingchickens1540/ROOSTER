package org.team1540.base.power;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

@SuppressWarnings("unused")
public interface PowerManageable extends Comparable<PowerManageable>, Sendable {

  /**
   * Get the priority of this PowerManageable. Used for power management.
   *
   * @return The priority of this PowerManageable.
   */
  double getPriority();

  /**
   * Sets the priority of this PowerManageable. Used for power management.
   *
   * @param priority The priority to set.
   */
  void setPriority(double priority);

  double getVoltage();

  /**
   * Set an absolute voltage limit for this PowerManageable.
   *
   * @param limit The voltage limit, from 0 to some number in volts (inclusive.)
   */
  void setVoltageLimit(double limit);

  /**
   * Stop limiting the power.
   */
  void stopLimitingPower();


  /**
   * Compare two PowerManageables by priority.
   *
   * @param o PowerManageables to compare to.
   * @return (int) (getPriority() - o.getPriority())
   */
  @Override
  default int compareTo(PowerManageable o) {
    return (int) (getPriority() - o.getPriority());
  }

  @Override
  default void initSendable(SendableBuilder builder) {
    builder.setSmartDashboardType("PowerManageable");
    builder.addDoubleProperty("priority", this::getPriority, this::setPriority);
    builder.addDoubleProperty("voltage", this::getVoltage, null);
  }

}
