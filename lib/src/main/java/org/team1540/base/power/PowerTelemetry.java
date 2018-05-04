package org.team1540.base.power;

public interface PowerTelemetry {

  /**
   * Gets the total current draw.
   *
   * @return The current draw in amps.
   */
  double getCurrent();

  /**
   * Gets the average voltage.
   *
   * @return The average voltage in volts.
   */
  double getVoltage();

  default double getPower() {
    return getCurrent() * getVoltage();
  }

}
