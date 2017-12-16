package org.team1540.base;

import edu.wpi.first.wpilibj.command.Subsystem;

public abstract class ChickenSubsystem extends Subsystem {

  public ChickenSubsystem(String name) {
    super(name);
  }

  public ChickenSubsystem() {
  }

  protected abstract double getCurrent();

  /**
   * Set a power limit for this subsystem.
   *
   * @param limit The power limit in amps.
   */
  protected abstract void limitPower(double limit);

  protected abstract void stopLimitingPower();
}
