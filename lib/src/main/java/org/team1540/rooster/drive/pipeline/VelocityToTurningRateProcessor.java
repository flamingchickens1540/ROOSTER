package org.team1540.rooster.drive.pipeline;

import org.team1540.rooster.functional.Processor;

/**
 * {@link Processor} to calculate a turning rate from left and right velocities. The {@link
 * TankDriveData} returned from this method will have all fields identical to the one passed, except
 * for the {@link TankDriveData#turningRate turningRate} field.
 */
public class VelocityToTurningRateProcessor implements Processor<TankDriveData, TankDriveData> {

  private double trackWidth;

  /**
   * Creates a new {@code VelocityToTurningRateProcessor}.
   *
   * @param trackWidth The track width of the robot (distance between left and right wheels).
   */
  public VelocityToTurningRateProcessor(double trackWidth) {
    this.trackWidth = trackWidth;
  }

  @Override
  public TankDriveData apply(TankDriveData tankDriveData) {
    if (tankDriveData.left.velocity.isPresent() && tankDriveData.right.velocity.isPresent()) {
      return tankDriveData.withTurningRate(
          (tankDriveData.right.velocity.getAsDouble() - tankDriveData.left.velocity.getAsDouble())
              / trackWidth);
    } else {
      return tankDriveData;
    }
  }
}
