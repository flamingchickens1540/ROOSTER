package org.team1540.rooster.motionprofiling;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;
import java.util.function.DoubleSupplier;
import org.jetbrains.annotations.NotNull;
import org.team1540.rooster.motionprofiling.ProfileFollower.ProfileDriveSignal;
import org.team1540.rooster.util.AsyncCommand;

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

  private double lVelCoeff;
  private double lVelIntercept;
  private double lAccelCoeff;
  private double rVelCoeff;
  private double rVelIntercept;
  private double rAccelCoeff;
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
  public FollowProfile(@NotNull MotionProfile left, @NotNull MotionProfile right,
      @NotNull Subsystem[] subsystems,
      @NotNull SetpointConsumer leftSetpointConsumer,
      @NotNull SetpointConsumer rightSetpointConsumer,
      @NotNull DoubleSupplier headingSupplier,
      long loopFreq, double lVelCoeff, double lVelIntercept, double lAccelCoeff, double rVelCoeff,
      double rVelIntercept, double rAccelCoeff, double headingP, double headingI) {
    super(loopFreq);
    for (Subsystem subsystem : subsystems) {
      requires(subsystem);
    }
    this.left = left;
    this.right = right;
    this.leftSetpointConsumer = leftSetpointConsumer;
    this.rightSetpointConsumer = rightSetpointConsumer;
    this.headingSupplier = headingSupplier;
    this.lVelCoeff = lVelCoeff;
    this.lVelIntercept = lVelIntercept;
    this.lAccelCoeff = lAccelCoeff;
    this.rVelCoeff = rVelCoeff;
    this.rVelIntercept = rVelIntercept;
    this.rAccelCoeff = rAccelCoeff;
    this.headingP = headingP;
    this.headingI = headingI;

    follower = new ProfileFollower(left, right, lVelCoeff, lVelIntercept, lAccelCoeff, rVelCoeff,
        rVelIntercept, rAccelCoeff, headingP, headingI);
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