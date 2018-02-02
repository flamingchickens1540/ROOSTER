package org.team1540.base;

/**
 * Static utility functions.
 */
public class Utilities {
  /**
   * Processes an axis and returns the value only if it is outside the provided deadzone.
   *
   * @param axis The axis to return
   * @param deadzone The deadzone to use.
   *
   * @return If |{@code axis}| is greater than |{@code deadzone}|, returns {@code axis}; otherwise, returns 0.
   */
  public static double processAxisDeadzone(double axis, double deadzone) {
    return (Math.abs(axis) > Math.abs(deadzone)) ? axis : 0;
  }

  /**
   * Inverts the provided value if {@code shouldInvert} is {@code true}.
   *
   * @param shouldInvert Whether to invert the number.
   * @param toInvert The number to invert.
   * @return If {@code shouldInvert} is {@code true}, {@code -toInvert}; otherwise, {@code toInvert}
   */
  public static double invertIf(boolean shouldInvert, double toInvert) {
    return (shouldInvert ? -1 : 1) * toInvert;
  }
}
