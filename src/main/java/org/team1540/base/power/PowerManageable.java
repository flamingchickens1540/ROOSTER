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

  /**
   * Gets the current percent output that the motors are being capped at.
   *
   * @return The current percent output from 0 to 1 (not enforced!)
   */
  double getPercentOutputLimit();

  /**
   * Set the percent of the current power draw this motor can draw.
   * e.g. if you were drawing .5 and set this to .5, you'll draw .25
   * @param limit The percent of the current power draw to draw.
   */
  void setPercentOutputLimit(double limit);

  /**
   * Stop limiting the power.
   */
  void stopLimitingPower();

  /**
   * Gets the class responsible for grabbing the power telemetry, including power, current, and
   * voltage.
   *
   * @return The PowerTelemetry, or null if it does not exist.
   */
  PowerTelemetry getPowerTelemetry();

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
    sendablePowerInfo(builder);
  }

  default void sendablePowerInfo(SendableBuilder builder) {
    builder.setSmartDashboardType("PowerManageable");
    builder.addDoubleProperty("priority", this::getPriority, this::setPriority);
    builder.addDoubleProperty("percentOutputLimit", this::getPercentOutputLimit,
        this::setPercentOutputLimit
    );
  }

}
