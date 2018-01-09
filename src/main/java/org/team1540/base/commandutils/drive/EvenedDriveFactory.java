package org.team1540.base.commandutils.drive;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;
import org.team1540.base.templates.Drive;

/**
 * Factory class to create side-evened-drive ("smooth drive") commands..
 * <p>
 * The {@code EvenedDriveFactory} is used to produce side-evened-drive commands. To create
 * these commands, you first need a subsystem that implements the {@link
 * Drive} interface. Then, create an {@code EvenedDriveFactory}, and set up its core options (mostly
 * algorithm coefficients) using setters. Finally, create one or
 * more {@link Command Commands} using the command creation method.
 * <p>
 * All setters in this class use a builder pattern; that is, they return an instance of the class
 * they are called on. This can be used to chain method calls easily by calling another setter
 * method on the return value of previous setter methods.
 * <p>
 * Commands prouced by this class are entirely independent; any subsequent changes to factory
 * parameters using the setter methods will not affect commands that have already been built.
 */
public class EvenedDriveFactory {
  private double confidenceCoeff;
  /**
   * How much the setpoint will decrease per tick when PID error is high.
   */
  private double decreaseCoeff;
  private Drive drive;
  /**
   * How much the requested speed will increase per tick when PID error is low.
   */
  private double increaseCoeff;
  private Joystick joystick;
  /**
   * Error above which the drivetrain will decrease the requested speed.
   */
  private double maxError;
  private double minConfidence;

  /**
   * Creates an evened-drive command.
   *
   * @param joystick The joystick to use for control.
   * @param leftAxis The joystick axis to use for left-side control.
   * @param rightAxis The joystick axis to use for right-side control.
   *
   * @return A {@code Command} that when started or set as a default command drives the currently
   *     set {@link Drive} instance around nicely.
   */
  public Command createEvenedDriveCommand(Joystick joystick, int leftAxis, int rightAxis) {
    return new DriveEvenedCommand(drive, joystick, leftAxis, rightAxis, increaseCoeff,
        decreaseCoeff, maxError, confidenceCoeff, minConfidence);
  }


  /**
   * Gets the confidence coefficient used by all evened-drive commands. The confidence coefficient
   * is a unitless quantity that controls how quickly the drive's confidence in its setpoint
   * increases.
   *
   * @return The currently set confidence coefficient.
   */
  public double getConfidenceCoeff() {
    return confidenceCoeff;
  }

  /**
   * Sets the confidence coefficient used by all evened-drive commands. The confidence coefficient
   * is a unitless quantity that controls how quickly the drive's confidence in its setpoint
   * increases.
   *
   * @param confidenceCoeff The coefficient to set.
   *
   * @return An instance of this {@code EvenedDriveFactory} in a builder pattern.
   */
  public EvenedDriveFactory setConfidenceCoeff(double confidenceCoeff) {
    this.confidenceCoeff = confidenceCoeff;
    return this;
  }

  /**
   * Gets the decreasing coefficient used by all even-drive commands. The decreasing coefficient
   * controls how quickly the drive's setpoint decreases when error is high.
   *
   * @return The currently set decreasing coefficient, in velocity units per tick.
   */
  public double getDecreaseCoeff() {
    return decreaseCoeff;
  }

  /**
   * Sets the decreasing coefficient used by all even-drive commands. The decreasing coefficient
   * controls how quickly the drive's setpoint decreases when error is high.
   *
   * @param decreaseCoeff The decreasing coefficient, in velocity units per tick.
   *
   * @return An instance of this {@code EvenedDriveFactory} in a builder pattern.
   */
  public EvenedDriveFactory setDecreaseCoeff(double decreaseCoeff) {
    this.decreaseCoeff = decreaseCoeff;
    return this;
  }

  /**
   * Gets the {@link Drive} instance used by all even-drive commands.
   *
   * @return The currently set {@code Drive} instance.
   */
  public Drive getDrive() {
    return drive;
  }

  /**
   * Sets the {@link Drive} instance used by all even-drive commands.
   *
   * @param drive The {@code Drive} instance to set.
   *
   * @return An instance of this {@code EvenedDriveFactory} in a builder pattern.
   */
  public EvenedDriveFactory setDrive(Drive drive) {
    this.drive = drive;
    return this;
  }

  /**
   * Gets the increasing coefficient used by all even-drive commands. The increasing coefficient
   * governs how much the requested speed will increase per tick when PID error is low.
   *
   * @return The currently set increasing coefficient, in velocity units per tick.
   */
  public double getIncreaseCoeff() {
    return increaseCoeff;
  }

  /**
   * Sets the increasing coefficient used by all even-drive commands. The increasing coefficient
   * governs how much the requested speed will increase per tick when PID error is low.
   *
   * @param increaseCoeff The increasing coefficient to set, in velocity units per tick.
   *
   * @return An instance of this {@code EvenedDriveFactory} in a builder pattern.
   */
  public EvenedDriveFactory setIncreaseCoeff(double increaseCoeff) {
    this.increaseCoeff = increaseCoeff;
    return this;
  }

  /**
   * Sets the maximum error used by all even-drive commands. The maximum error governs the error
   * above which the drivetrain will decrease the requested speed, and below which the drivetrain
   * will increase it.
   *
   * @return The currently set maximum error, in velocity units.
   */
  public double getMaxError() {
    return maxError;
  }

  /**
   * Sets the maximum error used by all even-drive commands. The maximum error governs the error
   * above which the drivetrain will decrease the requested speed, and below which the drivetrain
   * will increase it.
   *
   * @param maxError The maximum error to set, in velocity units.
   *
   * @return An instance of this {@code EvenedDriveFactory} in a builder pattern.
   */
  public EvenedDriveFactory setMaxError(double maxError) {
    this.maxError = maxError;
    return this;
  }

  /**
   * Gets the minimum confidence used by all even-drive commands. The minimum confidence is a
   * unitless quantity that governs the confidence above which the drivetrain will also use the PID
   * setpoint for turning without calibrating as it goes.
   *
   * @return The currently set minimum confidence.
   */
  public double getMinConfidence() {
    return minConfidence;
  }

  /**
   * Sets the minimum confidence used by all even-drive commands. The minimum confidence is a
   * unitless quantity that governs the confidence above which the drivetrain will also use the PID
   * setpoint for turning without calibrating as it goes.
   *
   * @param minConfidence The minimum confidence to set.
   *
   * @return An instance of this {@code EvenedDriveFactory} in a builder pattern.
   */
  public EvenedDriveFactory setMinConfidence(double minConfidence) {
    this.minConfidence = minConfidence;
    return this;
  }
}
