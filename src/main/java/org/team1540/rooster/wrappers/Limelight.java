package org.team1540.rooster.wrappers;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class to interface with a Limelight vision system through NetworkTables. This class allows for
 * easy and compile-time-checked access to the Limelight's configuration and outputs. For full
 * details on configuration, see the <a href="http://docs.limelightvision.io/en/latest/networktables_api.html">NetworkTables
 * API documentation</a>.
 */
public class Limelight {

  private NetworkTable table;

  /**
   * Creates a new {@code Limelight} pulling data from the {@code limelight} network table.
   */
  public Limelight() {
    this("limelight");
  }

  /**
   * Creates a new {@code Limelight} pulling data from the specified table.
   *
   * @param tableName The name of the table.
   */
  public Limelight(String tableName) {
    this.table = NetworkTableInstance.getDefault().getTable(tableName);
  }

  // output

  /**
   * Gets whether the Limelight has any valid targets.
   *
   * @return {@code true} if the connected Limelight has a valid target; {@code false} otherwise.
   */
  public boolean isValidTarget() {
    return table.getEntry("tv").getDouble(0) == 1;
  }

  /**
   * Gets the horizontal offset, in degrees, of the Limelight's target relative to the crosshair.
   *
   * @return The offset in degrees, from -27 to 27.
   */
  public double getHorizontalOffset() {
    return table.getEntry("tx").getDouble(0);
  }

  /**
   * Gets the vertical offset, in degrees, of the Limelight's target relative to the crosshair.
   *
   * @return The offset in degrees, from -20.5 to 20.5.
   */
  public double getVerticalOffset() {
    return table.getEntry("ty").getDouble(0);
  }

  /**
   * Gets the area of the Limelight's target as a percentage of its field of view.
   *
   * @return The target area, from 0 to 100.
   */
  public double getTargetArea() {
    return table.getEntry("ta").getDouble(0);
  }

  /**
   * Gets the skew or rotation of the Limelight's target.
   *
   * @return The skew of the target, in degrees from -90 to 0.
   */
  public double getSkew() {
    return table.getEntry("ts").getDouble(0);
  }

  /**
   * Gets the latency caused by the Limelight's processing pipeline. This is not the
   * photon-to-RoboRIO latency: at least 11 ms should be added to account for additional camera
   * latency.
   *
   * @return The pipeline's latency contribution, in milliseconds.
   */
  public double getPipelineLatency() {
    return table.getEntry("tl").getDouble(0);
  }

  // raw contour output

  /**
   * Gets a raw contour from the Limelight.
   *
   * @param contourNum The number of the contour, from 0 to 2 inclusive.
   * @return A {@link RawContour} describing the contour.
   * @throws IllegalArgumentException If {@code contourNum} is less than 0 or greater than 2.
   */
  public RawContour getRawContour(int contourNum) {
    if (contourNum < 0 || contourNum > 2) {
      throw new IllegalArgumentException("Invalid contour");
    }

    return new RawContour(table.getEntry("tx" + contourNum).getDouble(0),
        table.getEntry("ty" + contourNum).getDouble(0),
        table.getEntry("ta" + contourNum).getDouble(0),
        table.getEntry("ts" + contourNum).getDouble(0));
  }

  /**
   * Gets all three raw contours from the Limelight.
   *
   * @return An array of three {@link RawContour RawContours} corresponding to contours 0, 1, and 2
   * respectively.
   */
  public RawContour[] getRawContours() {
    RawContour[] rawContours = new RawContour[3];

    for (int i = 0; i <= 2; i++) {
      rawContours[i] = getRawContour(i);
    }

    return rawContours;
  }

  // options

  /**
   * Gets the current LED mode of the limelight.
   *
   * @return The current LED mode, or {@code null} if the mode entry could not be parsed or was not
   * present.
   */
  @Nullable
  public LedMode getLedMode() {
    int raw = (int) table.getEntry("ledMode").getNumber(-1);
    for (LedMode mode : LedMode.values()) {
      if (mode.value == raw) {
        return mode;
      }
    }

    return null;
  }

  /**
   * Sets the Limelight's LED mode.
   *
   * @param mode The desired setting.
   */
  public void setLedMode(@NotNull LedMode mode) {
    table.getEntry("ledMode").forceSetNumber(mode.value);
  }

  /**
   * Gets whether the Limelight is in drive camera mode. Drive camera mode increases camera exposure
   * and disables vision processing.
   *
   * @return {@code true} if the Limelight is in drive camera mode; {@code false} otherwise.
   */
  public boolean isDriveCameraMode() {
    return table.getEntry("camMode").getDouble(0) == 1;
  }

  /**
   * Enables or disables the Limelight's drive camera mode. Drive camera mode increases camera
   * exposure and disables vision processing.
   *
   * @param enable Whether to enable drive camera mode.
   */
  public void setDriveCameraMode(boolean enable) {
    table.getEntry("camMode").forceSetNumber(enable ? 1 : 0);
  }

  /**
   * Gets the Limelight's currently active vision pipeline.
   *
   * @return The Limelight's active vision pipeline, from 0 to 9 inclusive.
   */
  public int getCurrentPipeline() {
    return table.getEntry("pipeline").getNumber(0).intValue();
  }

  /**
   * Sets the Limelight's currently active vision pipeline.
   *
   * @param pipeline The number of the pipeline to activate, from 0 to 9 inclusive.
   * @throws IllegalArgumentException If {@code pipeline} is less than 0 or greater than 9.
   */
  public void setCurrentPipeline(int pipeline) {
    if (pipeline < 0 || pipeline > 9) {
      throw new IllegalArgumentException("Invalid pipeline");
    }

    table.getEntry("pipeline").forceSetNumber(pipeline);
  }

  /**
   * Gets the current picture-in-picture mode of the limelight.
   *
   * @return The current picture-in-picture mode, or {@code null} if the mode entry could not be
   * parsed or was not present.
   */
  @Nullable
  public PipMode getPipMode() {
    int raw = (int) table.getEntry("stream").getNumber(-1);
    for (PipMode mode : PipMode.values()) {
      if (mode.value == raw) {
        return mode;
      }
    }

    return null;
  }

  /**
   * Sets the Limelight's picture-in-picture mode.
   *
   * @param mode The desired setting.
   */
  public void setPipMode(@NotNull PipMode mode) {
    table.getEntry("stream").forceSetNumber(mode.value);
  }


  /**
   * Gets whether the Limelight is currently taking snapshots.
   *
   * @return {@code true} if the connected Limelight is currently taking snapshots; {@code false}
   * otherwise.
   */
  public boolean isTakingSnapshots() {
    return table.getEntry("snapshot").getDouble(0) == 1;
  }

  /**
   * Sets whether the Limelight should take snapshots. If enabled, the Limelight will take two
   * snapshots per second.
   *
   * @param enable Whether to enable taking snapshots.
   */
  public void setTakingSnapshots(boolean enable) {
    table.getEntry("snapshot").forceSetNumber(enable ? 1 : 0);
  }


  public enum LedMode {
    /**
     * Use the LED mode set in the currently active Limelight pipeline.
     */
    PIPELINE(0),
    /**
     * Force LEDs to off, regardless of current pipeline setting.
     */
    OFF(1),
    /**
     * Force LEDs to blink, regardless of current pipeline setting.
     */
    BLINK(2),
    /**
     * Force LEDs to on, regardless of current pipeline setting.
     */
    ON(3);

    final int value;

    LedMode(int value) {
      this.value = value;
    }
  }

  public enum PipMode {
    /**
     * Display side-by-side streams if a webcam is attached to the Limelight.
     */
    STANDARD(0),
    /**
     * Display the secondary camera stream in the lower-right corner of the primary camera stream.
     */
    PIP_MAIN(1),
    /**
     * Display the main camera stream in the lower-right corner of the secondary camera stream.
     */
    PIP_SECONDARY(1);

    final int value;

    PipMode(int value) {
      this.value = value;
    }
  }

  /**
   * A raw contour returned from a {@link Limelight}. An instance of this class can only be obtained
   * from the {@link Limelight#getRawContour(int)} and {@link Limelight#getRawContours()} methods.
   */
  public static class RawContour {

    /**
     * The x-position of the contour in raw screen space. This value can be from -1 to 1, with 0
     * being the center of the camera's field of view.
     */
    public final double x;
    /**
     * The y-position of the contour in raw screen space. This value can be from -1 to 1, with 0
     * being the center of the camera's field of view.
     */
    public final double y;
    /**
     * The area of the contour relative to the camera's field of view. This value is between 0 and
     * 100, with 100% indicating that the contour completely fills the camera's field of view.
     */
    public final double area;
    /**
     * The skew or rotation of the contour. This value is between -90 and 0 degrees.
     */
    public final double skew;

    private RawContour(double x, double y, double area, double skew) {
      this.x = x;
      this.y = y;
      this.area = area;
      this.skew = skew;
    }
  }
}
