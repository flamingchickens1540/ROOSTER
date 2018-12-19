package org.team1540.rooster.drive.pipeline;

import java.util.function.DoubleSupplier;

/**
 * A {@link PIDProcessor} for maintaining a robot's heading.
 */
public class HeadingPIDProcessor extends PIDProcessor<TankDriveData, TankDriveData> {

  private DoubleSupplier headingSupplier;
  private boolean outputToPosition;
  private boolean invertSides;

  /**
   * Create a new {@code HeadingPIDProcessor} without inverted sides that outputs to feed-forward
   * setpoints.
   *
   * @param p The P coefficient.
   * @param headingSupplier A supplier that returns the current heading, in radians from -&pi; to
   * &pi;.
   */
  public HeadingPIDProcessor(double p, DoubleSupplier headingSupplier) {
    this(p, 0, headingSupplier);
  }

  /**
   * Create a new {@code HeadingPIDProcessor} without inverted sides that outputs to feed-forward
   * setpoints.
   *
   * @param p The P coefficient.
   * @param i The I coefficient.
   * @param headingSupplier A supplier that returns the current heading, in radians from -&pi; to
   * &pi;.
   */
  public HeadingPIDProcessor(double p, double i, DoubleSupplier headingSupplier) {
    this(p, i, 0, headingSupplier);
  }

  /**
   * Create a new {@code HeadingPIDProcessor} without inverted sides that outputs to feed-forward
   * setpoints.
   *
   * @param p The P coefficient.
   * @param i The I coefficient.
   * @param d The D coefficient.
   * @param headingSupplier A supplier that returns the current heading, in radians from -&pi; to
   * &pi;.
   */
  public HeadingPIDProcessor(double p, double i, double d, DoubleSupplier headingSupplier) {
    this(p, i, d, headingSupplier, false);
  }

  /**
   * Create a new {@code HeadingPIDProcessor} without inverted sides.
   *
   * @param p The P coefficient.
   * @param i The I coefficient.
   * @param d The D coefficient.
   * @param headingSupplier A supplier that returns the current heading, in radians from -&pi; to
   * &pi;.
   * @param outputToPosition Whether to output to the position setpoints (as opposed to the
   * feed-forward setpoints.)
   */
  public HeadingPIDProcessor(double p, double i, double d, DoubleSupplier headingSupplier,
      boolean outputToPosition) {
    this(p, i, d, headingSupplier, outputToPosition, false);
  }

  /**
   * Create a new {@code HeadingPIDProcessor}.
   *
   * @param p The P coefficient.
   * @param i The I coefficient.
   * @param d The D coefficient.
   * @param headingSupplier A supplier that returns the current heading, in radians from -&pi; to
   * &pi;.
   * @param outputToPosition Whether to output to the position setpoints (as opposed to the
   * feed-forward setpoints.)
   * @param invertSides Whether to invert the output sides. (If {@code true}, the loop output will
   * be added to the left side and subtracted from the right, and vice versa.)
   */
  public HeadingPIDProcessor(double p, double i, double d, DoubleSupplier headingSupplier,
      boolean outputToPosition, boolean invertSides) {
    super(p, i, d);
    this.headingSupplier = headingSupplier;
    this.outputToPosition = outputToPosition;
    this.invertSides = invertSides;
  }

  @Override
  protected double getError(TankDriveData data) {
    if (data.heading.isPresent()) {
      double heading = headingSupplier.getAsDouble();
      double headingTarget = data.heading.getAsDouble();

      // basically magic https://stackoverflow.com/a/2007279
      return Math.atan2(Math.sin(heading - headingTarget), Math.cos(heading - headingTarget));
    } else {
      return 0;
    }
  }

  @Override
  protected TankDriveData createOutput(TankDriveData data, double loopOutput) {
    // multiplying the output by -1 effectively flips the sides
    loopOutput *= invertSides ? -1 : 1;

    if (outputToPosition) {
      return data.plusPositions(-loopOutput, loopOutput);
    } else {
      return data.plusAdditionalFeedForwards(-loopOutput, loopOutput);
    }
  }
}
