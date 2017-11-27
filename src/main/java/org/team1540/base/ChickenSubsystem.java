package org.team1540.base;

import edu.wpi.first.wpilibj.command.Subsystem;

public abstract class ChickenSubsystem extends Subsystem {

  protected abstract double getCurrent();

  protected abstract void limitPower(double limit);

  protected abstract void stopLimitingPower();
}
