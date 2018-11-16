package org.team1540.rooster.motionprofiling;

import jaci.pathfinder.Trajectory;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

/**
 * @deprecated Replaced by the {@link FollowProfile}-based system.
 */
@Deprecated
public class MotionProfilingProperties {

  private double encoderTicksPerUnit = 1023 * 0.1 * Math.PI;

  private double secondsFromNeutralToFull = 0;

  private DoubleSupplier getEncoderVelocityFunction;
  private DoubleConsumer setMotorVelocityFunction;

  // Okay so this is actually pretty much unused, but here because we kinda might plan on using it
  // in the future and it's useful to have
  private DoubleSupplier getEncoderPositionFunction;

  private Trajectory trajectory;

  private double currentTime = 0;

  public MotionProfilingProperties(DoubleSupplier getEncoderVelocityFunction,
      DoubleConsumer setMotorVelocityFunction, DoubleSupplier getEncoderPositionFunction,
      Trajectory trajectory) {
    this.getEncoderVelocityFunction = getEncoderVelocityFunction;
    this.setMotorVelocityFunction = setMotorVelocityFunction;
    this.getEncoderPositionFunction = getEncoderPositionFunction;
    this.trajectory = trajectory;
  }

  public MotionProfilingProperties(double encoderTicksPerUnit, double secondsFromNeutralToFull,
      DoubleSupplier getEncoderVelocityFunction, DoubleConsumer setMotorVelocityFunction,
      DoubleSupplier getEncoderPositionFunction, Trajectory trajectory) {
    this.encoderTicksPerUnit = encoderTicksPerUnit;
    this.secondsFromNeutralToFull = secondsFromNeutralToFull;
    this.getEncoderVelocityFunction = getEncoderVelocityFunction;
    this.setMotorVelocityFunction = setMotorVelocityFunction;
    this.getEncoderPositionFunction = getEncoderPositionFunction;
    this.trajectory = trajectory;
  }

  /**
   * Get the number of encoder ticks per revolution of the wheel. This is roughly equal to the
   * number of encoder ticks per rev * the wheel diameter * pi.
   *
   * @return The number of encoder ticks in native units.
   */
  public double getEncoderTicksPerUnit() {
    return encoderTicksPerUnit;
  }

  /**
   * Set the number of encoder ticks per unit distance travelled by the wheel. You can calculate
   * this by doing the number of encoder ticks per rev * the wheel diameter * pi.
   *
   * @param encoderTicksPerUnit The number of encoder ticks in native units.
   */
  public void setEncoderTicksPerUnit(double encoderTicksPerUnit) {
    this.encoderTicksPerUnit = encoderTicksPerUnit;
  }

  /**
   * Get the ratio of encoder ticks to distance travelled by the wheel. Multiply this number by a
   * number of encoder ticks to get the distance travelled by the wheel.
   *
   * @return The ratio in encoder ticks / unit used in the Trajectory.
   */
  public double getEncoderTickRatio() {
    return (1 / encoderTicksPerUnit);
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
