package org.team1540.rooster.drive;

import org.team1540.rooster.drive.pipeline.Processor;

@FunctionalInterface
public interface JoystickScaling extends Processor<Double, Double> {

  public double scale(double input);

  public default Double apply(Double input) {
    return scale(input);
  }
}
