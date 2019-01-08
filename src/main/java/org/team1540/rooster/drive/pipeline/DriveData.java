package org.team1540.rooster.drive.pipeline;

import java.util.Objects;
import java.util.OptionalDouble;
import java.util.function.Function;
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
   * Create a new {@code DriveData} with all fields empty.
   */
  public DriveData() {
    this(OptionalDouble.empty(), OptionalDouble.empty(), OptionalDouble.empty(),
        OptionalDouble.empty());
  }

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

  /**
   * Creates a copy of this {@code DriveData} with a different {@link #position} value (all other
   * fields remain the same).
   *
   * @param position The new value for {@link #position}.
   * @return A new {@code DriveData} as described above.
   */
  @NotNull
  @Contract(value = "_ -> new", pure = true)
  public DriveData withPosition(double position) {
    return new DriveData(OptionalDouble.of(position), velocity, acceleration,
        additionalFeedForward);
  }

  /**
   * Creates a copy of this {@code DriveData} with a different {@link #velocity} value (all other
   * fields remain the same).
   *
   * @param velocity The new value for {@link #velocity}.
   * @return A new {@code DriveData} as described above.
   */
  @NotNull
  @Contract(value = "_ -> new", pure = true)
  public DriveData withVelocity(double velocity) {
    return new DriveData(position, OptionalDouble.of(velocity), acceleration,
        additionalFeedForward);
  }

  /**
   * Creates a copy of this {@code DriveData} with a different {@link #acceleration} value (all
   * other fields remain the same).
   *
   * @param acceleration The new value for {@link #acceleration}.
   * @return A new {@code DriveData} as described above.
   */
  @NotNull
  @Contract(value = "_ -> new", pure = true)
  public DriveData withAcceleration(double acceleration) {
    return new DriveData(position, velocity, OptionalDouble.of(acceleration),
        additionalFeedForward);
  }

  /**
   * Creates a copy of this {@code DriveData} with a different {@link #additionalFeedForward} value
   * (all other fields remain the same).
   *
   * @param additionalFeedForward The new value for {@link #additionalFeedForward}.
   * @return A new {@code DriveData} as described above.
   */
  @NotNull
  @Contract(value = "_ -> new", pure = true)
  public DriveData withAdditionalFeedForward(double additionalFeedForward) {
    return new DriveData(position, velocity, acceleration,
        OptionalDouble.of(additionalFeedForward));
  }

  /**
   * Creates a copy of this {@code DriveData} with a modified {@link #position}.
   *
   * @param function A {@link Function} that takes this {@code DriveData}'s current {@link
   * #position} and returns a new {@link #position}.
   * @return A copy of this {@code DriveData} with all fields identical except for the {@link
   * #position} field.
   */
  @NotNull
  @Contract(value = "_ -> new", pure = true)
  public DriveData modifyPosition(@NotNull Function<OptionalDouble, OptionalDouble> function) {
    return new DriveData(function.apply(position), velocity, acceleration, additionalFeedForward);
  }

  /**
   * Creates a copy of this {@code DriveData} with a modified {@link #velocity}.
   *
   * @param function A {@link Function} that takes this {@code DriveData}'s current {@link
   * #velocity} and returns a new {@link #velocity}.
   * @return A copy of this {@code DriveData} with all fields identical except for the {@link
   * #velocity} field.
   */
  @NotNull
  @Contract(value = "_ -> new", pure = true)
  public DriveData modifyVelocity(@NotNull Function<OptionalDouble, OptionalDouble> function) {
    return new DriveData(position, function.apply(velocity), acceleration, additionalFeedForward);
  }

  /**
   * Creates a copy of this {@code DriveData} with a modified {@link #acceleration}.
   *
   * @param function A {@link Function} that takes this {@code DriveData}'s current {@link
   * #acceleration} and returns a new {@link #acceleration}.
   * @return A copy of this {@code DriveData} with all fields identical except for the {@link
   * #acceleration} field.
   */
  @NotNull
  @Contract(value = "_ -> new", pure = true)
  public DriveData modifyAcceleration(@NotNull Function<OptionalDouble, OptionalDouble> function) {
    return new DriveData(position, velocity, function.apply(acceleration), additionalFeedForward);
  }

  /**
   * Creates a copy of this {@code DriveData} with a modified {@link #additionalFeedForward}.
   *
   * @param function A {@link Function} that takes this {@code DriveData}'s current {@link
   * #additionalFeedForward} and returns a new {@link #additionalFeedForward}.
   * @return A copy of this {@code DriveData} with all fields identical except for the {@link
   * #additionalFeedForward} field.
   */
  @NotNull
  @Contract(value = "_ -> new", pure = true)
  public DriveData modifyAdditionalFeedForward(
      @NotNull Function<OptionalDouble, OptionalDouble> function) {
    return new DriveData(position, velocity, acceleration, function.apply(additionalFeedForward));
  }

  /**
   * Adds the provided value to this {@code DriveData}'s {@link #position}. If there is already a
   * value present in {@link #position}, the returned {@code DriveData}'s {@link #position} will be
   * equal to the sum of that value plus the parameter; otherwise, the {@link #position} will be
   * equal to the value of the provided position parameter.
   *
   * @param position The position value to add.
   * @return A new {@link DriveData} as described above.
   */
  @NotNull
  @Contract(value = "_ -> new", pure = true)
  public DriveData plusPosition(double position) {
    return modifyPosition((old) -> OptionalDouble.of(old.orElse(0) + position));
  }


  /**
   * Adds the provided value to this {@code DriveData}'s {@link #velocity}. If there is already a
   * value present in {@link #velocity}, the returned {@code DriveData}'s {@link #velocity} will be
   * equal to the sum of that value plus the parameter; otherwise, the {@link #velocity} will be
   * equal to the value of the provided velocity parameter.
   *
   * @param velocity The velocity value to add.
   * @return A new {@link DriveData} as described above.
   */
  @NotNull
  @Contract(value = "_ -> new", pure = true)
  public DriveData plusVelocity(double velocity) {
    return modifyVelocity((old) -> OptionalDouble.of(old.orElse(0) + velocity));
  }

  /**
   * Adds the provided value to this {@code DriveData}'s {@link #acceleration}. If there is already
   * a value present in {@link #acceleration}, the returned {@code DriveData}'s {@link
   * #acceleration} will be equal to the sum of that value plus the parameter; otherwise, the {@link
   * #acceleration} will be equal to the value of the provided acceleration parameter.
   *
   * @param acceleration The acceleration value to add.
   * @return A new {@link DriveData} as described above.
   */
  @NotNull
  @Contract(value = "_ -> new", pure = true)
  public DriveData plusAcceleration(double acceleration) {
    return modifyAcceleration((old) -> OptionalDouble.of(old.orElse(0) + acceleration));
  }

  /**
   * Adds the provided value to this {@code DriveData}'s {@link #additionalFeedForward}. If there is
   * already a value present in {@link #additionalFeedForward}, the returned {@code DriveData}'s
   * {@link #additionalFeedForward} will be equal to the sum of that value plus the parameter;
   * otherwise, the {@link #additionalFeedForward} will be equal to the value of the provided
   * additionalFeedForward parameter.
   *
   * @param additionalFeedForward The additionalFeedForward value to add.
   * @return A new {@link DriveData} as described above.
   */
  @NotNull
  @Contract(value = "_ -> new", pure = true)
  public DriveData plusAdditionalFeedForward(double additionalFeedForward) {
    return modifyAdditionalFeedForward(
        (old) -> OptionalDouble.of(old.orElse(0) + additionalFeedForward));
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
