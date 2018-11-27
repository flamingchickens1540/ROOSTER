package org.team1540.rooster.drive.pipeline;


/*
Implementation note:
This is not a command, as I realized that with PIDDrive so much of the configuration was simply
dealing with scaling the joysticks and creating a full pipeline from joystick to motor controller.
This just takes speed values in a method, and outputs a drive signal. This also means it can
be plugged into a motion profile executor.
*/

import java.util.OptionalDouble;
import org.jetbrains.annotations.Contract;

public class FeedForwardProcessor implements Processor<TankDriveData, TankDriveData> {

  private final double kV;
  private final double vIntercept;
  private final double kA;

  // TODO: Add explanation of units in class docs

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
  private OpenLoopDriveSignal getDriveSignal(double leftSpeed, double leftAccel, double rightSpeed,
      double rightAccel) {
    double left = getThrottle(leftSpeed, leftAccel);
    double right = getThrottle(rightSpeed, rightAccel);
    return new OpenLoopDriveSignal(left, right);
  }

  @Contract(pure = true)
  private double getThrottle(double wantedSpeed, double wantedAccel) {
    return (kV * wantedSpeed)
        + (kA * wantedAccel)
        + (wantedSpeed != 0 ? Math.copySign(vIntercept, wantedSpeed) : 0);
  }

  @Override
  public TankDriveData apply(TankDriveData command) {
    OpenLoopDriveSignal signal = getDriveSignal(
        command.left.velocity.orElse(0),
        command.left.acceleration.orElse(0),
        command.right.velocity.orElse(0),
        command.right.acceleration.orElse(0));

    return new TankDriveData(
        new DriveData(command.left.position,
            command.left.velocity,
            command.left.acceleration,
            OptionalDouble
                .of(command.left.additionalFeedForward.orElse(0) + signal.getLeftThrottle())),
        new DriveData(command.right.position,
            command.right.velocity,
            command.right.acceleration,
            OptionalDouble
                .of(command.right.additionalFeedForward.orElse(0) + signal.getRightThrottle())),
        command.heading,
        command.turningRate);
  }

  /**
   * Data structure for a drive's left and right throttles.
   */
  private static class OpenLoopDriveSignal {

    private final double leftThrottle;
    private final double rightThrottle;

    public OpenLoopDriveSignal(double leftThrottle, double rightThrottle) {
      this.leftThrottle = leftThrottle;
      this.rightThrottle = rightThrottle;
    }

    public double getLeftThrottle() {
      return leftThrottle;
    }

    public double getRightThrottle() {
      return rightThrottle;
    }
  }
}
