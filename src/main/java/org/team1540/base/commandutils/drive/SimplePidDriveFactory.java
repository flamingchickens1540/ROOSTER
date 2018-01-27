package org.team1540.base.commandutils.drive;

import edu.wpi.first.wpilibj.Joystick;
import org.team1540.base.templates.Drive;

public class SimplePidDriveFactory {

  private Drive drive;
  private double maxSetpoint;
  private boolean invertLeftAxis = false;
  private boolean invertRightAxis = false;
  private boolean invertLeftOutput = false;
  private boolean invertRightOutput = false;
  private boolean invertFwdTrigger = false;
  private boolean invertBackTrigger = false;

  public SimplePidDrive createSimplePidDrive(Joystick joystick, int leftAxis, int rightAxis,
      int fwdTrigger, int backTrigger) {
    return new SimplePidDrive(drive, maxSetpoint, joystick, leftAxis, rightAxis, fwdTrigger,
        backTrigger, invertLeftAxis, invertRightAxis, invertLeftOutput, invertRightOutput,
        invertFwdTrigger, invertBackTrigger);
  }

  public Drive getDrive() {
    return drive;
  }

  public SimplePidDriveFactory setDrive(Drive drive) {
    this.drive = drive;
    return this;
  }

  public double getMaxSetpoint() {
    return maxSetpoint;
  }

  public SimplePidDriveFactory setMaxSetpoint(double maxSetpoint) {
    this.maxSetpoint = maxSetpoint;
    return this;
  }

  public boolean isInvertLeftAxis() {
    return invertLeftAxis;
  }

  public SimplePidDriveFactory setInvertLeftAxis(boolean invertLeftAxis) {
    this.invertLeftAxis = invertLeftAxis;
    return this;
  }

  public boolean isInvertRightAxis() {
    return invertRightAxis;
  }

  public SimplePidDriveFactory setInvertRightAxis(boolean invertRightAxis) {
    this.invertRightAxis = invertRightAxis;
    return this;
  }

  public boolean isInvertLeftOutput() {
    return invertLeftOutput;
  }

  public SimplePidDriveFactory setInvertLeftOutput(boolean invertLeftOutput) {
    this.invertLeftOutput = invertLeftOutput;
    return this;
  }

  public boolean isInvertRightOutput() {
    return invertRightOutput;
  }

  public SimplePidDriveFactory setInvertRightOutput(boolean invertRightOutput) {
    this.invertRightOutput = invertRightOutput;
    return this;
  }

  public boolean isInvertFwdTrigger() {
    return invertFwdTrigger;
  }

  public SimplePidDriveFactory setInvertFwdTrigger(boolean invertFwdTrigger) {
    this.invertFwdTrigger = invertFwdTrigger;
    return this;
  }

  public boolean isInvertBackTrigger() {
    return invertBackTrigger;
  }

  public SimplePidDriveFactory setInvertBackTrigger(boolean invertBackTrigger) {
    this.invertBackTrigger = invertBackTrigger;
    return this;
  }
}
