package org.team1540.base.testing.zuko;

import edu.wpi.first.wpilibj.Preferences;

public class Tuning {

  private Preferences tuning;

  public Tuning() {
    tuning = Preferences.getInstance();
  }

  public double getFlywheelTargetSpeed() {
    return tuning.getDouble("Flywheel Target Speed", 14000);
  }

  public double getFlywheelSpeedMarginOfError() {
    return tuning.getDouble("Flywheel Speed Margin Of Error", 100);
  }

  public double getFlywheelBackwardsValue() {
    return tuning.getDouble("Flywheel Backwards Value", 0.3);
  }

  public double getFlywheelBackwardsCurrentThreshold() {
    return tuning.getDouble("Flywheel Backwards Current Threshold", 8);
  }

  public double getIntakeRollersValue() {
    return tuning.getDouble("Intake Rollers Value", 1);
  }

  public double getIntakeArmValue() {
    return tuning.getDouble("Intake Arm Value", 0.5);
  }

  public double getIntakeArmCurrentThreshold() {
    return tuning.getDouble("Intake Arm Current Threshold", 5);
  }

}
