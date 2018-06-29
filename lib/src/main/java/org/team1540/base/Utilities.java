package org.team1540.base;

import org.jetbrains.annotations.Contract;

/**
 * Static utility functions.
 */
public class Utilities {

  /**
   * Processes an axis and returns the value only if it is outside the provided deadzone.
   *
   * @param axis The axis to return.
   * @param deadzone The deadzone to use.
   * @return If |{@code axis}| is greater than |{@code deadzone}|, returns {@code axis}; otherwise,
   * returns 0.
   * @deprecated Use {@link #processDeadzone(double, double) processDeadzone()}'s improved
   * algorithm.
   */
  public static double processAxisDeadzone(double axis, double deadzone) {
    return (Math.abs(axis) > Math.abs(deadzone)) ? axis : 0;
  }

  /**
   * Processes an axis with a deadzone. This implementation scales the output such that the area
   * between the deadzone and 1 is mapped to the full range of motion.
   * <p>
   * Full implementation details: The function processes the deadzone according to the function
   * \(\DeclareMathOperator{\sgn}{sgn}\frac{d(x)-Z\sgn(d(x))}{1-Z}\), where \(x\) is the {@code
   * axis} parameter, \(Z\) is the {@code deadzone} parameter, and \(d(x)\) is the deadzone function
   * \(d(x)=\begin{cases}x &amp; |x| \geq D  \\  0 &amp; |x| &lt; D\end{cases}\). (Note: Equations
   * may require viewing the Javadoc using a browser.)
   *
   * @param axis The axis to return.
   * @param deadzone The deadzone to use.
   * @return The axis value processed with respect to the specified deadzone.
   */
  @Contract(pure = true)
  public static double processDeadzone(double axis, double deadzone) {
    double baseDeadzone = (Math.abs(axis) > Math.abs(deadzone)) ? axis : 0;
    return baseDeadzone != 0 ? (baseDeadzone - Math.copySign(deadzone, baseDeadzone)) / (1
        - deadzone) : 0;
  }

  /**
   * Inverts the provided value if {@code shouldInvert} is {@code true}.
   *
   * @param shouldInvert Whether to invert the number.
   * @param toInvert The number to invert.
   * @return If {@code shouldInvert} is {@code true}, {@code -toInvert}; otherwise, {@code toInvert}
   */
  @Contract(pure = true)
  public static double invertIf(boolean shouldInvert, double toInvert) {
    return (shouldInvert ? -1 : 1) * toInvert;
  }

  /**
   * Constrains an input to a given range in either direction from zero.
   *
   * This does <strong>not</strong> map the input to the range; it simply hard-caps it when it's
   * outside.
   *
   * @param input The input to constrain.
   * @param cap The distance from zero to constrain {@code input} to.
   * @return If {@code input} &gt; {@code cap}, return {@code cap}; if {@code input} &lt; {@code
   * -cap}, return {@code -cap}; otherwise, return {@code input}.
   */
  @Contract(pure = true)
  public static double constrain(double input, double cap) {
    if (cap < 0) {
      throw new IllegalArgumentException("Cap cannot be negative");
    }

    return constrain(input, -cap, cap);
  }

  /**
   * Constrains  an input to a given range.
   *
   * This does <strong>not</strong> map the input to the range; it simply hard-caps it when it's
   * outside.
   *
   * @param input The input to constrain.
   * @param lowerCap The lower bound of the range.
   * @param upperCap The upper bound of the range.
   * @return If {@code input} &gt; {@code upperCap}, return {@code upperCap}; if {@code input} &lt;
   * {@code lowerCap}, return {@code lowerCap}; otherwise, return {@code input}.
   */
  @Contract(pure = true)
  public static double constrain(double input, double lowerCap, double upperCap) {
    if (lowerCap > upperCap) {
      throw new IllegalArgumentException("Lower cap cannot be less than upper cap");
    }

    if (input < lowerCap) {
      return lowerCap;
    } else if (input > upperCap) {
      return upperCap;
    } else {
      return input;
    }
  }
}
