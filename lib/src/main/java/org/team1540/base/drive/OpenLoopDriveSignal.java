package org.team1540.base.drive;

/**
 * Data structure for a drive's left and right throttles.
 */
public class OpenLoopDriveSignal {

  private final double leftThrottle;
  private final double rightThrottle;

  public OpenLoopDriveSignal(double leftThrottle, double rightThrottle) {
    this.leftThrottle = leftThrottle;
    this.rightThrottle = rightThrottle;
  }

  public double getLeftThrottle() {
    return leftThrottle;
  }

  public double getRightThrottle() {
    return rightThrottle;
  }
}
