package org.team1540.rooster.drive.pipeline;

import java.util.Objects;
import java.util.OptionalDouble;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Encapsulates data for one side of a tank drivetrain.
 *
 * {@code DriveData} instances are typically passed around as members of {@link TankDriveData}
 * instances in drive pipelines. Each {@code DriveData} instance contains values (or empty {@link
 * OptionalDouble OptionalDoubles}) for  {@linkplain #position}, {@linkplain #velocity}, {@linkplain
 * #acceleration}, and {@linkplain #additionalFeedForward feed-forward}.
 *
 * @see TankDriveData
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class DriveData {

  /**
   * The desired position in position-units, or an empty optional if velocity should not be
   * controlled.
   */
  @NotNull
  public final OptionalDouble position;
  /**
   * The desired velocity in position-units per second, or an empty optional if velocity should not
   * be controlled.
   */
  @NotNull
  public final OptionalDouble velocity;
  /**
   * The desired acceleration in position-units per second squared, or an empty optional if
   * acceleration should not be controlled.
   */
  @NotNull
  public final OptionalDouble acceleration;

  /**
   * An additional raw amount (from -1 to 1 inclusive) that should be added to motor throttle after
   * any closed-loop logic, or an empty optional if no feed-forward should be added.
   */
  @NotNull
  public final OptionalDouble additionalFeedForward;

  /**
   * Create a new {@code DriveData} with all fields empty except for the provided velocity.
   *
   * @param velocity The desired velocity in position-units per second, or an empty optional if
   * velocity should not be controlled.
   */
  public DriveData(@NotNull OptionalDouble velocity) {
    this(OptionalDouble.empty(), velocity, OptionalDouble.empty(), OptionalDouble.empty());
  }

  /**
   * Create a new {@code DriveData} with the supplied values.
   *
   * @param position The desired position in position-units, or an empty optional if velocity should
   * not be controlled.
   * @param velocity The desired velocity in position-units per second, or an empty optional if
   * velocity should not be controlled.
   * @param acceleration The desired acceleration in position-units per second squared, or an empty
   * optional if acceleration should not be controlled.
   * @param additionalFeedForward An additional raw amount (from -1 to 1 inclusive) that should be
   * added to motor throttle after any closed-loop logic, or an empty optional if no feed-forward
   * should be added.
   */
  public DriveData(@NotNull OptionalDouble position, @NotNull OptionalDouble velocity,
      @NotNull OptionalDouble acceleration, @NotNull OptionalDouble additionalFeedForward) {
    this.position = Objects.requireNonNull(position);
    this.velocity = Objects.requireNonNull(velocity);
    this.acceleration = Objects.requireNonNull(acceleration);
    this.additionalFeedForward = Objects.requireNonNull(additionalFeedForward);
  }

  @Override
  public String toString() {
    return (position.isPresent() ? "position " + position.getAsDouble() : "")
        + (velocity.isPresent() ? ", velocity " + velocity.getAsDouble() : "")
        + (acceleration.isPresent() ? ", acceleration " + acceleration.getAsDouble() : "")
        + (additionalFeedForward.isPresent() ? ", feedforward " + additionalFeedForward
        .getAsDouble() : "");
  }

  @Override
  @Contract(value = "null -> false", pure = true)
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DriveData)) {
      return false;
    }
    DriveData driveData = (DriveData) o;
    return position.equals(driveData.position) &&
        velocity.equals(driveData.velocity) &&
        acceleration.equals(driveData.acceleration) &&
        additionalFeedForward.equals(driveData.additionalFeedForward);
  }

  @Override
  public int hashCode() {
    return Objects.hash(position, velocity, acceleration, additionalFeedForward);
  }
}
