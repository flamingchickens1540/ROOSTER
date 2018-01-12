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
   * @return If |{@code axis}| is greater than |{@code deadzone}|, returns {@code axis}; otherwise,
   * returns 0.
   */
  public static double processAxisDeadzone(double axis, double deadzone) {
    return (Math.abs(axis) > Math.abs(deadzone)) ? axis : 0;
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
