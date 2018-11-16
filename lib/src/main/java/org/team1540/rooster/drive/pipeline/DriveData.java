package org.team1540.rooster.drive.pipeline;

import java.util.OptionalDouble;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class DriveData {

  /**
   * The desired position in position-units, or an empty optional if velocity should not be
   * controlled.
   */
  public final OptionalDouble position;
  /**
   * The desired velocity in position-units per second, or an empty optional if velocity should not
   * be controlled.
   */
  public final OptionalDouble velocity;
  /**
   * The desired acceleration in position-units per second squared, or an empty optional if
   * acceleration should not be controlled.
   */
  public final OptionalDouble acceleration;

  /**
   * An additional raw amount (from -1 to 1 inclusive) that should be added to motor throttle after
   * any closed-loop logic, or an empty optional if no feed-forward should be added.
   */
  public final OptionalDouble additionalFeedForward;

  public DriveData(OptionalDouble velocity) {
    this(OptionalDouble.empty(), velocity, OptionalDouble.empty(), OptionalDouble.empty());
  }

  public DriveData(OptionalDouble position, OptionalDouble velocity,
      OptionalDouble acceleration, OptionalDouble additionalFeedForward) {
    this.position = position;
    this.velocity = velocity;
    this.acceleration = acceleration;
    this.additionalFeedForward = additionalFeedForward;
  }

  @Override
  public String toString() {
    return "position " + position
        + ", velocity " + velocity
        + ", acceleration " + acceleration
        + ", feedforward " + additionalFeedForward;
  }
}
