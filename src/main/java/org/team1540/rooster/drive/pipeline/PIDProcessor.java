package org.team1540.rooster.drive.pipeline;

import org.team1540.rooster.functional.Processor;

/**
 * Processor to execute a generic PID loop.
 *
 * @param <I> The input type of the processor.
 * @param <O> The output type of the processor.
 */
public abstract class PIDProcessor<I, O> implements Processor<I, O> {

  private double p, i, d;

  private double iAccum;

  private double lastError;

  private long lastTime = -1;

  /**
   * Create a new {@code PIDProcessor}.
   *
   * @param p The P coefficient.
   * @param i The I coefficient.
   * @param d The D coefficient.
   */
  protected PIDProcessor(double p, double i, double d) {
    this.p = p;
    this.i = i;
    this.d = d;
  }

  /**
   * Extract the PID loop target from the data passed to the processor, and calculate the current
   * error.
   *
   * @param data The data instance that was passed to the processor's {@link #apply(Object) apply()}
   * method.
   * @return The error.
   */
  protected abstract double getError(I data);

  /**
   * Create the processor output.
   *
   * @param data The data instance that was passed to the processor's {@link #apply(Object) apply()}
   * method.
   * @param loopOutput The output of the PID loop
   * @return The processor's output; this will be returned from the the processor's {@link
   * #apply(Object) apply()} method.
   */
  protected abstract O createOutput(I data, double loopOutput);

  @Override
  public O apply(I input) {
    double output = 0;
    double error = getError(input);

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

    return createOutput(input, output);
  }


  /**
   * Gets the current value of the integral accumulator.
   *
   * @return The integral accumulator. If the PID loop has not yet been run (i.e. {@link
   * #apply(Object) apply()} has not yet been called since instantiation/the last call to {@link
   * #reset()}), returns 0.
   */
  public double getIAccum() {
    return iAccum;
  }

  /**
   * Gets the closed-loop error from the last run of the PID loop.
   *
   * @return The closed-loop error. If the PID loop has not yet been run (i.e. {@link #apply(Object)
   * apply()} has not yet been called since instantiation/the last call to {@link #reset()}),
   * returns 0.
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
}
