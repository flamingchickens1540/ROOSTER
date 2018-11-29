package org.team1540.rooster.motionprofiling;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import org.jetbrains.annotations.NotNull;
import org.team1540.rooster.motionprofiling.MotionProfile.Point;

/**
 * Helper to execute motion profiles. This class has no dependency on WPILib/any external library.
 *
 * The profile-following algorithm used here is a derivative of the algorithm used by Team 2471. The
 * output of a heading-based PI loop is added to the profile's position setpoint and passed to the
 * motor controllers, with additional velocity, acceleration, and static friction feed-forwards (in
 * line with <a href="https://www.chiefdelphi.com/media/papers/3402">Eli Barnett's drive
 * characterization method</a>) added via throttle bump.
 * <p>
 * It is designed to use native Talon SRX position PID control with a throttle bump, but the output
 * could instead be used to control a RIO-side PID loop.
 * <p>
 * This class is stateful; it keeps track of the last time {@link #get(double, double)} get()} was
 * called, and also has an integral accumulator for the gyro PI controller. With that in mind, if
 * using a {@code FollowProfile} instance multiple times, call {@link #reset()} before beginning to
 * execute the second, third, etc. profiles.
 */
public class ProfileFollower {

  @NotNull
  private MotionProfile left;
  @NotNull
  private MotionProfile right;

  private double lVelCoeff;
  private double lVelIntercept;
  private double lAccelCoeff;
  private double rVelCoeff;
  private double rVelIntercept;
  private double rAccelCoeff;
  private double headingP;
  private double headingI;

  private double gyroIAccum;

  private double profTime;

  private double lastTime = -1;

  /**
   * Creates a {@code ProfileFollower}.
   *
   * For an explanation of units, see the {@linkplain org.team1540.rooster.motionprofiling package
   * docs}.
   *
   * @param lVelCoeff The left velocity coefficient (kV), in bump units per profile unit per
   * second.
   * @param lVelIntercept The left velocity intercept (VIntercept), in bump units.
   * @param lAccelCoeff The left acceleration coefficient (kA), in bump units per profile unit per
   * second squared.
   * @param rVelCoeff The right velocity coefficient (kV), in bump units per profile unit per
   * second.
   * @param rVelIntercept The right velocity intercept (VIntercept), in bump units.
   * @param rAccelCoeff The right acceleration coefficient (kA), in bump units per profile unit per
   * second squared.
   * @param headingP The P coefficient for the heading controller, in profile units per radian.
   * @param headingI The I coefficient for the heading controller, in profile units per
   * radian-second.
   */
  public ProfileFollower(
      @NotNull MotionProfile left,
      @NotNull MotionProfile right, double lVelCoeff, double lVelIntercept, double lAccelCoeff,
      double rVelCoeff, double rVelIntercept, double rAccelCoeff, double headingP,
      double headingI) {
    this.left = left;
    this.right = right;
    this.lVelCoeff = lVelCoeff;
    this.lVelIntercept = lVelIntercept;
    this.lAccelCoeff = lAccelCoeff;
    this.rVelCoeff = rVelCoeff;
    this.rVelIntercept = rVelIntercept;
    this.rAccelCoeff = rAccelCoeff;
    this.headingP = headingP;
    this.headingI = headingI;
  }

  /**
   * Get the output from the motion profile at a given time (usually the current time).
   *
   * @param heading The current gyro heading, from 0 to 2&pi; inclusive.
   * @param time The time, in seconds, since motion profile execution began. This is used to find
   * the segment to execute.
   * @return A {@link ProfileDriveSignal} describing the necessary drive commands.
   */
  @NotNull
  public ProfileDriveSignal get(double heading, double time) {
    Point leftSegment = getCurrentSegment(left, time);
    Point rightSegment = getCurrentSegment(right, time);

    double headingTarget = leftSegment.heading;

    // basically magic https://stackoverflow.com/a/2007279
    double headingError = atan2(sin(heading - headingTarget), cos(heading - headingTarget));

    double timeSinceLast = lastTime == -1 ? 0 : time - lastTime;
    gyroIAccum += headingError * timeSinceLast;

    lastTime = time;

    double gyroPOut = headingError * headingP;
    double gyroIOut = gyroIAccum * headingI;

    double leftVelFOut = lVelCoeff * leftSegment.velocity;
    double rightVelFOut = rVelCoeff * rightSegment.velocity;

    double leftVelInterceptOut =
        leftSegment.velocity == 0 ? 0 : Math.copySign(lVelIntercept, leftSegment.velocity);
    double rightVelInterceptOut =
        rightSegment.velocity == 0 ? 0 : Math.copySign(rVelIntercept, rightSegment.velocity);

    double leftAccelFOut = lAccelCoeff * leftSegment.acceleration;
    double rightAccelFOut = rAccelCoeff * rightSegment.acceleration;

    return new ProfileDriveSignal(
        leftSegment.position - gyroPOut - gyroIOut,
        leftVelFOut + leftVelInterceptOut + leftAccelFOut,
        rightSegment.position + gyroPOut + gyroIOut,
        rightVelFOut + rightVelInterceptOut + rightAccelFOut
    );
  }

  /**
   * Reset the profile follower.
   *
   * This resets the follower so that it can be used multiple times.
   */
  public void reset() {
    gyroIAccum = 0;
    lastTime = -1;
  }

  /**
   * Get the current integral accumulator for the gyro PI controller.
   *
   * @return The integral accumulator.
   */
  public double getGyroIAccum() {
    return gyroIAccum;
  }


  /**
   * Get the current gyro error being fed into the PI controller.
   *
   * @param heading The current gyro heading reading, in radians from 0 to 2&pi;.
   * @param time The current time, in seconds.
   * @return The heading error, in radians.
   */
  public double getGyroError(double heading, double time) {
    double tgtHeading = getCurrentPointLeft(time).heading;

    return atan2(sin(heading - tgtHeading), cos(heading - tgtHeading));
  }

  /**
   * Get the currently executing {@code Point} on the left side.
   *
   * @param time The current time, in seconds.
   * @return The current {@code Point}.
   */
  @NotNull
  public Point getCurrentPointLeft(double time) {
    return getCurrentSegment(left, time);
  }


  /**
   * Get the currently executing {@code Point} on the right side.
   *
   * @param time The current time, in seconds.
   * @return The current {@code Point}.
   */
  @NotNull
  public Point getCurrentPointRight(double time) {
    return getCurrentSegment(right, time);
  }

  /**
   * Gets the total time to execute the profile.
   *
   * @return The time to execute the currently loaded profile, in seconds.
   */
  public double getProfileTime() {
    return profTime;
  }

  /**
   * Get whether the profile is finished (i.e. <code>time &gt; {@link #getProfileTime()}</code>}
   *
   * @param time The current time since the profile began executing, in seconds.
   * @return {@code true} if the profile is finished; {@code false} otherwise.
   */
  public boolean isProfileFinished(double time) {
    return time > profTime;
  }

  /**
   * A signal to be sent to the robot drive, consisting of left and right position setpoints and
   * feed-forward bumps.
   */
  public static class ProfileDriveSignal {

    /**
     * The left-side position setpoint, in {@linkplain org.team1540.rooster.motionprofiling profile
     * units}.
     */
    public final double leftSetpoint;
    /**
     * The left-side feed-forward throttle bump, in fractions of motor throttle (i.e. 0.5 == 50% of
     * max motor throttle).
     */
    public final double leftBump;
    /**
     * The right-side position setpoint, in {@linkplain org.team1540.rooster.motionprofiling profile
     * units}.
     */
    public final double rightSetpoint;
    /**
     * The right-side feed-forward throttle bump, in fractions of motor throttle (i.e. 0.5 == 50% of
     * max motor throttle).
     */
    public final double rightBump;

    public ProfileDriveSignal(double lSetpoint, double lBump, double rSetpoint, double rBump) {
      this.leftSetpoint = lSetpoint;
      this.leftBump = lBump;
      this.rightSetpoint = rSetpoint;
      this.rightBump = rBump;
    }
  }

  @NotNull
  private static Point getCurrentSegment(@NotNull MotionProfile trajectory, double currentTime) {
    // Start from the current time and find the closest point.
    int startIndex = Math.toIntExact(Math.round(currentTime / trajectory.get(0).dt));

    int length = trajectory.size();
    int index = startIndex;
    if (startIndex >= length - 1) {
      index = length - 1;
    }
    return trajectory.get(index);
  }
}
