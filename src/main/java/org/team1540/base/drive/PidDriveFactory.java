package org.team1540.base.drive;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.team1540.base.wrappers.ChickenController;

public class PidDriveFactory {

  private Subsystem subsystem;
  private ChickenController left;
  private ChickenController right;

  private double maxVel;
  private JoystickScaling scaling;
  private double maxBrakePct;
  private boolean invertLeftBrakeDirection;
  private boolean invertRightBrakeDirection;
  private double brakingStopZone;

  private Joystick joystick;
  private int leftAxis;
  private boolean invertLeft;
  private int rightAxis;
  private boolean invertRight;
  private int forwardTrigger;
  private int backTrigger;
  private double deadzone;
  private double brakeOverrideThresh;

  public Subsystem getSubsystem() {
    return subsystem;
  }

  public PidDriveFactory setSubsystem(Subsystem subsystem) {
    this.subsystem = subsystem;
    return this;
  }

  public ChickenController getLeft() {
    return left;
  }

  public PidDriveFactory setLeft(ChickenController left) {
    this.left = left;
    return this;
  }

  public ChickenController getRight() {
    return right;
  }

  public PidDriveFactory setRight(ChickenController right) {
    this.right = right;
    return this;
  }

  public double getMaxVel() {
    return maxVel;
  }

  public PidDriveFactory setMaxVel(double maxVel) {
    this.maxVel = maxVel;
    return this;
  }

  public JoystickScaling getScaling() {
    return scaling;
  }

  public PidDriveFactory setScaling(JoystickScaling scaling) {
    this.scaling = scaling;
    return this;
  }

  public double getMaxBrakePct() {
    return maxBrakePct;
  }

  public PidDriveFactory setMaxBrakePct(double maxBrakePct) {
    this.maxBrakePct = maxBrakePct;
    return this;
  }

  public boolean isInvertLeftBrakeDirection() {
    return invertLeftBrakeDirection;
  }

  public PidDriveFactory setInvertLeftBrakeDirection(boolean invertLeftBrakeDirection) {
    this.invertLeftBrakeDirection = invertLeftBrakeDirection;
    return this;
  }

  public boolean isInvertRightBrakeDirection() {
    return invertRightBrakeDirection;
  }

  public PidDriveFactory setInvertRightBrakeDirection(boolean invertRightBrakeDirection) {
    this.invertRightBrakeDirection = invertRightBrakeDirection;
    return this;
  }

  public double getBrakingStopZone() {
    return brakingStopZone;
  }

  public PidDriveFactory setBrakingStopZone(double brakingStopZone) {
    this.brakingStopZone = brakingStopZone;
    return this;
  }

  public Joystick getJoystick() {
    return joystick;
  }

  public PidDriveFactory setJoystick(Joystick joystick) {
    this.joystick = joystick;
    return this;
  }

  public int getLeftAxis() {
    return leftAxis;
  }

  public PidDriveFactory setLeftAxis(int leftAxis) {
    this.leftAxis = leftAxis;
    return this;
  }

  public boolean isInvertLeft() {
    return invertLeft;
  }

  public PidDriveFactory setInvertLeft(boolean invertLeft) {
    this.invertLeft = invertLeft;
    return this;
  }

  public int getRightAxis() {
    return rightAxis;
  }

  public PidDriveFactory setRightAxis(int rightAxis) {
    this.rightAxis = rightAxis;
    return this;
  }

  public boolean isInvertRight() {
    return invertRight;
  }

  public PidDriveFactory setInvertRight(boolean invertRight) {
    this.invertRight = invertRight;
    return this;
  }

  public int getForwardTrigger() {
    return forwardTrigger;
  }

  public PidDriveFactory setForwardTrigger(int forwardTrigger) {
    this.forwardTrigger = forwardTrigger;
    return this;
  }

  public int getBackTrigger() {
    return backTrigger;
  }

  public PidDriveFactory setBackTrigger(int backTrigger) {
    this.backTrigger = backTrigger;
    return this;
  }

  public double getDeadzone() {
    return deadzone;
  }

  public PidDriveFactory setDeadzone(double deadzone) {
    this.deadzone = deadzone;
    return this;
  }

  public double getBrakeOverrideThresh() {
    return brakeOverrideThresh;
  }

  public PidDriveFactory setBrakeOverrideThresh(double brakeOverrideThresh) {
    this.brakeOverrideThresh = brakeOverrideThresh;
    return this;
  }

  public PidDrive createPidDrive() {
    PidDriveConfiguration config = new PidDriveConfiguration();
    config.subsystem = subsystem;
    config.left = left;
    config.right = right;
    config.maxVel = maxVel;
    config.scaling = scaling;
    config.maxBrakePct = maxBrakePct;
    config.invertLeftBrakeDirection = invertLeftBrakeDirection;
    config.invertRightBrakeDirection = invertRightBrakeDirection;
    config.brakingStopZone = brakingStopZone;
    config.joystick = joystick;
    config.leftAxis = leftAxis;
    config.invertLeft = invertLeft;
    config.rightAxis = rightAxis;
    config.invertRight = invertRight;
    config.forwardTrigger = forwardTrigger;
    config.backTrigger = backTrigger;
    config.deadzone = deadzone;
    config.brakeOverrideThresh = brakeOverrideThresh;
    return new PidDrive(config);
  }
}
