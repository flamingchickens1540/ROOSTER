package org.team1540.rooster.drive.pipeline;

import java.util.OptionalDouble;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * {@link Processor} to apply an <a href="https://www.chiefdelphi.com/media/papers/3402">Oblarg-style</a>
 * feed-forward. This processor allows for velocity, acceleration, and static friction (vIntercept)
 * feed-forwards. For further details, see {@link #apply(TankDriveData) apply()}. The output will
 * mirror the input with the exception of a change in the feed-forward value.
 */
public class FeedForwardProcessor implements Processor<TankDriveData, TankDriveData> {

  private final double kV;
  private final double vIntercept;
  private final double kA;

  /**
   * Creates a {@code FeedForwardProcessor} with the provided \(k_v\) and \(v_{Intercept}\)
   *
   * @param kV The velocity constant feed-forward, in output units per speed unit.
   * @param vIntercept The velocity intercept, in output units
   * @param kA The acceleration constant feed-forward, in output units per acceleration unit.
   */
  public FeedForwardProcessor(double kV, double vIntercept, double kA) {
    this.kV = kV;
    this.vIntercept = vIntercept;
    this.kA = kA;
  }

  @Contract(pure = true)
  private double getThrottle(double wantedSpeed, double wantedAccel) {
    return (kV * wantedSpeed)
        + (kA * wantedAccel)
        + (wantedSpeed != 0 ? Math.copySign(vIntercept, wantedSpeed) : 0);
  }

  /**
   * Applys feed-forwards to the provided {@link TankDriveData}. The method for calculating the
   * feed-forward is as follows:
   * <ol>
   * <li>The feed-forward starts at 0. </li>
   * <li>The product of the velocity (if present) and the kV is added.</li>
   * <li>The product of the acceleration (if present) and the kA is added.</li>
   * <li>If the velocity is present and nonzero, the velocity intercept (with the sign of the
   * velocity) is added.</li>
   * </ol>
   * The calculated feed-forward is then added to any feed-forward already present in the
   * DriveData.
   *
   * @param command The data to use.
   * @return A new {@link TankDriveData} as described above.
   */
  @Override
  @NotNull
  public TankDriveData apply(@NotNull TankDriveData command) {
    return new TankDriveData(
        new DriveData(command.left.position,
            command.left.velocity,
            command.left.acceleration,
            OptionalDouble.of(command.left.additionalFeedForward.orElse(0)
                + getThrottle(command.left.velocity.orElse(0),
                command.left.acceleration.orElse(0)))),
        new DriveData(command.right.position,
            command.right.velocity,
            command.right.acceleration,
            OptionalDouble.of(command.right.additionalFeedForward.orElse(0)
                + getThrottle(command.right.velocity.orElse(0),
                command.right.acceleration.orElse(0)))),
        command.heading,
        command.turningRate);
  }
}
