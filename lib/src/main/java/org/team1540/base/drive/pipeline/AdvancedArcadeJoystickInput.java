package org.team1540.base.drive.pipeline;

import java.util.OptionalDouble;
import java.util.function.DoubleSupplier;
import org.jetbrains.annotations.NotNull;
import org.team1540.base.drive.DrivePipeline;

/**
 * Modified arcade drive joystick input.
 *
 * This arcade drive uses one throttle input and two wheel inputs (soft and hard). The soft wheel
 * input is multiplied by the absolute value of the throttle input before being used, while the hard
 * is not&ndash;this allows for smoother control of the robot's path at a variety of speeds. The
 * core algorithm is adapted from Team 2471's drive code, which can be found
 * <a href="https://github.com/TeamMeanMachine/2018FRC/blob/13c96d2f0e2e780b0cec03fe71ad4919f70f6368/src/main/kotlin/org/team2471/frc/powerup/drivetrain/Drivetrain.kt#L163">here</a>.
 * <p>
 * This class is an {@link Input} that provides a {@link TankDriveData} for use in a {@link
 * DrivePipeline}. The resulting {@link TankDriveData} will have only the left and right velocities
 * set (all other values are empty {@link OptionalDouble OptionalDoubles}) with units corresponding
 * to the max velocity set on construction.
 *
 * @see Input
 * @see OpenLoopFeedForwardProcessor
 * @see DrivePipeline
 */
public class AdvancedArcadeJoystickInput implements Input<TankDriveData> {

  private double maxVelocity;
  private @NotNull DoubleSupplier throttleInput;
  private @NotNull DoubleSupplier softTurnInput;
  private @NotNull DoubleSupplier hardTurnInput;

  /**
   * Creates a new {@code AdvancedArcadeJoystickInput}.
   *
   * @param maxVelocity The maximum velocity of the robot; joystick values will be scaled to this
   * amount.
   * @param throttleInput A {@link DoubleSupplier} that supplies the input for the throttle, from -1
   * to 1 inclusive.
   * @param softTurnInput A {@link DoubleSupplier} that supplies the input for the soft-turn, from
   * -1 (full left) to 1 (full right) inclusive.
   * @param hardTurnInput A {@link DoubleSupplier} that supplies the input for the soft-turn, from
   * -1 (full left) to 1 (full right) inclusive.
   */
  public AdvancedArcadeJoystickInput(double maxVelocity,
      @NotNull DoubleSupplier throttleInput,
      @NotNull DoubleSupplier softTurnInput,
      @NotNull DoubleSupplier hardTurnInput) {
    this.maxVelocity = maxVelocity;
    this.throttleInput = throttleInput;
    this.softTurnInput = softTurnInput;
    this.hardTurnInput = hardTurnInput;
  }

  /**
   * Gets the desired output.
   *
   * @return A {@link TankDriveData} with only the left and right velocity fields specified.
   */
  @Override
  public TankDriveData get() {
    double throttle = throttleInput.getAsDouble();
    double soft = softTurnInput.getAsDouble();
    double hard = hardTurnInput.getAsDouble();

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

    // TODO: Heading control

    return new TankDriveData(
        new DriveData(OptionalDouble.of(leftPower * maxVelocity)),
        new DriveData(OptionalDouble.of(rightPower * maxVelocity)),
        OptionalDouble.empty(),
        OptionalDouble.empty());
  }
}
