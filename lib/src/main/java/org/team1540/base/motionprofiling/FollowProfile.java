package org.team1540.base.motionprofiling;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;
import java.util.Arrays;
import java.util.function.DoubleSupplier;
import org.jetbrains.annotations.NotNull;
import org.team1540.base.motionprofiling.MotionProfile.Point;
import org.team1540.base.util.AsyncCommand;

/**
 * {@link edu.wpi.first.wpilibj.command.Command Command} to execute a motion profile.
 * <p>
 * The profile-following algorithm used here is a derivative of the algorithm used by Team 2471. The
 * output of a heading-based PI loop is added to the profile's position setpoint and passed to the
 * motor controllers, with additional velocity, acceleration, and static friction feed-forwards (in
 * line with <a href="https://www.chiefdelphi.com/media/papers/3402">Eli Barnett's drive
 * characterization method</a>) added via throttle bump.
 * <p>
 * It is designed to use native Talon SRX position PID control with a throttle bump, but the
 * provided {@link SetpointConsumer SetpointConsumers} could instead be used to control a RIO-side
 * PID loop.
 *
 * @see FollowProfileFactory
 */
public class FollowProfile extends AsyncCommand {

  @NotNull
  MotionProfile left;
  @NotNull
  MotionProfile right;

  @NotNull
  private SetpointConsumer leftSetpointConsumer;
  @NotNull
  private SetpointConsumer rightSetpointConsumer;

  @NotNull
  private DoubleSupplier headingSupplier;

  private double profTime;

  private double velCoeff;
  private double velIntercept;
  private double accelCoeff;
  private double headingP;
  private double headingI;

  private double gyroIAccum;

  /**
   * Timer for the amount of time since profiling started (used to find current point)
   */
  private Timer timer = new Timer();

  /**
   * Timer for the amount of time since the loop last ran (used for integral term)
   */
  private Timer execTimer = new Timer();

  /**
   * Creates a {@code FollowProfile} command.
   *
   * @param subsystems The required subsystems for this command.
   * @param leftSetpointConsumer The {@link SetpointConsumer} for the left-side motors.
   * @param rightSetpointConsumer The {@link SetpointConsumer} for the right-side motors.
   * @param headingSupplier A {@link DoubleSupplier} that returns the robot's current heading in
   * radians from 0 to 2&pi;.
   * @param loopFreq The interval, in milliseconds, between profile loop execution.
   * @param velCoeff The velocity coefficient (kV), in bump units per profile unit per second.
   * @param velIntercept The velocity intercept (VIntercept), in bump units.
   * @param accelCoeff The acceleration coefficient (kA), in bump units per profile unit per second
   * squared.
   * @param headingP The P coefficient for the heading controller, in profile units per radian.
   * @param headingI The I coefficient for the heading controller, in profile units per
   * radian-second.
   */
  public FollowProfile(@NotNull MotionProfile left, @NotNull MotionProfile right,
      @NotNull Subsystem[] subsystems,
      @NotNull SetpointConsumer leftSetpointConsumer,
      @NotNull SetpointConsumer rightSetpointConsumer, @NotNull DoubleSupplier headingSupplier,
      long loopFreq, double velCoeff, double velIntercept,
      double accelCoeff, double headingP, double headingI) {
    super(loopFreq);
    for (Subsystem subsystem : subsystems) {
      requires(subsystem);
    }
    this.left = left;
    this.right = right;
    this.leftSetpointConsumer = leftSetpointConsumer;
    this.rightSetpointConsumer = rightSetpointConsumer;
    this.headingSupplier = headingSupplier;
    this.velCoeff = velCoeff;
    this.velIntercept = velIntercept;
    this.accelCoeff = accelCoeff;
    this.headingP = headingP;
    this.headingI = headingI;

    profTime = Arrays.stream(left.points).mapToDouble(s -> s.dt).sum();
  }

  private Point getCurrentSegment(MotionProfile trajectory, double currentTime) {
    // Start from the current time and find the closest point.
    int startIndex = Math.toIntExact(Math.round(currentTime / trajectory.get(0).dt));

    int length = trajectory.size();
    int index = startIndex;
    if (startIndex >= length - 1) {
      index = length - 1;
    }
    return trajectory.get(index);
  }

  @Override
  protected void runPeriodic() {
    Point leftSegment = getCurrentSegment(left, timer.get());
    Point rightSegment = getCurrentSegment(right, timer.get());
    // check finish status
    if (timer.get() > profTime) {
      markAsFinished();
    }

    double heading = headingSupplier.getAsDouble();
    double headingTarget = leftSegment.heading;

    double error1 = heading - headingTarget;
    double error2 = heading - headingTarget + 2 * Math.PI;
    double error3 = heading - headingTarget - 2 * Math.PI;

    // basically magic https://stackoverflow.com/a/2007279
    double headingError = atan2(sin(heading - headingTarget), cos(heading - headingTarget));

    gyroIAccum += headingError * execTimer.get();
    execTimer.reset();
    execTimer.start();

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

    leftSetpointConsumer.set(leftSegment.position - gyroPOut - gyroIOut,
        leftVelFOut + leftVelInterceptOut + leftAccelFOut);
    rightSetpointConsumer.set(rightSegment.position + gyroPOut + gyroIOut,
        rightVelFOut + rightVelInterceptOut + rightAccelFOut);
  }

  @Override
  protected void runInitial() {
    timer.start();
    execTimer.reset();
    gyroIAccum = 0;
  }

  @Override
  protected void runEnd() {
    timer.stop();
    timer.reset();
    execTimer.stop();
    execTimer.reset();
  }
}
