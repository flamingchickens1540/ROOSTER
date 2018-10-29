package org.team1540.base.drive.pipeline;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import java.util.function.Consumer;
import org.team1540.base.wrappers.ChickenTalon;

public class TalonSRXOutput implements Consumer<TankDriveData> {

  private ChickenTalon left;
  private ChickenTalon right;

  @SuppressWarnings("OptionalGetWithoutIsPresent")
  @Override
  public void accept(TankDriveData tankDriveData) {
    if (tankDriveData.left.position.isPresent()) {
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
    } else if (tankDriveData.left.velocity.isPresent()) {
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
    this.left = left;
    this.right = right;
  }
}
