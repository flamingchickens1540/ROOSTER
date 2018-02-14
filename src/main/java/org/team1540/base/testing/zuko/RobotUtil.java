package org.team1540.base.testing.zuko;

public class RobotUtil {

  public static double deadzone(double input, double range) {
    if (Math.abs(input) < range) {
      return 0.0;
    }
    return input;
  }

  public static double betterDeadzone(double input, double range, double exponent) {
    if (Math.abs(input) > range) {
      if (input > 0) {
        return Math.pow((input - range) / (1 - range), exponent);
      } else {
        return -Math.pow((-input - range) / (1 - range), exponent);
      }
    } else {
      return 0;
    }
  }

  public static double limit(double input, double max, double min) {
    if (input > max) {
      return max;
    } else if (input < min) {
      return min;
    }
    return input;
  }

}
