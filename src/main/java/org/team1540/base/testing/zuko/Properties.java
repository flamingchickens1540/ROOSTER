package org.team1540.base.testing.zuko;

import jaci.pathfinder.Trajectory;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

public class Properties {

  private double encoderTicksPerRev = 1023;

  private double wheelDiameter = 0.1;
  private double secondsFromNeutralToFull = 0;

  private DoubleSupplier getEncoderVelocityFunction;
  private DoubleConsumer setMotorVelocityFunction;

  // Okay so this is actually pretty much unused, but here because we kinda might plan on using it
  // in the future and it's useful to have
  private DoubleSupplier getEncoderPositionFunction;

  private Trajectory trajectory;

  private double currentTime = 0;

  public Properties(DoubleSupplier getEncoderVelocityFunction,
      DoubleConsumer setMotorVelocityFunction, DoubleSupplier getEncoderPositionFunction,
      Trajectory trajectory) {
    this.getEncoderVelocityFunction = getEncoderVelocityFunction;
    this.setMotorVelocityFunction = setMotorVelocityFunction;
    this.getEncoderPositionFunction = getEncoderPositionFunction;
    this.trajectory = trajectory;
  }

  public Properties(double encoderTicksPerRev,
      double wheelDiameter, double secondsFromNeutralToFull,
      DoubleSupplier getEncoderVelocityFunction,
      DoubleConsumer setMotorVelocityFunction, DoubleSupplier getEncoderPositionFunction,
      Trajectory trajectory) {
    this.encoderTicksPerRev = encoderTicksPerRev;
    this.wheelDiameter = wheelDiameter;
    this.secondsFromNeutralToFull = secondsFromNeutralToFull;
    this.getEncoderVelocityFunction = getEncoderVelocityFunction;
    this.setMotorVelocityFunction = setMotorVelocityFunction;
    this.getEncoderPositionFunction = getEncoderPositionFunction;
    this.trajectory = trajectory;
  }

  /**
   * Get the number of encoder ticks per revolution of the wheel.
   *
   * @return The number of encoder ticks in native units.
   */
  public double getEncoderTicksPerRev() {
    return encoderTicksPerRev;
  }

  /**
   * Set the number of encoder ticks per revolution of the wheel.
   *
   * @param encoderTicksPerRev The number of encoder ticks in native units.
   */
  public void setEncoderTicksPerRev(double encoderTicksPerRev) {
    this.encoderTicksPerRev = encoderTicksPerRev;
  }

  /**
   * Get the wheel diameter.
   *
   * @return The wheel diameter.
   */
  public double getWheelDiameter() {
    return wheelDiameter;
  }

  /**
   * Set the wheel diameter.
   *
   * @param wheelDiameter The wheel diameter in the same units as the Trajectory.
   */
  public void setWheelDiameter(double wheelDiameter) {
    this.wheelDiameter = wheelDiameter;
  }

  /**
   * Get the ratio of encoder ticks to distance travelled by the wheel. Multiply this number by a
   * number of encoder ticks to get the distance travelled by the wheel.
   *
   * @return The ratio in encoder ticks / unit used in the Trajectory.
   */
  public double getEncoderTickRatio() {
    return (1 / encoderTicksPerRev) * (wheelDiameter * Math.PI);
  }

  public Trajectory getTrajectory() {
    return trajectory;
  }

  public double getSecondsFromNeutralToFull() {
    return secondsFromNeutralToFull;
  }

  public void setSecondsFromNeutralToFull(double secondsFromNeutralToFull) {
    this.secondsFromNeutralToFull = secondsFromNeutralToFull;
  }

  public DoubleSupplier getGetEncoderVelocityFunction() {
    return getEncoderVelocityFunction;
  }

  /**
   * Sets the function that sets the encoder velocity. This function will be passed the velocity in
   * native units / decisecond.
   *
   * @param getEncoderVelocityFunction DoubleSupplier that sets the encoder velocity.
   */
  public void setGetEncoderVelocityFunction(DoubleSupplier getEncoderVelocityFunction) {
    this.getEncoderVelocityFunction = getEncoderVelocityFunction;
  }

  public DoubleConsumer getSetMotorVelocityFunction() {
    return setMotorVelocityFunction;
  }

  public void setSetMotorVelocityFunction(DoubleConsumer setMotorVelocityFunction) {
    this.setMotorVelocityFunction = setMotorVelocityFunction;
  }

  public DoubleSupplier getGetEncoderPositionFunction() {
    return getEncoderPositionFunction;
  }

  public void setGetEncoderPositionFunction(DoubleSupplier getEncoderPositionFunction) {
    this.getEncoderPositionFunction = getEncoderPositionFunction;
  }

}