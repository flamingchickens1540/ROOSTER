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
 * DrivePipeline}. The resulting {@link TankDriveData} will have the left and right velocities set
 * with units corresponding to the max velocity set on construction, as well as the turning rate in
 * radians per second. All other values are empty {@link OptionalDouble OptionalDoubles}.
 *
 * @see Input
 * @see OpenLoopFeedForwardProcessor
 * @see DrivePipeline
 */
public class AdvancedArcadeJoystickInput implements Input<TankDriveData> {

  private double maxVelocity;
  private double trackWidth;
  private @NotNull DoubleSupplier throttleInput;
  private @NotNull DoubleSupplier softTurnInput;
  private @NotNull DoubleSupplier hardTurnInput;

  /**
   * Creates a new {@code AdvancedArcadeJoystickInput}.
   *
   * @param maxVelocity The maximum velocity of the robot; joystick values will be scaled to this
   * amount. This should be in position units per second to keep with the specification of {@link
   * TankDriveData}.
   * @param trackWidth The track width of the robot (distance between the wheels); this should be in
   * the same position units as maxVelocity.
   * @param throttleInput A {@link DoubleSupplier} that supplies the input for the throttle, from -1
   * to 1 inclusive.
   * @param softTurnInput A {@link DoubleSupplier} that supplies the input for the soft-turn, from
   * -1 (full left) to 1 (full right) inclusive.
   * @param hardTurnInput A {@link DoubleSupplier} that supplies the input for the soft-turn, from
   * -1 (full left) to 1 (full right) inclusive.
   */
  public AdvancedArcadeJoystickInput(double maxVelocity, double trackWidth,
      @NotNull DoubleSupplier throttleInput,
      @NotNull DoubleSupplier softTurnInput,
      @NotNull DoubleSupplier hardTurnInput) {
    this.maxVelocity = maxVelocity;
    this.trackWidth = trackWidth;
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

    // omega (dtheta / dt or yaw rate) is just the difference in velocities (powers) divided by the track width

    double leftVelocity = leftPower * maxVelocity;
    double rightVelocity = rightPower * maxVelocity;

    double omega = (rightVelocity - leftVelocity) / trackWidth;

    return new TankDriveData(
        new DriveData(OptionalDouble.of(leftVelocity)),
        new DriveData(OptionalDouble.of(rightVelocity)),
        OptionalDouble.empty(),
        OptionalDouble.of(omega));
  }
}