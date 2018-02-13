package org.team1540.base.power;

@SuppressWarnings("unused")
public interface PowerManageable extends Comparable<PowerManageable> {

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
   * Set a percent output limit for this PowerManageable.
   *
   * @param limit The percent output limit, from 0 to 1 (inclusive.)
   */
  void setLimit(double limit);

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

}
