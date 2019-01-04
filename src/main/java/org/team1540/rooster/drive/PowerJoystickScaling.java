package org.team1540.rooster.drive;

/**
 * Joystick scaling based on raising the joystick input to a set power.
 */
public class PowerJoystickScaling implements JoystickScaling {

  private double pow;

  /**
   * Creates a new {@code PowerJoystickScaling} instance with the provided power.
   *
   * @param pow The power to raise the joystick input to during scaling.
   */
  public PowerJoystickScaling(double pow) {
    this.pow = pow;
  }

  /**
   * Scales the provided input using the equation {@code |input|}<sup>{@code pow}</sup>, where
   * {@code pow} is the power set via constructor or using {@link #setPow(double) setPow()}.
   *
   * @param input The input to scale.
   * @return The absolute value of the input, raised to a specified power, with the sign (positive
   * or negative) of the original input.
   */
  @Override
  public double scale(double input) {
    return Math.copySign(Math.pow(Math.abs(input), pow), input);
  }

  public double getPow() {
    return pow;
  }

  public void setPow(double pow) {
    this.pow = pow;
  }
}
