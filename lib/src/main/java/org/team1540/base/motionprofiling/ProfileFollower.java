package org.team1540.base.motionprofiling;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.util.Arrays;
import org.jetbrains.annotations.NotNull;
import org.team1540.base.motionprofiling.MotionProfile.Point;

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
 */
public class ProfileFollower {

  @NotNull
  private MotionProfile left;
  @NotNull
  private MotionProfile right;

  private double velCoeff;
  private double velIntercept;
  private double accelCoeff;
  private double headingP;
  private double headingI;

  private double gyroIAccum;

  private double profTime;

  private double lastTime = -1;

  /**
   * Creates a {@code ProfileFollower}.
   *
   * For an explanation of units, see the {@linkplain org.team1540.base.motionprofiling package
   * docs}.
   *
   * @param velCoeff The velocity coefficient (kV), in bump units per profile unit per second.
   * @param velIntercept The velocity intercept (VIntercept), in bump units.
   * @param accelCoeff The acceleration coefficient (kA), in bump units per profile unit per second
   * squared.
   * @param headingP The P coefficient for the heading controller, in profile units per radian.
   * @param headingI The I coefficient for the heading controller, in profile units per
   * radian-second.
   */
  public ProfileFollower(@NotNull MotionProfile left, @NotNull MotionProfile right, double velCoeff,
      double velIntercept, double accelCoeff, double headingP, double headingI) {
    this.left = left;
    this.right = right;
    this.velCoeff = velCoeff;
    this.velIntercept = velIntercept;
    this.accelCoeff = accelCoeff;
    this.headingP = headingP;
    this.headingI = headingI;

    profTime = Math.max(Arrays.stream(left.points).mapToDouble(s -> s.dt).sum(),
        Arrays.stream(right.points).mapToDouble(s -> s.dt).sum());
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

    double leftVelFOut = velCoeff * leftSegment.velocity;
    double rightVelFOut = velCoeff * rightSegment.velocity;

    double leftVelInterceptOut =
        leftSegment.velocity == 0 ? 0 : Math.copySign(velIntercept, leftSegment.velocity);
    double rightVelInterceptOut =
        rightSegment.velocity == 0 ? 0 : Math.copySign(velIntercept, rightSegment.velocity);

    double leftAccelFOut = accelCoeff * leftSegment.acceleration;
    double rightAccelFOut = accelCoeff * rightSegment.acceleration;

    return new ProfileDriveSignal(
        leftSegment.position - gyroPOut - gyroIOut,
        leftVelFOut + leftVelInterceptOut + leftAccelFOut,
        rightSegment.position + gyroPOut + gyroIOut,
        rightVelFOut + rightVelInterceptOut + rightAccelFOut
    );
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
     * The left-side position setpoint, in {@linkplain org.team1540.base.motionprofiling profile
     * units}.
     */
    public final double leftSetpoint;
    /**
     * The left-side feed-forward throttle bump, in fractions of motor throttle (i.e. 0.5 == 50% of
     * max motor throttle).
     */
    public final double leftBump;
    /**
     * The right-side position setpoint, in {@linkplain org.team1540.base.motionprofiling profile
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
