package org.team1540.base.commands.drive;

import edu.wpi.first.wpilibj.Joystick;
import org.team1540.base.templates.Drive;

public class PidDriveFactory {

  private Drive drive;
  private double maxSetpoint;
  private boolean invertLeftAxis = false;
  private boolean invertRightAxis = false;
  private boolean invertLeftOutput = false;
  private boolean invertRightOutput = false;
  private boolean invertFwdTrigger = false;
  private boolean invertBackTrigger = false;

  public PidDrive createSimplePidDrive(Joystick joystick, int leftAxis, int rightAxis,
      int fwdTrigger, int backTrigger) {

    PidDrive.Configuration configuration = new PidDrive.Configuration();

    configuration.drive = drive;
    configuration.maxSetpoint = maxSetpoint;
    configuration.joystick = joystick;
    configuration.leftAxis = leftAxis;
    configuration.rightAxis = rightAxis;
    configuration.fwdTrigger = fwdTrigger;
    configuration.backTrigger = backTrigger;
    configuration.invertLeftAxis = invertLeftAxis;
    configuration.invertRightAxis = invertRightAxis;
    configuration.invertFwdTrigger = invertFwdTrigger;
    configuration.invertBackTrigger = invertBackTrigger;

    return new PidDrive(configuration);
  }

  public Drive getDrive() {
    return drive;
  }

  public PidDriveFactory setDrive(Drive drive) {
    this.drive = drive;
    return this;
  }

  public double getMaxSetpoint() {
    return maxSetpoint;
  }

  public PidDriveFactory setMaxSetpoint(double maxSetpoint) {
    this.maxSetpoint = maxSetpoint;
    return this;
  }

  public boolean isInvertLeftAxis() {
    return invertLeftAxis;
  }

  public PidDriveFactory setInvertLeftAxis(boolean invertLeftAxis) {
    this.invertLeftAxis = invertLeftAxis;
    return this;
  }

  public boolean isInvertRightAxis() {
    return invertRightAxis;
  }

  public PidDriveFactory setInvertRightAxis(boolean invertRightAxis) {
    this.invertRightAxis = invertRightAxis;
    return this;
  }

  public boolean isInvertLeftOutput() {
    return invertLeftOutput;
  }

  public PidDriveFactory setInvertLeftOutput(boolean invertLeftOutput) {
    this.invertLeftOutput = invertLeftOutput;
    return this;
  }

  public boolean isInvertRightOutput() {
    return invertRightOutput;
  }

  public PidDriveFactory setInvertRightOutput(boolean invertRightOutput) {
    this.invertRightOutput = invertRightOutput;
    return this;
  }

  public boolean isInvertFwdTrigger() {
    return invertFwdTrigger;
  }

  public PidDriveFactory setInvertFwdTrigger(boolean invertFwdTrigger) {
    this.invertFwdTrigger = invertFwdTrigger;
    return this;
  }

  public boolean isInvertBackTrigger() {
    return invertBackTrigger;
  }

  public PidDriveFactory setInvertBackTrigger(boolean invertBackTrigger) {
    this.invertBackTrigger = invertBackTrigger;
    return this;
  }
}
