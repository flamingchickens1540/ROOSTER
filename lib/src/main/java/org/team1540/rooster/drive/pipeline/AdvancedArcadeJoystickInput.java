package org.team1540.rooster.drive.pipeline;

import java.util.OptionalDouble;
import java.util.function.DoubleSupplier;
import org.jetbrains.annotations.NotNull;
import org.team1540.rooster.Utilities;
import org.team1540.rooster.functional.Input;

/**
 * Modified arcade drive joystick input.
 *
 * This arcade drive uses one throttle input and two wheel inputs (soft and hard). The soft wheel
 * input is multiplied by the absolute value of the throttle input before being used, while the hard
 * is not&mdash;this allows for smoother control of the robot's path at a variety of speeds. The
 * core algorithm is adapted from Team 2471's drive code, which can be found
 * <a href="https://github.com/TeamMeanMachine/2018FRC/blob/13c96d2f0e2e780b0cec03fe71ad4919f70f6368/src/main/kotlin/org/team2471/frc/powerup/drivetrain/Drivetrain.kt#L163">here</a>.
 * <p>
 * This class is an {@link Input} that provides a {@link TankDriveData}. The resulting {@link
 * TankDriveData} will have the left and right feed-forwards set to throttles between -1 and 1. All
 * other values are empty {@link OptionalDouble OptionalDoubles}.
 *
 * @see Input
 * @see FeedForwardProcessor
 */
public class AdvancedArcadeJoystickInput implements Input<TankDriveData> {

  private boolean reverseBackwards;
  @NotNull
  private DoubleSupplier throttleInput;
  @NotNull
  private DoubleSupplier softTurnInput;
  @NotNull
  private DoubleSupplier hardTurnInput;

  /**
   * Creates a new {@code AdvancedArcadeJoystickInput} that does not reverse while going backwards.
   *
   * @param throttleInput A {@link DoubleSupplier} that supplies the input for the throttle, from -1
   * to 1 inclusive.
   * @param softTurnInput A {@link DoubleSupplier} that supplies the input for the soft-turn, from
   * -1 (full left) to 1 (full right) inclusive.
   * @param hardTurnInput A {@link DoubleSupplier} that supplies the input for the soft-turn, from
   * -1 to 1 inclusive.
   */
  public AdvancedArcadeJoystickInput(@NotNull DoubleSupplier throttleInput,
      @NotNull DoubleSupplier softTurnInput,
      @NotNull DoubleSupplier hardTurnInput) {
    this(false, throttleInput, softTurnInput, hardTurnInput);
  }

  /**
   * Creates a new {@code AdvancedArcadeJoystickInput}.
   *
   * @param reverseBackwards If {@code true}, reverses the direction of the soft turn when the
   * throttle is negative.
   * @param throttleInput A {@link DoubleSupplier} that supplies the input for the throttle, from -1
   * to 1 inclusive.
   * @param softTurnInput A {@link DoubleSupplier} that supplies the input for the soft-turn, from
   * -1 (full left) to 1 (full right) inclusive.
   * @param hardTurnInput A {@link DoubleSupplier} that supplies the input for the soft-turn, from
   * -1 to 1 inclusive.
   */
  public AdvancedArcadeJoystickInput(boolean reverseBackwards,
      @NotNull DoubleSupplier throttleInput,
      @NotNull DoubleSupplier softTurnInput,
      @NotNull DoubleSupplier hardTurnInput) {
    this.reverseBackwards = reverseBackwards;
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

    // scale the soft turn by the throttle, but don't scale the hard turn
    // add turn value to left and subtract from right
    double leftPowerRaw = throttle
        + (soft * Utilities.invertIf(reverseBackwards && throttle < 0, Math.abs(throttle)))
        + hard;
    double rightPowerRaw = throttle
        - (soft * Utilities.invertIf(reverseBackwards && throttle < 0, Math.abs(throttle)))
        - hard;

    // scale the powers, so if the total power for one side is greater than 1 we start reducing the
    // other side to compensate
    double maxPower = Math.max(Math.abs(leftPowerRaw), Math.abs(rightPowerRaw));

    double leftPower, rightPower;
    if (maxPower > 1) {
      leftPower = leftPowerRaw / maxPower;
      rightPower = rightPowerRaw / maxPower;
    } else {
      leftPower = leftPowerRaw;
      rightPower = rightPowerRaw;
    }

    return new TankDriveData().withAdditionalFeedForwards(leftPower, rightPower);
  }
}
