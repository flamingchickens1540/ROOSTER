package org.team1540.base.commands.drive;

import edu.wpi.first.wpilibj.Joystick;
import org.team1540.base.templates.CtreDrive;

public class AdvancedPidDriveFactory {

  private CtreDrive drive;
  private double maxSetpoint;
  private Joystick joystick;

  private int leftAxis = 1;
  private int rightAxis = 5;
  private int fwdTrigger = 3;
  private int backTrigger = 2;

  private boolean invertLeftAxis = false;
  private boolean invertRightAxis = false;
  private boolean invertLeftOutput = false;
  private boolean invertRightOutput = false;
  private boolean invertFwdTrigger = false;
  private boolean invertBackTrigger = false;

  private double maxBrakePct = 1.00;

  private double deadzone = 0.1;

  private boolean usingBrownoutAlert = true;
  private double maxBrownoutCooldown = 1;

  public AdvancedPidDrive createAdvancedPidDrive() {
    AdvancedPidDrive.Configuration config = new AdvancedPidDrive.Configuration();

    config.drive = this.drive;
    config.maxSetpoint = this.maxSetpoint;
    config.joystick = this.joystick;
    config.leftAxis = this.leftAxis;
    config.rightAxis = this.rightAxis;
    config.fwdTrigger = this.fwdTrigger;
    config.backTrigger = this.backTrigger;
    config.invertLeftAxis = this.invertLeftAxis;
    config.invertRightAxis = this.invertRightAxis;
    config.invertLeftOutput = this.invertLeftOutput;
    config.invertRightOutput = this.invertRightOutput;
    config.invertFwdTrigger = this.invertFwdTrigger;
    config.invertBackTrigger = this.invertBackTrigger;
    config.maxBrakePct = this.maxBrakePct;
    config.deadzone = this.deadzone;
    config.usingBrownoutAlert = this.usingBrownoutAlert;
    config.maxBrownoutCooldown = this.maxBrownoutCooldown;

    return new AdvancedPidDrive(config);
  }

  public CtreDrive getDrive() {
    return drive;
  }

  public AdvancedPidDriveFactory setDrive(CtreDrive drive) {
    this.drive = drive;
    return this;
  }

  public double getMaxSetpoint() {
    return maxSetpoint;
  }

  public AdvancedPidDriveFactory setMaxSetpoint(double maxSetpoint) {
    this.maxSetpoint = maxSetpoint;
    return this;
  }

  public Joystick getJoystick() {
    return joystick;
  }

  public AdvancedPidDriveFactory setJoystick(Joystick joystick) {
    this.joystick = joystick;
    return this;
  }

  public int getLeftAxis() {
    return leftAxis;
  }

  public AdvancedPidDriveFactory setLeftAxis(int leftAxis) {
    this.leftAxis = leftAxis;
    return this;
  }

  public int getRightAxis() {
    return rightAxis;
  }

  public AdvancedPidDriveFactory setRightAxis(int rightAxis) {
    this.rightAxis = rightAxis;
    return this;
  }

  public int getFwdTrigger() {
    return fwdTrigger;
  }

  public AdvancedPidDriveFactory setFwdTrigger(int fwdTrigger) {
    this.fwdTrigger = fwdTrigger;
    return this;
  }

  public int getBackTrigger() {
    return backTrigger;
  }

  public AdvancedPidDriveFactory setBackTrigger(int backTrigger) {
    this.backTrigger = backTrigger;
    return this;
  }

  public boolean isInvertLeftAxis() {
    return invertLeftAxis;
  }

  public AdvancedPidDriveFactory setInvertLeftAxis(boolean invertLeftAxis) {
    this.invertLeftAxis = invertLeftAxis;
    return this;
  }

  public boolean isInvertRightAxis() {
    return invertRightAxis;
  }

  public AdvancedPidDriveFactory setInvertRightAxis(boolean invertRightAxis) {
    this.invertRightAxis = invertRightAxis;
    return this;
  }

  public boolean isInvertLeftOutput() {
    return invertLeftOutput;
  }

  public AdvancedPidDriveFactory setInvertLeftOutput(boolean invertLeftOutput) {
    this.invertLeftOutput = invertLeftOutput;
    return this;
  }

  public boolean isInvertRightOutput() {
    return invertRightOutput;
  }

  public AdvancedPidDriveFactory setInvertRightOutput(boolean invertRightOutput) {
    this.invertRightOutput = invertRightOutput;
    return this;
  }

  public boolean isInvertFwdTrigger() {
    return invertFwdTrigger;
  }

  public AdvancedPidDriveFactory setInvertFwdTrigger(boolean invertFwdTrigger) {
    this.invertFwdTrigger = invertFwdTrigger;
    return this;
  }

  public boolean isInvertBackTrigger() {
    return invertBackTrigger;
  }

  public AdvancedPidDriveFactory setInvertBackTrigger(boolean invertBackTrigger) {
    this.invertBackTrigger = invertBackTrigger;
    return this;
  }

  public double getMaxBrakePct() {
    return maxBrakePct;
  }

  public AdvancedPidDriveFactory setMaxBrakePct(double maxBrakePct) {
    this.maxBrakePct = maxBrakePct;
    return this;
  }

  public double getDeadzone() {
    return deadzone;
  }

  public AdvancedPidDriveFactory setDeadzone(double deadzone) {
    this.deadzone = deadzone;
    return this;
  }

  public boolean isUsingBrownoutAlert() {
    return usingBrownoutAlert;
  }

  public AdvancedPidDriveFactory setUsingBrownoutAlert(boolean usingBrownoutAlert) {
    this.usingBrownoutAlert = usingBrownoutAlert;
    return this;
  }

  public double getMaxBrownoutCooldown() {
    return maxBrownoutCooldown;
  }

  public AdvancedPidDriveFactory setMaxBrownoutCooldown(double maxBrownoutCooldown) {
    this.maxBrownoutCooldown = maxBrownoutCooldown;
    return this;
  }
}
