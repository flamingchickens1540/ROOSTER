package org.team1540.rooster.drive.pipeline;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import org.team1540.rooster.wrappers.ChickenTalon;

public class TalonSRXOutput implements Output<TankDriveData> {

  private ChickenTalon left;
  private ChickenTalon right;
  private boolean useClosedLoop;

  @SuppressWarnings("OptionalGetWithoutIsPresent")
  @Override
  public void accept(TankDriveData tankDriveData) {
    if (tankDriveData.left.position.isPresent() && useClosedLoop) {
      if (tankDriveData.left.additionalFeedForward.isPresent()) {
        left.set(ControlMode.Position, tankDriveData.left.position.getAsDouble(),
            DemandType.ArbitraryFeedForward,
            tankDriveData.left.additionalFeedForward.getAsDouble());
        right.set(ControlMode.Position, tankDriveData.right.position.getAsDouble(),
            DemandType.ArbitraryFeedForward,
            tankDriveData.right.additionalFeedForward.getAsDouble());
      } else {
        left.set(ControlMode.Position, tankDriveData.left.position.getAsDouble());
        right.set(ControlMode.Position, tankDriveData.right.position.getAsDouble());
      }
    } else if (tankDriveData.left.velocity.isPresent() && useClosedLoop) {
      if (tankDriveData.left.additionalFeedForward.isPresent()) {
        left.set(ControlMode.Velocity, tankDriveData.left.velocity.getAsDouble(),
            DemandType.ArbitraryFeedForward,
            tankDriveData.left.additionalFeedForward.getAsDouble());
        right.set(ControlMode.Velocity, tankDriveData.right.velocity.getAsDouble(),
            DemandType.ArbitraryFeedForward,
            tankDriveData.right.additionalFeedForward.getAsDouble());
      } else {
        left.set(ControlMode.Velocity, tankDriveData.left.velocity.getAsDouble());
        right.set(ControlMode.Velocity, tankDriveData.right.velocity.getAsDouble());
      }
    } else {
      left.set(ControlMode.PercentOutput, tankDriveData.left.additionalFeedForward.orElse(0));
      right.set(ControlMode.PercentOutput, tankDriveData.right.additionalFeedForward.orElse(0));
    }
  }

  public TalonSRXOutput(ChickenTalon left, ChickenTalon right) {
    this(left, right, true);
  }

  public TalonSRXOutput(ChickenTalon left, ChickenTalon right, boolean useClosedLoop) {
    this.left = left;
    this.right = right;
    this.useClosedLoop = useClosedLoop;
  }
}
