package org.team1540.rooster.drive.pipeline;

import java.util.Objects;
import java.util.function.DoubleSupplier;
import org.jetbrains.annotations.NotNull;
import org.team1540.rooster.functional.Processor;

/**
 * Modifies velocity set-points to keep a desired turning rate. This {@link Processor} accepts a
 * {@link TankDriveData} and uses the {@linkplain TankDriveData#turningRate turning rate} as an
 * input to the loop.
 *
 * @see Processor
 * @see PIDProcessor
 */
public class TurningRatePIDProcessor extends PIDProcessor<TankDriveData, TankDriveData> {

  private DoubleSupplier yawRateSupplier;
  private boolean invertSides;

  @Override
  protected double getError(TankDriveData data) {
    return data.turningRate.isPresent() ? data.turningRate.getAsDouble() - yawRateSupplier
        .getAsDouble() : 0;
  }

  @Override
  protected TankDriveData createOutput(TankDriveData data, double loopOutput) {
    // multiplying the output by -1 effectively flips the sides
    loopOutput *= invertSides ? -1 : 1;

    return data.plusVelocities(-loopOutput, loopOutput);
  }

  /**
   * Creates a new {@code TurningRateClosedLoopProcessor} with P and I D coefficients of 0 that does
   * not invert sides.
   *
   * @param yawRateSupplier A {@link DoubleSupplier} that supplies the current yaw rate in radians
   * per second.
   * @param p The P coefficient of the PID loop.
   */
  public TurningRatePIDProcessor(DoubleSupplier yawRateSupplier, double p) {
    this(yawRateSupplier, p, 0);
  }

  /**
   * Creates a new {@code TurningRateClosedLoopProcessor} with a D coefficient of 0 that does not
   * invert sides.
   *
   * @param yawRateSupplier A {@link DoubleSupplier} that supplies the current yaw rate in radians
   * per second.
   * @param p The P coefficient of the PID loop.
   * @param i The I coefficient of the PID loop.
   */
  public TurningRatePIDProcessor(DoubleSupplier yawRateSupplier, double p, double i) {
    this(yawRateSupplier, p, i, 0);
  }

  /**
   * Creates a new {@code TurningRateClosedLoopProcessor} that does not invert sides.
   *
   * @param yawRateSupplier A {@link DoubleSupplier} that supplies the current yaw rate in radians
   * per second.
   * @param p The P coefficient of the PID loop.
   * @param i The I coefficient of the PID loop.
   * @param d The D coefficient of the PID loop.
   */
  public TurningRatePIDProcessor(DoubleSupplier yawRateSupplier, double p, double i,
      double d) {
    this(yawRateSupplier, p, i, d, false);
  }

  /**
   * Creates a new {@code TurningRateClosedLoopProcessor} with I and D coefficients of 0.
   *
   * @param yawRateSupplier A {@link DoubleSupplier} that supplies the current yaw rate in radians
   * per second.
   * @param p The P coefficient of the PID loop.
   * @param invertSides Whether to invert the output of the PID loop before sending it onwards.
   */
  public TurningRatePIDProcessor(DoubleSupplier yawRateSupplier, double p,
      boolean invertSides) {
    this(yawRateSupplier, p, 0, invertSides);
  }

  /**
   * Creates a new {@code TurningRateClosedLoopProcessor} with a D coefficient of 0.
   *
   * @param yawRateSupplier A {@link DoubleSupplier} that supplies the current yaw rate in radians
   * per second.
   * @param p The P coefficient of the PID loop.
   * @param i The I coefficient of the PID loop.
   * @param invertSides Whether to invert the output of the PID loop before sending it onwards.
   */
  public TurningRatePIDProcessor(DoubleSupplier yawRateSupplier, double p, double i,
      boolean invertSides) {
    this(yawRateSupplier, p, i, 0, invertSides);
  }

  /**
   * Creates a new {@code TurningRateClosedLoopProcessor}.
   *
   * @param yawRateSupplier A {@link DoubleSupplier} that supplies the current yaw rate in radians
   * per second.
   * @param p The P coefficient of the PID loop.
   * @param i The I coefficient of the PID loop.
   * @param d The D coefficient of the PID loop.
   * @param invertSides Whether to invert the output of the PID loop before sending it onwards.
   */
  public TurningRatePIDProcessor(@NotNull DoubleSupplier yawRateSupplier, double p, double i,
      double d, boolean invertSides) {
    super(p, i, d);
    this.yawRateSupplier = Objects.requireNonNull(yawRateSupplier);
    this.invertSides = invertSides;
  }
}
