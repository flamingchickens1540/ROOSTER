package org.team1540.base.drive;

import static java.lang.Math.pow;

public class CubicJoystickScaling implements JoystickScaling {

  private double a;
  private double b;
  private double c;
  private double d;

  public CubicJoystickScaling(double a) {
    this(a, 0, 0, 0);
  }

  public CubicJoystickScaling(double a, double b, double c, double d) {
    this.a = a;
    this.b = b;
    this.c = c;
    this.d = d;
  }

  @Override
  public double scale(double input) {
    return (a * pow(input, 3)) + (b * pow(input, 2)) + (c * input) + d;
  }
}
