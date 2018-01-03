package org.team1540.base;

import edu.wpi.first.wpilibj.command.Subsystem;
import org.team1540.base.power.PowerManageable;


public class ChickenSubsystem extends Subsystem implements PowerManageable {

  private double priority = 0.0;

  public ChickenSubsystem(String name) {
    super(name);
  }

  public ChickenSubsystem() {
  }

  @Override
  protected void initDefaultCommand() {

  }

  /**
   * Compare two ChickenCommands by priority.
   *
   * @param o ChickenCommand to compare to.
   *
   * @return priority.compareTo(o.priority)
   */
  public int compareTo(ChickenSubsystem o) {
    return Double.compare((priority), o.priority);
  }

  public double getPriority() {
    return priority;
  }

  public void setPriority(double priority) {
    this.priority = priority;
  }

  @Override
  public double getCurrent() {
    return 0.0;
  }

  @Override
  public void limitPower(double limit) {

  }

  @Override
  public void stopLimitingPower() {

  }

  /**
   * Compare two PowerManageables by priority.
   *
   * @param o PowerManageables to compare to.
   * @return priority.compareTo(o.priority)
   */
  public int compareTo(PowerManageable o) {
    return Double.compare((priority), o.getPriority());
  }
}
