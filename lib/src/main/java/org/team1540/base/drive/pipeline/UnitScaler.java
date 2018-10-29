package org.team1540.base.drive.pipeline;

import java.util.OptionalDouble;

/**
 * Scales units by a desired factor. For details of the scaling, see {@link #apply(TankDriveData)
 * apply()}.
 */
public class UnitScaler implements Processor<TankDriveData, TankDriveData> {

  private double distanceFactor;
  private double timeFactor;

  /**
   * Scales the units in the provided {@link TankDriveData} instance. During scaling, for both sides
   * ({@link TankDriveData#left left} and {@link TankDriveData#right right}), {@link
   * DriveData#position position} is multiplied by the distance factor, and {@link
   * DriveData#velocity velocity} and {@link DriveData#acceleration acceleration} are multiplied by
   * the distance factor then divided by the time factor. If any of these parameters are not
   * present, they will also simply not be present in the output.
   *
   * Feed-forward, heading, and turning rate setpoints are passed through unaffected.
   *
   * @param d The data to scale.
   * @return A new {@link TankDriveData}, scaled as above.
   */
  @Override
  public TankDriveData apply(TankDriveData d) {
    return new TankDriveData(
        new DriveData(
            d.left.position.isPresent() ?
                OptionalDouble.of(d.left.position.getAsDouble() * distanceFactor)
                : d.left.position,
            d.left.velocity.isPresent() ?
                OptionalDouble.of(d.left.velocity.getAsDouble() * distanceFactor / timeFactor)
                : d.left.velocity,
            d.left.acceleration.isPresent() ?
                OptionalDouble.of(d.left.acceleration.getAsDouble() * distanceFactor / timeFactor)
                : d.left.acceleration,
            d.left.additionalFeedForward
        ),
        new DriveData(
            d.right.position.isPresent() ?
                OptionalDouble.of(d.right.position.getAsDouble() * distanceFactor)
                : d.right.position,
            d.right.velocity.isPresent() ?
                OptionalDouble.of(d.right.velocity.getAsDouble() * distanceFactor / timeFactor)
                : d.right.velocity,
            d.right.acceleration.isPresent() ?
                OptionalDouble.of(d.right.acceleration.getAsDouble() * distanceFactor / timeFactor)
                : d.right.acceleration,
            d.right.additionalFeedForward
        ),
        d.heading, d.turningRate);
  }

  /**
   * Creates a new {@code UnitScaler}.
   *
   * @param distanceFactor The scale factor from input distance units (e.g. feet, meters) to output
   * distance units (e.g. ticks).
   * @param timeFactor The scale factor from input time units (e.g. seconds) to output time units.
   */
  public UnitScaler(double distanceFactor, double timeFactor) {
    this.distanceFactor = distanceFactor;
    this.timeFactor = timeFactor;
  }
}
