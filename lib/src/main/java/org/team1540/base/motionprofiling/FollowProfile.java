package org.team1540.base.motionprofiling;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;
import java.util.function.DoubleSupplier;
import org.jetbrains.annotations.NotNull;
import org.team1540.base.motionprofiling.ProfileFollower.ProfileDriveSignal;
import org.team1540.base.util.AsyncCommand;

/**
 * {@link edu.wpi.first.wpilibj.command.Command Command} to execute a motion profile. This is an
 * {@link AsyncCommand}-based wrapper around {@link ProfileFollower} which handles creating the
 * instance, running it in a fast loop, and sending the output to the motors.
 *
 * @see FollowProfileFactory
 * @see ProfileFollower
 */
public class FollowProfile extends AsyncCommand {

  @NotNull
  private MotionProfile left;
  @NotNull
  private MotionProfile right;

  @NotNull
  private SetpointConsumer leftSetpointConsumer;
  @NotNull
  private SetpointConsumer rightSetpointConsumer;

  @NotNull
  private DoubleSupplier headingSupplier;

  private double velCoeff;
  private double velIntercept;
  private double accelCoeff;
  private double headingP;
  private double headingI;

  private double gyroIAccum;

  private ProfileFollower follower;

  /**
   * Timer for the amount of time since profiling started (used to find current point)
   */
  private Timer timer = new Timer();

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

    follower = new ProfileFollower(left, right, velCoeff, velIntercept, accelCoeff, headingP,
        headingI);
  }

  /**
   * Get the time since starting the motion profile.
   *
   * @return The time, in seconds, since beginning to execute a motion profile, or 0 if not
   * currently executing a profile.
   */
  public double getExecutionTime() {
    return isRunning() ? timer.get() : 0;
  }

  /**
   * Get the underlying {@link ProfileFollower}
   *
   * @return The underlying {@link ProfileFollower}. Any modifications to this object will affect
   * this command.
   */
  public ProfileFollower getFollower() {
    return follower;
  }

  @Override
  protected void runPeriodic() {
    // check finish status
    if (follower.isProfileFinished(timer.get())) {
      markAsFinished();
    }

    ProfileDriveSignal sig = follower.get(headingSupplier.getAsDouble(), timer.get());

    leftSetpointConsumer.set(sig.leftSetpoint, sig.leftBump);
    rightSetpointConsumer.set(sig.rightSetpoint, sig.rightBump);
  }

  @Override
  protected void runInitial() {
    follower.reset();
    timer.start();
  }

  @Override
  protected void runEnd() {
    timer.stop();
    timer.reset();
  }
}
