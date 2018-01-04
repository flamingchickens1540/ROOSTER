package org.team1540.base.power;

@SuppressWarnings("unused")
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
