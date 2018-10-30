package org.team1540.base.drive.pipeline;

import java.util.OptionalDouble;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public class AdvancedArcadeJoystickInput implements Input<TankDriveData> {

  private double maxVelocity;
  private @NotNull Supplier<Double> throttleInput;
  private @NotNull Supplier<Double> softTurnInput;
  private @NotNull Supplier<Double> hardTurnInput;

  public AdvancedArcadeJoystickInput(double maxVelocity,
      @NotNull Supplier<Double> throttleInput,
      @NotNull Supplier<Double> softTurnInput,
      @NotNull Supplier<Double> hardTurnInput) {
    this.maxVelocity = maxVelocity;
    this.throttleInput = throttleInput;
    this.softTurnInput = softTurnInput;
    this.hardTurnInput = hardTurnInput;
  }

  @Override
  public TankDriveData get() {
    double throttle = throttleInput.get();
    double soft = softTurnInput.get();
    double hard = hardTurnInput.get();

    double leftPowerRaw = throttle + (soft * Math.abs(throttle)) + hard;
    double rightPowerRaw = throttle - (soft * Math.abs(throttle)) - hard;

    double maxPower = Math.max(Math.abs(leftPowerRaw), Math.abs(rightPowerRaw));

    double leftPower, rightPower;
    if (maxPower > 1) {
      leftPower = leftPowerRaw / maxPower;
      rightPower = rightPowerRaw / maxPower;
    } else {
      leftPower = leftPowerRaw;
      rightPower = rightPowerRaw;
    }

    return new TankDriveData(
        new DriveData(OptionalDouble.of(leftPower * maxVelocity)),
        new DriveData(OptionalDouble.of(rightPower * maxVelocity)),
        OptionalDouble.empty(),
        OptionalDouble.empty());
  }
}
