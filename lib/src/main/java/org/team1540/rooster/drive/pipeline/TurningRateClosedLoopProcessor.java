package org.team1540.rooster.drive.pipeline;

import java.util.OptionalDouble;
import java.util.function.DoubleSupplier;

/**
 * Modifies velocity setpoints to keep a desired turning rate.
 */
public class TurningRateClosedLoopProcessor implements Processor<TankDriveData, TankDriveData> {

  private DoubleSupplier yawRateSupplier;
  private double p, i, d;

  private double iAccum;

  private double lastError;

  private long lastTime = -1;

  private boolean invertSides;

  @Override
  public TankDriveData apply(TankDriveData driveData) {
    if (driveData.turningRate.isPresent()) {
      // simple PID controller

      double actual = this.yawRateSupplier.getAsDouble();
      double target = driveData.turningRate.getAsDouble();

      double output = 0;
      double error = target - actual;

      // p gain
      output += error * p;

      if (lastTime != -1) {
        double dt = (System.currentTimeMillis() - lastTime) / 1000.0;

        // i gain
        iAccum += error * dt;
        output += i * iAccum;

        // d gain
        double dError = (error - lastError) / dt;
        output += d * dError;
      }

      lastError = error;
      lastTime = System.currentTimeMillis();

      // multiplying the output by -1 effectively flips the sides
      output *= invertSides ? -1 : 1;

      return new TankDriveData(
          new DriveData(
              driveData.left.position,
              OptionalDouble.of(driveData.left.velocity.orElse(0) - output),
              driveData.left.acceleration,
              driveData.left.additionalFeedForward
          ),
          new DriveData(
              driveData.right.position,
              OptionalDouble.of(driveData.right.velocity.orElse(0) + output),
              driveData.right.acceleration,
              driveData.right.additionalFeedForward
          ),
          driveData.heading,
          driveData.turningRate
      );
    } else {
      // we can't do anything here without a setpoint so just pass the boi through
      return driveData;
    }
  }

  /**
   * Gets the current value of the integral accumulator.
   *
   * @return The integral accumulator, in radians. If the PID loop has not yet been run (i.e. {@link
   * #apply(TankDriveData)} apply()} has not yet been called since instantiation/the last call to
   * {@link #reset()}), returns 0.
   */
  public double getIAccum() {
    return iAccum;
  }

  /**
   * Gets the closed-loop error from the last run of the PID loop.
   *
   * @return The closed-loop error, in radians per second. If the PID loop has not yet been run
   * (i.e. {@link #apply(TankDriveData)} apply()} has not yet been called since instantiation/the
   * last call to {@link #reset()}), returns 0.
   */
  public double getError() {
    return lastError;
  }

  /**
   * Resets the PID loop. This clears the integral accumulator and error values, and should be
   * called if the {@code TurningRateClosedLoopProcessor} is being reused for multiple discrete
   * occasions (i.e. executions of different motion profile segments). Calling this is functionally
   * equivalent to creating a new processor instance.
   */
  public void reset() {
    iAccum = 0;
    lastError = 0;
    lastTime = -1;
  }

  public TurningRateClosedLoopProcessor(DoubleSupplier yawRateSupplier, double p) {
    this(yawRateSupplier, p, 0);
  }

  public TurningRateClosedLoopProcessor(DoubleSupplier yawRateSupplier, double p, double i) {
    this(yawRateSupplier, p, i, 0);
  }

  public TurningRateClosedLoopProcessor(DoubleSupplier yawRateSupplier, double p, double i,
      double d) {
    this(yawRateSupplier, p, i, d, false);
  }

  public TurningRateClosedLoopProcessor(DoubleSupplier yawRateSupplier, double p,
      boolean invertSides) {
    this(yawRateSupplier, p, 0, invertSides);
  }

  public TurningRateClosedLoopProcessor(DoubleSupplier yawRateSupplier, double p, double i,
      boolean invertSides) {
    this(yawRateSupplier, p, i, 0, invertSides);
  }

  public TurningRateClosedLoopProcessor(DoubleSupplier yawRateSupplier, double p, double i,
      double d, boolean invertSides) {
    this.yawRateSupplier = yawRateSupplier;
    this.p = p;
    this.i = i;
    this.d = d;
    this.invertSides = invertSides;
  }
}
