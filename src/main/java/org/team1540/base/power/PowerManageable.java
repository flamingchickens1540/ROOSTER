package org.team1540.base.power;

public interface PowerManageable extends Comparable<PowerManageable> {

  /**
   * Get the priority of this PowerManageable. Used for power management.
   */
  double getPriority();

  /**
   * Sets the priority of this PowerManageable. Used for power management.
   */
  void setPriority(double priority);

  double getCurrent();

  /**
   * Set a power limit for this PowerManageable.
   *
   * @param limit The power limit in amps.
   */
  void limitPower(double limit);

  /**
   * Stop limiting the power.
   */
  void stopLimitingPower();

}
