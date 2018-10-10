package org.team1540.base.drive;


/*
Implementation note:
This is not a command, as I realized that with PIDDrive so much of the configuration was simply
dealing with scaling the joysticks and creating a full pipeline from joystick to motor controller.
This just takes speed values in a method, and outputs a drive signal. This also means it can
be plugged into a motion profile executor.
*/

import org.jetbrains.annotations.Contract;

public class CharacterizedDriveHelper {

  private final double kV;
  private final double vIntercept;

  // TODO: Add explanation of units in class docs

  /**
   * Creates a {@code CharacterizedDriveHelper} with the provided \(k_v\) and \(v_{Intercept}\)
   *
   * @param kV The velocity constant feed-forward, in output units per speed unit.
   * @param vIntercept The velocity intercept, in output units
   */
  public CharacterizedDriveHelper(double kV, double vIntercept) {
    this.kV = kV;
    this.vIntercept = vIntercept;
  }

  @Contract(pure = true)
  public OpenLoopDriveSignal getDriveSignal(double leftSpeed, double rightSpeed) {
    double left = getThrottle(leftSpeed);
    double right = getThrottle(rightSpeed);
    return new OpenLoopDriveSignal(left, right);
  }

  @Contract(pure = true)
  private double getThrottle(double wantedSpeed) {
    return (kV * wantedSpeed) + vIntercept;
  }
}
