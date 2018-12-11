package org.team1540.rooster.drive.pipeline;

import org.team1540.rooster.functional.Processor;

/**
 * {@link Processor} to convert headings on [0,2&pi;] to [-&pi;, &pi;] and vice versa. (This class
 * also supports degrees by passing {@code true} as the second argument to {@link
 * #HeadingTransformProcessor(boolean, boolean)}.
 *
 * This class is a {@link Processor} that uses {@link TankDriveData}. All fields are passed through
 * as-is except for the heading field which is converted if it is present.
 */
public class HeadingTransformProcessor implements Processor<TankDriveData, TankDriveData> {

  private boolean outputPositive;
  private boolean radians;

  /**
   * Create a new {@code HeadingTransformProcessor} that uses radians.
   *
   * @param outputPositive Whether to output values in the range [0,2&pi;] (i.e. [0,360]). If {@code
   * true}, creates a {@code HeadingTransformProcessor} to convert angles on [-&pi;, &pi;] to angles
   * on [0,2&pi;]; if {@code false}, creates one that does the reverse.
   */
  public HeadingTransformProcessor(boolean outputPositive) {
    this(outputPositive, true);
  }

  /**
   * Create a new {@code HeadingTransformProcessor}.
   *
   * @param outputPositive Whether to output values in the range [0,2&pi;] (or [0,360]). If {@code
   * true}, creates a {@code HeadingTransformProcessor} to convert angles on [-&pi;, &pi;] to angles
   * on [0,2&pi;]; if {@code false}, creates one that does the reverse.
   * @param radians Whether to use radians. If {@code true}, assumes inputs are in radians;
   * otherwise, uses degrees.
   */
  public HeadingTransformProcessor(boolean outputPositive, boolean radians) {
    this.outputPositive = outputPositive;
    this.radians = radians;
  }

  @Override
  public TankDriveData apply(TankDriveData data) {
    if (data.heading.isPresent()) {
      double heading = data.heading.getAsDouble();
      double halfOfCircle = radians ? Math.PI : 180;
      double processed = heading + ((outputPositive ? 1 : -1) * halfOfCircle);
      return data.withHeading(processed);
    } else {
      return data;
    }
  }
}
