package org.team1540.base.drive.pipeline;

import java.util.OptionalDouble;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class TankDriveData {

  /**
   * The drive data for the left side.
   */
  public final DriveData left;
  /**
   * The drive data for the right side.
   */
  public final DriveData right;
  /**
   * The desired turning rate in radians from 0 (straight forward) to 2&pi;, increasing clockwise,
   * or an empty optional if heading should not be controlled.
   */
  public final OptionalDouble heading;
  /**
   * The desired turning rate in radians/sec, or an empty optional if turning rate should not be
   * controlled.
   */
  public final OptionalDouble turningRate;

  public TankDriveData(DriveData left, DriveData right, OptionalDouble heading,
      OptionalDouble turningRate) {
    this.left = left;
    this.right = right;
    this.heading = heading;
    this.turningRate = turningRate;
  }


}
