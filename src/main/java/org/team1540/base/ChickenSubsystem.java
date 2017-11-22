package org.team1540.base;

import edu.wpi.first.wpilibj.command.Subsystem;

import static org.team1540.base.ChickenSubsystem.PowerLimitMethod.CEILING;

public abstract class ChickenSubsystem extends Subsystem {

  private final PowerLimitMethod powerLimitMethod = PowerLimitMethod.LINEAR_SCALE;

  public double getCurrent() {
    // TODO
    return 0;
  }

  public void setAbsolutePowerLimit(double limit) {
    if (powerLimitMethod == CEILING) {
      // TODO
      // Implicitly LINEAR_SCALE
    } else {
      // TODO
    }
  }

  enum PowerLimitMethod {
    CEILING, LINEAR_SCALE;
  }

  public void stopLimitingPower() {
    // TODO
  }
}
