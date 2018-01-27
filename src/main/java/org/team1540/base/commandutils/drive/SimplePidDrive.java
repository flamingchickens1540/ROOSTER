package org.team1540.base.commandutils.drive;

import static org.team1540.base.Utilities.constrain;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;
import org.team1540.base.templates.Drive;

public class SimplePidDrive extends Command {

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

  public SimplePidDrive(Drive drive, double maxSetpoint, Joystick joystick, int leftAxis,
      int rightAxis, int fwdTrigger, int backTrigger, boolean invertLeftAxis,
      boolean invertRightAxis, boolean invertLeftOutput, boolean invertRightOutput,
      boolean invertFwdTrigger, boolean invertBackTrigger) {
    this.drive = drive;
    this.maxSetpoint = maxSetpoint;
    this.joystick = joystick;
    this.leftAxis = leftAxis;
    this.rightAxis = rightAxis;
    this.fwdTrigger = fwdTrigger;
    this.backTrigger = backTrigger;
    this.invertLeftAxis = invertLeftAxis;
    this.invertRightAxis = invertRightAxis;
    this.invertLeftOutput = invertLeftOutput;
    this.invertRightOutput = invertRightOutput;
    this.invertFwdTrigger = invertFwdTrigger;
    this.invertBackTrigger = invertBackTrigger;
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

  public double getMaxSetpoint() {
    return maxSetpoint;
  }

  public Joystick getJoystick() {
    return joystick;
  }

  public int getLeftAxis() {
    return leftAxis;
  }

  public int getRightAxis() {
    return rightAxis;
  }

  public int getFwdTrigger() {
    return fwdTrigger;
  }

  public int getBackTrigger() {
    return backTrigger;
  }

  public boolean isInvertLeftAxis() {
    return invertLeftAxis;
  }

  public boolean isInvertRightAxis() {
    return invertRightAxis;
  }

  public boolean isInvertLeftOutput() {
    return invertLeftOutput;
  }

  public boolean isInvertRightOutput() {
    return invertRightOutput;
  }

  public boolean isInvertFwdTrigger() {
    return invertFwdTrigger;
  }

  public boolean isInvertBackTrigger() {
    return invertBackTrigger;
  }

  public SimplePidDrive setDrive(Drive drive) {
    this.drive = drive;
    return this;
  }

  public SimplePidDrive setMaxSetpoint(double maxSetpoint) {
    this.maxSetpoint = maxSetpoint;
    return this;
  }

  public SimplePidDrive setJoystick(Joystick joystick) {
    this.joystick = joystick;
    return this;
  }

  public SimplePidDrive setLeftAxis(int leftAxis) {
    this.leftAxis = leftAxis;
    return this;
  }

  public SimplePidDrive setRightAxis(int rightAxis) {
    this.rightAxis = rightAxis;
    return this;
  }

  public SimplePidDrive setFwdTrigger(int fwdTrigger) {
    this.fwdTrigger = fwdTrigger;
    return this;
  }

  public SimplePidDrive setBackTrigger(int backTrigger) {
    this.backTrigger = backTrigger;
    return this;
  }

  public SimplePidDrive setInvertLeftAxis(boolean invertLeftAxis) {
    this.invertLeftAxis = invertLeftAxis;
    return this;
  }

  public SimplePidDrive setInvertRightAxis(boolean invertRightAxis) {
    this.invertRightAxis = invertRightAxis;
    return this;
  }

  public SimplePidDrive setInvertLeftOutput(boolean invertLeftOutput) {
    this.invertLeftOutput = invertLeftOutput;
    return this;
  }

  public SimplePidDrive setInvertRightOutput(boolean invertRightOutput) {
    this.invertRightOutput = invertRightOutput;
    return this;
  }

  public SimplePidDrive setInvertFwdTrigger(boolean invertFwdTrigger) {
    this.invertFwdTrigger = invertFwdTrigger;
    return this;
  }

  public SimplePidDrive setInvertBackTrigger(boolean invertBackTrigger) {
    this.invertBackTrigger = invertBackTrigger;
    return this;
  }
}
