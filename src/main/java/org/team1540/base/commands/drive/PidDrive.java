package org.team1540.base.commands.drive;

import static org.team1540.base.Utilities.constrain;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;
import org.team1540.base.templates.Drive;

public class PidDrive extends Command {

  private Drive drive;
  private double maxSetpoint;
  private Joystick joystick;
  private int leftAxis;
  private int rightAxis;
  private int fwdTrigger;
  private int backTrigger;
  private boolean invertLeftAxis;
  private boolean invertRightAxis;
  private boolean invertLeftOutput;
  private boolean invertRightOutput;
  private boolean invertFwdTrigger;
  private boolean invertBackTrigger;

  public PidDrive(Configuration config) {
    this.drive = config.drive;
    this.maxSetpoint = config.maxSetpoint;
    this.joystick = config.joystick;
    this.leftAxis = config.leftAxis;
    this.rightAxis = config.rightAxis;
    this.fwdTrigger = config.fwdTrigger;
    this.backTrigger = config.backTrigger;
    this.invertLeftAxis = config.invertLeftAxis;
    this.invertRightAxis = config.invertRightAxis;
    this.invertLeftOutput = config.invertLeftOutput;
    this.invertRightOutput = config.invertRightOutput;
    this.invertFwdTrigger = config.invertFwdTrigger;
    this.invertBackTrigger = config.invertBackTrigger;
    requires(drive.getAttachedSubsystem());
  }

  @Override
  protected void execute() {
    double leftPct = joystick.getRawAxis(leftAxis) * (invertLeftAxis ? -1 : 1);
    double rightPct = joystick.getRawAxis(rightAxis) * (invertRightAxis ? -1 : 1);

    double forwardAdd = joystick.getRawAxis(fwdTrigger) * (invertFwdTrigger ? -1 : 1);
    double backAdd = joystick.getRawAxis(backTrigger) * (invertBackTrigger ? -1 : 1);

    double leftOutput = (leftPct + forwardAdd - backAdd) * (invertLeftOutput ? -1 : 1);
    double rightOutput = (rightPct + forwardAdd - backAdd) * (invertRightOutput ? -1 : 1);

    drive.setLeftVelocity(constrain(leftOutput, 1));
    drive.setRightVelocity(constrain(rightOutput, 1));
  }

  @Override
  protected boolean isFinished() {
    return false;
  }


  public Drive getDrive() {
    return drive;
  }

  public PidDrive setDrive(Drive drive) {
    this.drive = drive;
    return this;
  }

  public double getMaxSetpoint() {
    return maxSetpoint;
  }

  public PidDrive setMaxSetpoint(double maxSetpoint) {
    this.maxSetpoint = maxSetpoint;
    return this;
  }

  public Joystick getJoystick() {
    return joystick;
  }

  public PidDrive setJoystick(Joystick joystick) {
    this.joystick = joystick;
    return this;
  }

  public int getLeftAxis() {
    return leftAxis;
  }

  public PidDrive setLeftAxis(int leftAxis) {
    this.leftAxis = leftAxis;
    return this;
  }

  public int getRightAxis() {
    return rightAxis;
  }

  public PidDrive setRightAxis(int rightAxis) {
    this.rightAxis = rightAxis;
    return this;
  }

  public int getFwdTrigger() {
    return fwdTrigger;
  }

  public PidDrive setFwdTrigger(int fwdTrigger) {
    this.fwdTrigger = fwdTrigger;
    return this;
  }

  public int getBackTrigger() {
    return backTrigger;
  }

  public PidDrive setBackTrigger(int backTrigger) {
    this.backTrigger = backTrigger;
    return this;
  }

  public boolean isInvertLeftAxis() {
    return invertLeftAxis;
  }

  public PidDrive setInvertLeftAxis(boolean invertLeftAxis) {
    this.invertLeftAxis = invertLeftAxis;
    return this;
  }

  public boolean isInvertRightAxis() {
    return invertRightAxis;
  }

  public PidDrive setInvertRightAxis(boolean invertRightAxis) {
    this.invertRightAxis = invertRightAxis;
    return this;
  }

  public boolean isInvertLeftOutput() {
    return invertLeftOutput;
  }

  public PidDrive setInvertLeftOutput(boolean invertLeftOutput) {
    this.invertLeftOutput = invertLeftOutput;
    return this;
  }

  public boolean isInvertRightOutput() {
    return invertRightOutput;
  }

  public PidDrive setInvertRightOutput(boolean invertRightOutput) {
    this.invertRightOutput = invertRightOutput;
    return this;
  }

  public boolean isInvertFwdTrigger() {
    return invertFwdTrigger;
  }

  public PidDrive setInvertFwdTrigger(boolean invertFwdTrigger) {
    this.invertFwdTrigger = invertFwdTrigger;
    return this;
  }

  public boolean isInvertBackTrigger() {
    return invertBackTrigger;
  }

  public PidDrive setInvertBackTrigger(boolean invertBackTrigger) {
    this.invertBackTrigger = invertBackTrigger;
    return this;
  }

  static class Configuration extends PidDriveConfiguration {

    Drive drive;
  }
}
