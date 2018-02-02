package org.team1540.base.drive;

public class LinearJoystickScaling implements JoystickScaling {

  private double a;
  private double b;

  public LinearJoystickScaling(double a) {
    this(a, 0);
  }

  public LinearJoystickScaling(double a, double b) {
    this.a = a;
    this.b = b;
  }

  @Override
  public double scale(double input) {
    return (a * input) + b;
  }
}
