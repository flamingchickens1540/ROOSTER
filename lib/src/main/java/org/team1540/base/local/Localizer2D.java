package org.team1540.base.local;

import java.util.function.DoubleSupplier;
import org.team1540.base.util.Executable;

public class Localizer2D implements Executable {

  private double xpos;
  private double ypos;

  private double distancePrevLeft;
  private double distancePrevRight;
  private double angleRadsPrev;

  private DoubleSupplier lDistSupplier;
  private DoubleSupplier rDistSupplier;
  private DoubleSupplier headingSupplier;

  public Localizer2D(DoubleSupplier lDistSupplier, DoubleSupplier rDistSupplier,
      DoubleSupplier headingSupplier) {
    this.lDistSupplier = lDistSupplier;
    this.rDistSupplier = rDistSupplier;
    this.headingSupplier = headingSupplier;
    reset();
  }

  public synchronized void reset() {
    xpos = 0;
    ypos = 0;

    distancePrevLeft = 0;
    distancePrevRight = 0;
    angleRadsPrev = 0;
  }


  @Override
  public synchronized void execute() {
    double distanceLeft = lDistSupplier.getAsDouble();
    double distanceRight = rDistSupplier.getAsDouble();
    double angleRads = headingSupplier.getAsDouble();
    double deltaDistanceLeft = distanceLeft - distancePrevLeft;
    double deltaDistanceRight = distanceRight - distancePrevRight;
    double deltaRads = angleRads - angleRadsPrev;

    distancePrevLeft = distanceLeft;
    distancePrevRight = distanceRight;
    angleRadsPrev = angleRads;

    // TODO: fall back on encoder pos
    double deltaForward = (deltaDistanceLeft + deltaDistanceRight) / 2; // Linear default
    double deltaLeft = 0;

    if (deltaRads != 0) {
      // Calculate radius of turn
      double avgRadius =
          (calcRadius(deltaDistanceLeft, deltaRads) + calcRadius(deltaDistanceRight, deltaRads))
              / 2;

      // Increment the accumulators
      deltaForward = calcDeltaY(avgRadius, deltaRads);
      deltaLeft = calcDeltaX(avgRadius, deltaRads);
    }

    xpos += deltaLeft * Math.cos(angleRads + Math.PI / 2) + deltaForward * Math.cos(angleRads);
    ypos += deltaLeft * Math.sin(angleRads + Math.PI / 2) + deltaForward * Math.sin(angleRads);
  }

  public synchronized double getX() {
    return xpos;
  }

  public synchronized double getY() {
    return ypos;
  }

  private static double calcRadius(double arcLength, double angleRads) {
    return arcLength / angleRads;
  }

  private static double calcDeltaX(double radius, double deltaRads) {
    return radius * (1.0 - Math.cos(deltaRads));
  }

  private static double calcDeltaY(double radius, double deltaRads) {
    return radius * Math.sin(deltaRads);
  }
}
