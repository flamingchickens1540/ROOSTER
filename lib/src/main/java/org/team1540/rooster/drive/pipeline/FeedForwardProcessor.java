package org.team1540.rooster.drive.pipeline;

import java.util.OptionalDouble;
import org.jetbrains.annotations.Contract;

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

  @Override
  public TankDriveData apply(TankDriveData command) {
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
