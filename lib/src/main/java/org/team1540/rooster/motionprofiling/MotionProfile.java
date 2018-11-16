package org.team1540.rooster.motionprofiling;

import java.util.Objects;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A sequence of {@link Point Points} that can be executed by a {@link FollowProfile}.
 */
public class MotionProfile {

  /**
   * The {@link Point Points} in the motion profile.
   */
  @NotNull
  public final Point[] points;

  /**
   * Create a new {@link MotionProfile} from an array of points.
   *
   * @param points The points to use.
   */
  public MotionProfile(@NotNull Point[] points) {
    this.points = points;
  }

  /**
   * Gets the <i>n</i>th {@link Point} (0-indexed) in the motion profile.
   *
   * @param index The index of the point to get.
   * @return The point at the specified index.
   * @throws ArrayIndexOutOfBoundsException if {@code index} &ge; {@link #size()}.
   */
  @Contract(pure = true)
  public Point get(int index) {
    return points[index];
  }

  /**
   * Get the number of {@link Point points} in the profile.
   *
   * @return The number of points in the profile; specifically, {@code points.length}.
   */
  @Contract(pure = true)
  public int size() {
    return points.length;
  }

  /**
   * A single instant within a {@link MotionProfile}.
   */
  public static class Point {

    /**
     * The time change since the previous point, in seconds.
     */
    public double dt;
    /**
     * The x-position of the robot in {@linkplain org.team1540.rooster.motionprofiling profile units},
     * or 0 if not applicable.
     */
    public double x;
    /**
     * The y-position of the robot in {@linkplain org.team1540.rooster.motionprofiling profile units},
     * or 0 if not applicable.
     */
    public double y;
    /**
     * The position of the profiled mechanism, in {@linkplain org.team1540.rooster.motionprofiling
     * profile units}.
     */
    public double position;
    /**
     * The velocity of the profiled mechanism, in {@linkplain org.team1540.rooster.motionprofiling
     * profile units} per second.
     */
    public double velocity;
    /**
     * The acceleration of the profiled mechanism, in {@linkplain org.team1540.rooster.motionprofiling
     * profile units} per second squared.
     */
    public double acceleration;
    /**
     * The jerk of the profiled mechanism, in {@linkplain org.team1540.rooster.motionprofiling profile
     * units} per second cubed.
     */
    public double jerk;
    /**
     * The robot's heading in radians, or 0 if not applicable.
     */
    public double heading;

    /**
     * Creates a new {@code Point}.
     *
     * @param dt The time change since the previous point, in seconds.
     * @param x The x-position of the robot in {@linkplain org.team1540.rooster.motionprofiling profile
     * units}, or 0 if not applicable.
     * @param y The y-position of the robot in {@linkplain org.team1540.rooster.motionprofiling profile
     * units}, or 0 if not applicable.
     * @param position The position of the profiled mechanism, in {@linkplain
     * org.team1540.rooster.motionprofiling profile units}.
     * @param velocity The velocity of the profiled mechanism, in {@linkplain
     * org.team1540.rooster.motionprofiling profile units} per second.
     * @param acceleration The acceleration of the profiled mechanism, in {@linkplain
     * org.team1540.rooster.motionprofiling profile units} per second squared.
     * @param jerk The jerk of the profiled mechanism, in {@linkplain org.team1540.rooster.motionprofiling
     * profile units} per second cubed.
     * @param heading The robot's heading in radians, or 0 if not applicable.
     */
    public Point(double dt, double x, double y, double position, double velocity,
        double acceleration, double jerk, double heading) {
      this.dt = dt;
      this.x = x;
      this.y = y;
      this.position = position;
      this.velocity = velocity;
      this.acceleration = acceleration;
      this.jerk = jerk;
      this.heading = heading;
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(@Nullable Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof Point)) {
        return false;
      }
      Point point = (Point) o;
      return Double.compare(point.dt, dt) == 0 &&
          Double.compare(point.x, x) == 0 &&
          Double.compare(point.y, y) == 0 &&
          Double.compare(point.position, position) == 0 &&
          Double.compare(point.velocity, velocity) == 0 &&
          Double.compare(point.acceleration, acceleration) == 0 &&
          Double.compare(point.jerk, jerk) == 0 &&
          Double.compare(point.heading, heading) == 0;
    }

    @Override
    public int hashCode() {
      return Objects.hash(dt, x, y, position, velocity, acceleration, jerk, heading);
    }
  }
}
