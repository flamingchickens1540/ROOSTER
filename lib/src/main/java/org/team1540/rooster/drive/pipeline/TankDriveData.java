package org.team1540.rooster.drive.pipeline;

import java.util.Objects;
import java.util.OptionalDouble;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Encapsulates drive commands for a tank drive.
 *
 * {@code TankDriveData} instances are usually passed around in drive pipelines. Each {@code
 * TankDriveData} instance contains {@link DriveData} instances for the left and right side, as well
 * as commands for {@linkplain #heading} and {@linkplain #turningRate turning rate}.
 *
 * @see DriveData
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class TankDriveData {

  /**
   * The drive data for the left side.
   */
  @NotNull
  public final DriveData left;
  /**
   * The drive data for the right side.
   */
  @NotNull
  public final DriveData right;
  /**
   * The desired heading in radians from 0 (straight forward) to 2&pi;, increasing clockwise, or an
   * empty optional if heading should not be controlled.
   */
  @NotNull
  public final OptionalDouble heading;
  /**
   * The desired turning rate in radians/sec, or an empty optional if turning rate should not be
   * controlled.
   */
  @NotNull
  public final OptionalDouble turningRate;

  /**
   * Creates a new {@code TankDriveData} with the supplied values.
   *
   * @param left The {@link DriveData} for the left side.
   * @param right The {@link DriveData} for the right side.
   * @param heading The desired heading in radians from 0 (straight forward) to 2&pi;, increasing
   * clockwise, or an empty optional if heading should not be controlled.
   * @param turningRate The desired turning rate in radians/sec, or an empty optional if turning
   * rate should not be controlled.
   */
  public TankDriveData(@NotNull DriveData left, @NotNull DriveData right,
      @NotNull OptionalDouble heading,
      @NotNull OptionalDouble turningRate) {
    this.left = Objects.requireNonNull(left);
    this.right = Objects.requireNonNull(right);
    this.heading = Objects.requireNonNull(heading);
    this.turningRate = Objects.requireNonNull(turningRate);
  }

  @Override
  public String toString() {
    return "TankDriveData: " + "left: " + left
        + ", right:" + right
        + (heading.isPresent() ? ", heading " + heading.getAsDouble() : "")
        + (turningRate.isPresent() ? ", turning rate " + turningRate.getAsDouble() : "");
  }

  @Override
  @Contract(value = "null -> false", pure = true)
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof TankDriveData)) {
      return false;
    }
    TankDriveData that = (TankDriveData) o;
    return left.equals(that.left) &&
        right.equals(that.right) &&
        heading.equals(that.heading) &&
        turningRate.equals(that.turningRate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(left, right, heading, turningRate);
  }
}
