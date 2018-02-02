package org.team1540.base.drive;

import static java.lang.Math.pow;

public class QuadJoystickScaling implements JoystickScaling {

  private double a;
  private double b;
  private double c;

  public QuadJoystickScaling() {
    this(1, 0, 0);
  }

  public QuadJoystickScaling(double a, double b, double c) {
    this.a = a;
    this.b = b;
    this.c = c;
  }

  @Override
  public double scale(double input) {
    return (a * pow(input, 2)) + (b * input) + c;
  }
}
