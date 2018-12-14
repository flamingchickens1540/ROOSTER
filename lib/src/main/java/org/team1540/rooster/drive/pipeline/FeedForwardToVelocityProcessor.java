package org.team1540.rooster.drive.pipeline;

import java.util.OptionalDouble;
import java.util.function.DoubleSupplier;
import org.team1540.rooster.functional.Processor;

/**
 * {@link Processor} that converts a feed-forward/throttle into a velocity setpoint. This processor
 * multiplies each feed-forward by the maximum velocity of the robot. This is useful when running
 * closed-loop teleop drive code (as joystick inputs usually provide feed-forwards).
 */
public class FeedForwardToVelocityProcessor implements Processor<TankDriveData, TankDriveData> {

  private DoubleSupplier maxVelocitySupplier;
  private boolean clearFeedForwards;

  /**
   * Creates a new {@code FeedForwardToVelocityProcessor} that clears feed-forwards in the returned
   * data.
   *
   * @param maxVelocity The maximum velocity of the robot.
   */
  public FeedForwardToVelocityProcessor(double maxVelocity) {
    this(maxVelocity, true);
  }

  /**
   * Creates a new {@code FeedForwardToVelocityProcessor}.
   *
   * @param maxVelocity The maximum velocity of the robot.
   * @param clearFeedForwards Whether to set the feed-forwards in the {@link TankDriveData} to empty
   * {@link OptionalDouble OptionalDoubles}. If {@code true}, the value will be cleared; if {@code
   * false}, it will be passed through as-is.
   */
  public FeedForwardToVelocityProcessor(double maxVelocity, boolean clearFeedForwards) {
    this(() -> maxVelocity, clearFeedForwards);
  }

  /**
   * Creates a new {@code FeedForwardToVelocityProcessor}.
   *
   * @param maxVelSupplier A supplier that supplies the maximum velocity of the robot.
   * @param clearFeedForwards Whether to set the feed-forwards in the {@link TankDriveData} to empty
   * {@link OptionalDouble OptionalDoubles}. If {@code true}, the value will be cleared; if {@code
   * false}, it will be passed through as-is.
   */
  public FeedForwardToVelocityProcessor(DoubleSupplier maxVelSupplier, boolean clearFeedForwards) {
    this.maxVelocitySupplier = maxVelSupplier;
    this.clearFeedForwards = clearFeedForwards;
  }

  @Override
  public TankDriveData apply(TankDriveData data) {
    double maxVelocity = maxVelocitySupplier.getAsDouble();
    return new TankDriveData(
        new DriveData(data.left.position,
            OptionalDouble.of(data.left.additionalFeedForward.orElse(0) * maxVelocity),
            data.left.acceleration,
            clearFeedForwards ? OptionalDouble.empty() : data.left.additionalFeedForward),
        new DriveData(data.right.position,
            OptionalDouble.of(data.right.additionalFeedForward.orElse(0) * maxVelocity),
            data.right.acceleration,
            clearFeedForwards ? OptionalDouble.empty() : data.right.additionalFeedForward),
        data.heading, data.turningRate);
  }
}
