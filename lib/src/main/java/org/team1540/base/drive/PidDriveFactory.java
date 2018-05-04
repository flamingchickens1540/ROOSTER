package org.team1540.base.drive;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.team1540.base.wrappers.ChickenController;

/**
 * Factory class for producing {@link PidDrive} instances.
 *
 * @see PidDrive
 */
public class PidDriveFactory {

  private Subsystem subsystem;
  private ChickenController left;
  private ChickenController right;

  private double maxVel;
  private JoystickScaling scaling = new PowerJoystickScaling(2);
  private double maxBrakePct = 0.1;
  private boolean invertLeftBrakeDirection = false;
  private boolean invertRightBrakeDirection = false;
  private double brakingStopZone = 0.1;

  private Joystick joystick;
  private int leftAxis;
  private boolean invertLeft = false;
  private int rightAxis;
  private boolean invertRight = false;
  private int forwardTrigger;
  private int backTrigger;
  private double deadzone = 0.1;
  private double brakeOverrideThresh = 0.9;

  public Subsystem getSubsystem() {
    return subsystem;
  }

  /**
   * Sets the subsystem used by this command for command creation.
   */
  public PidDriveFactory setSubsystem(Subsystem subsystem) {
    this.subsystem = subsystem;
    return this;
  }

  public ChickenController getLeft() {
    return left;
  }

  /**
   * Sets the left motor controller.
   *
   * @param left The motor controller to set.
   * @return An instance of this {@code PidDriveFactory} in a builder pattern.
   */
  public PidDriveFactory setLeft(ChickenController left) {
    this.left = left;
    return this;
  }

  public ChickenController getRight() {
    return right;
  }

  /**
   * Sets the right motor controller.
   *
   * @param right The motor controller to set.
   * @return An instance of this {@code PidDriveFactory} in a builder pattern.
   */
  public PidDriveFactory setRight(ChickenController right) {
    this.right = right;
    return this;
  }

  public double getMaxVel() {
    return maxVel;
  }

  /**
   * Set the maximum velocity of the powered drive mechanism.
   *
   * @param maxVel The maximum velocity, in motor controller native units.
   * @return An instance of this {@code PidDriveFactory} in a builder pattern.
   */
  public PidDriveFactory setMaxVel(double maxVel) {
    this.maxVel = maxVel;
    return this;
  }

  public JoystickScaling getScaling() {
    return scaling;
  }

  /**
   * Set the scaling function to apply to joystick inputs. Default: a {@link PowerJoystickScaling}
   * with a power of 2.
   *
   * @param scaling The scaling function.
   * @return An instance of this {@code PidDriveFactory} in a builder pattern.
   */
  public PidDriveFactory setScaling(JoystickScaling scaling) {
    this.scaling = scaling;
    return this;
  }

  public double getMaxBrakePct() {
    return maxBrakePct;
  }

  /**
   * Set the maximum allowable backdriving throttle. Defaults to 0.1.
   *
   * @param maxBrakePct The max throttle, in percent.
   * @return An instance of this {@code PidDriveFactory} in a builder pattern.
   */
  public PidDriveFactory setMaxBrakePct(double maxBrakePct) {
    this.maxBrakePct = maxBrakePct;
    return this;
  }

  public boolean isInvertLeftBrakeDirection() {
    return invertLeftBrakeDirection;
  }

  /**
   * Sets whether to invert the left-side braking algorithm forwards direction. Defaults to {@code
   * false}.
   *
   * @param invertLeftBrakeDirection Whether or not to invert.
   * @return An instance of this {@code PidDriveFactory} in a builder pattern.
   */
  public PidDriveFactory setInvertLeftBrakeDirection(boolean invertLeftBrakeDirection) {
    this.invertLeftBrakeDirection = invertLeftBrakeDirection;
    return this;
  }

  public boolean isInvertRightBrakeDirection() {
    return invertRightBrakeDirection;
  }

  /**
   * Sets whether to invert the right-side braking algorithm forwards detection. Defaults to {@code
   * false}.
   *
   * @param invertRightBrakeDirection Whether to invert.
   * @return An instance of this {@code PidDriveFactory} in a builder pattern.
   */
  public PidDriveFactory setInvertRightBrakeDirection(boolean invertRightBrakeDirection) {
    this.invertRightBrakeDirection = invertRightBrakeDirection;
    return this;
  }

  public double getBrakingStopZone() {
    return brakingStopZone;
  }

  /**
   * Sets the zone where the braking algorithm will consider the drive "stopped" for the purpose of
   * determining braking. When the drive is stopped, the motors will not be output-limited. Defaults
   * to 0.1.
   *
   * @param brakingStopZone The stop zone, in a fraction of the set max velocity.
   * @return An instance of this {@code PidDriveFactory} in a builder pattern.
   */
  public PidDriveFactory setBrakingStopZone(double brakingStopZone) {
    this.brakingStopZone = brakingStopZone;
    return this;
  }

  public Joystick getJoystick() {
    return joystick;
  }

  /**
   * Sets the joystick to use for command control.
   *
   * @param joystick The joystick.
   * @return An instance of this {@code PidDriveFactory} in a builder pattern.
   */
  public PidDriveFactory setJoystick(Joystick joystick) {
    this.joystick = joystick;
    return this;
  }

  public int getLeftAxis() {
    return leftAxis;
  }

  /**
   * Sets the joystick axis used for controlling the left-side motors.
   *
   * @param leftAxis The axis to use.
   * @return An instance of this {@code PidDriveFactory} in a builder pattern.
   */
  public PidDriveFactory setLeftAxis(int leftAxis) {
    this.leftAxis = leftAxis;
    return this;
  }

  public boolean isInvertLeft() {
    return invertLeft;
  }

  /**
   * Sets whether to invert the left joystick input. Defaults to {@code false}.
   *
   * @param invertLeft Whether or not to invert.
   * @return An instance of this {@code PidDriveFactory} in a builder pattern.
   */
  public PidDriveFactory setInvertLeft(boolean invertLeft) {
    this.invertLeft = invertLeft;
    return this;
  }

  public int getRightAxis() {
    return rightAxis;
  }

  /**
   * Sets the axis used for controlling the right-side motors.
   *
   * @param rightAxis The axis to use.
   * @return An instance of this {@code PidDriveFactory} in a builder pattern.
   */
  public PidDriveFactory setRightAxis(int rightAxis) {
    this.rightAxis = rightAxis;
    return this;
  }

  public boolean isInvertRight() {
    return invertRight;
  }

  /**
   * Sets whether to invert the right joystick input. Defaults to {@code false}.
   *
   * @param invertRight Whether or not to invert.
   * @return An instance of this {@code PidDriveFactory} in a builder pattern.
   */
  public PidDriveFactory setInvertRight(boolean invertRight) {
    this.invertRight = invertRight;
    return this;
  }

  public int getForwardTrigger() {
    return forwardTrigger;
  }

  /**
   * Sets the trigger axis used to apply straight forwards power.
   *
   * @param forwardTrigger The axis to set.
   * @return An instance of this {@code PidDriveFactory} in a builder pattern.
   */
  public PidDriveFactory setForwardTrigger(int forwardTrigger) {
    this.forwardTrigger = forwardTrigger;
    return this;
  }

  public int getBackTrigger() {
    return backTrigger;
  }

  /**
   * Sets the joystick axis used to apply straight backwards power.
   *
   * @param backTrigger The axis to set.
   * @return An instance of this {@code PidDriveFactory} in a builder pattern.
   */
  public PidDriveFactory setBackTrigger(int backTrigger) {
    this.backTrigger = backTrigger;
    return this;
  }

  public double getDeadzone() {
    return deadzone;
  }

  /**
   * Sets the joystick deadzone (joystick inputs within this distance from 0 will be ignored).
   * Defaults to 0.1.
   *
   * @param deadzone The deadzone to set
   * @return An instance of this {@code PidDriveFactory} in a builder pattern.
   */
  public PidDriveFactory setDeadzone(double deadzone) {
    this.deadzone = deadzone;
    return this;
  }

  public double getBrakeOverrideThresh() {
    return brakeOverrideThresh;
  }

  /**
   * Sets the joystick threshold above which the robot will apply full reverse power to fight moving
   * backwards. Defaults to 0.9.
   *
   * @param brakeOverrideThresh The threshold to set.
   * @return An instance of this {@code PidDriveFactory} in a builder pattern.
   */
  public PidDriveFactory setBrakeOverrideThresh(double brakeOverrideThresh) {
    this.brakeOverrideThresh = brakeOverrideThresh;
    return this;
  }

  /**
   * Creates a new {@code PidDrive} instance with previously-specified parameters.
   *
   * @return A new PidDrive.
   */
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
