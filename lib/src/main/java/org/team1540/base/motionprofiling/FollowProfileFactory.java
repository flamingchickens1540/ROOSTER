package org.team1540.base.motionprofiling;

import edu.wpi.first.wpilibj.command.Subsystem;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import java.io.File;
import java.util.function.DoubleSupplier;
import org.jetbrains.annotations.NotNull;

/**
 * Produces multiple similar {@link FollowProfile} commands for one subystem using common tuning
 * values. In general, one {@code FollowProfileFactory} should be created for each subsystem to be
 * motion-profiled. It can then be pre-loaded with common configuration and tuning values specific
 * to a subsystem, and kept as a single instance to create any needed {@code FollowProfile} commands
 * for that subsystem.
 * <p>
 * All setters in this class follow a builder pattern; i.e they return an instance of the object
 * they were called on. This allows for multiple set methods to be chained.
 *
 * @see FollowProfile
 */
public class FollowProfileFactory {

  @NotNull
  private Subsystem[] subsystems;
  @NotNull
  private SetpointConsumer leftSetpointConsumer;
  @NotNull
  private SetpointConsumer rightSetpointConsumer;

  @NotNull
  private DoubleSupplier headingSupplier = () -> 0;

  private long loopFreq = 20;

  private double velCoeff = 0;
  private double velIntercept = 0;
  private double accelCoeff = 0;
  private double headingP = 0;
  private double headingI = 0;

  /**
   * Constructs a {@code FollowProfileFactory} with the provided left and right set functions and
   * subsystems.
   *
   * @param left The {@link SetpointConsumer} for the left-side motors.
   * @param right The {@link SetpointConsumer} for the right-side motors.
   * @param subsystems The {@link Subsystem Subystems} that produced {@link FollowProfile} commands
   * should require.
   */
  public FollowProfileFactory(@NotNull SetpointConsumer left, @NotNull SetpointConsumer right,
      @NotNull Subsystem... subsystems) {
    this.leftSetpointConsumer = left;
    this.rightSetpointConsumer = right;
    this.subsystems = subsystems;
  }

  /**
   * Gets the subsystems used by produced {@link FollowProfile} commands.
   *
   * @return The required subsystems. Each of these will be declared as a requirement for each
   * produced command using {@code requires()}.
   */
  @NotNull
  public Subsystem[] getSubsystems() {
    return subsystems;
  }

  /**
   * Sets the subsystems required by produced {@link FollowProfile} commands.
   *
   * @param subsystems The required subsystems. Each of these will be declared as a requirement for
   * each produced command using {@code requires()}.
   * @return This {@code FollowProfileFactory} in a builder pattern.
   */
  @NotNull
  public FollowProfileFactory setSubsystems(@NotNull Subsystem... subsystems) {
    this.subsystems = subsystems;
    return this;
  }

  /**
   * Gets the {@link SetpointConsumer} for the left-side motors.
   *
   * @return The currently used {@link SetpointConsumer} for the left side.
   */
  @NotNull
  public SetpointConsumer getLeftSetpointConsumer() {
    return leftSetpointConsumer;
  }

  /**
   * Sets the {@link SetpointConsumer} for the left-side motors.
   *
   * @param leftSetpointConsumer A {@link SetpointConsumer} that takes a setpoint in profile units
   * and bump in bump units.
   */
  @NotNull
  public FollowProfileFactory setLeftSetpointConsumer(
      @NotNull SetpointConsumer leftSetpointConsumer) {
    this.leftSetpointConsumer = leftSetpointConsumer;
    return this;
  }

  /**
   * Gets the {@link SetpointConsumer} for the left-side motors.
   *
   * @return The currently used {@link SetpointConsumer} for the right side.
   */
  @NotNull
  public SetpointConsumer getRightSetpointConsumer() {
    return rightSetpointConsumer;
  }

  /**
   * @param rightSetpointConsumer A {@link SetpointConsumer} that takes a setpoint in profile units
   * and bump in bump units and passes them to the drivetrain.
   * @return This {@code FollowProfileFactory} in a builder pattern.
   */
  @NotNull
  public FollowProfileFactory setRightSetpointConsumer(
      @NotNull SetpointConsumer rightSetpointConsumer) {
    this.rightSetpointConsumer = rightSetpointConsumer;
    return this;
  }

  /**
   * Gets the {@link DoubleSupplier} used for the input of the heading loop.
   *
   * @return The heading supplier.
   */
  @NotNull
  public DoubleSupplier getHeadingSupplier() {
    return headingSupplier;
  }

  /**
   * Sets the {@link DoubleSupplier} used for the input of the heading loop.
   *
   * @param headingSupplier A {@link DoubleSupplier} that returns the robot's heading in radians
   * from 0 to 2&pi;.
   * @return This {@code FollowProfileFactory} in a builder pattern.
   */
  @NotNull
  public FollowProfileFactory setHeadingSupplier(@NotNull DoubleSupplier headingSupplier) {
    this.headingSupplier = headingSupplier;
    return this;
  }

  /**
   * Gets the delay between profile loop execution. Defaults to 20ms.
   *
   * @return The time between profile loop execution, in milliseconds.
   */
  public long getLoopFreq() {
    return loopFreq;
  }

  /**
   * Sets the delay between profile loop execution. Defaults to 20ms.
   *
   * @param loopFreq The time between loop executions, in milliseconds.
   * @return This {@code FollowProfileFactory} in a builder pattern.
   */
  @NotNull
  public FollowProfileFactory setLoopFreq(long loopFreq) {
    this.loopFreq = loopFreq;
    return this;
  }

  /**
   * Gets the velocity feed-forward coefficient. This is equivalent to the kV term in drive
   * characterization. Defaults to 0 (no feed-forward).
   *
   * @return The velocity feed-forward coefficient, in bump units per profile unit per second.
   */
  public double getVelCoeff() {
    return velCoeff;
  }

  /**
   * Sets the velocity feed-forward coefficent. This is equivalent to the kV term in drive *
   * characterization. Defaults to 0 (no feed-forward).
   *
   * @param velCoeff The velocity feed-forward coefficient, in bump units per profile unit per
   * second.
   * @return This {@code FollowProfileFactory} in a builder pattern.
   */
  @NotNull
  public FollowProfileFactory setVelCoeff(double velCoeff) {
    this.velCoeff = velCoeff;
    return this;
  }

  /**
   * Gets the velocity intercept, or VIntercept. (See Eli Barnett's drive characterization paper for
   * an explanation of why this is needed.)
   *
   * @return The velocity intercept, in bump units.
   */
  public double getVelIntercept() {
    return velIntercept;
  }

  /**
   * Sets the velocity intercept, or VIntercept. (See Eli Barnett's drive characterization paper for
   * an explanation of why this is needed.) Defaults to 0.
   *
   * @param velIntercept The velocity intercept, in bump units.
   * @return This {@code FollowProfileFactory} in a builder pattern.
   */
  @NotNull
  public FollowProfileFactory setVelIntercept(double velIntercept) {
    this.velIntercept = velIntercept;
    return this;
  }

  /**
   * Gets the acceleration feed-forward. This is equivalent to the kA term in drive
   * characterization. Defaults to 0 (no feed-forward).
   *
   * @return The currently set acceleration coefficient, in bump units per profile unit per second
   * squared.
   */
  public double getAccelCoeff() {
    return accelCoeff;
  }

  /**
   * Sets the acceleration feed-forward. This is equivalent to the kA term in drive
   * characterization. Defaults to 0 (no feed-forward).
   *
   * @param accelCoeff The acceleration coefficient, in bump units per profile unit per second
   * squared.
   * @return This {@code FollowProfileFactory} in a builder pattern.
   */
  @NotNull
  public FollowProfileFactory setAccelCoeff(double accelCoeff) {
    this.accelCoeff = accelCoeff;
    return this;
  }

  /**
   * Gets the P coefficient for the heading PI loop. Defaults to 0.
   *
   * @return The currently set P coefficient, in profile units per radian.
   */
  public double getHeadingP() {
    return headingP;
  }

  /**
   * Sets the P coefficient for the heading PI loop. Defaults to 0.
   *
   * @param headingP The P coefficient, in profile units per radian.
   * @return This {@code FollowProfileFactory} in a builder pattern.
   */
  @NotNull
  public FollowProfileFactory setHeadingP(double headingP) {
    this.headingP = headingP;
    return this;
  }


  /**
   * Gets the I coefficient for the heading PI loop. Defaults to 0.
   *
   * @return The currently set I coefficient, in profile units per radian-second.
   */
  public double getHeadingI() {
    return headingI;
  }

  /**
   * Sets the I coefficient for the heading PI loop. Defaults to 0.
   *
   * @param headingI The I coefficient, in profile units per radian-second.
   * @return This {@code FollowProfileFactory} in a builder pattern.
   */
  @NotNull
  public FollowProfileFactory setHeadingI(double headingI) {
    this.headingI = headingI;
    return this;
  }

  /**
   * Creates a new {@link FollowProfile} command to follow the provided profile. This is a
   * convienience method that calls {@link #create(File, File)} with two files as described below.
   * <p>
   * With a given profile name, this method will attempt to load a profile with name "profile" from
   * two files; the left side from /home/lvuser/profiles/profile_left.csv and the right side from
   * /home/lvuser/profiles/profile_right.csv.
   *
   * @param profileName The name of the profile.
   * @return A new {@link FollowProfile} command with the previously configured settings following
   * the provided profile.
   */
  @NotNull
  public FollowProfile create(@NotNull String profileName) {
    return create(new File("/home/lvuser/profiles/" + profileName + "_left.csv"),
        new File("/home/lvuser/profiles/" + profileName + "_right.csv"));
  }

  /**
   * Creates a new {@link FollowProfile} command to follow the provided profile. This is a
   * convinience method that calls {@link #create(MotionProfile, MotionProfile)} after loading the
   * trajectories using {@link Pathfinder#readFromCSV(File)} and converting them using {@link
   * MotionProfileUtils#createProfile(Trajectory)}.
   *
   * @param leftFile The file to load the left profile from; should be a pathfinder-formatted CSV.
   * @param rightFile The file to load the right profile from; should be a pathfinder-formatted
   * CSV.
   * @return A new {@link FollowProfile} command with the previously configured settings following *
   * the provided profile.
   */
  @NotNull
  public FollowProfile create(@NotNull File leftFile, @NotNull File rightFile) {
    return create(MotionProfileUtils.createProfile(Pathfinder.readFromCSV(leftFile)),
        MotionProfileUtils.createProfile(Pathfinder.readFromCSV(rightFile)));
  }

  /**
   * Creates a new {@link FollowProfile} command to follow the provided profile.
   *
   * @param left The profile for the left side.
   * @param right The profile for the right side.
   * @return A new {@link FollowProfile} command with the previously configured settings following *
   * the provided profile.
   */
  @NotNull
  public FollowProfile create(@NotNull MotionProfile left, @NotNull MotionProfile right) {
    return new FollowProfile(left, right, subsystems, leftSetpointConsumer, rightSetpointConsumer,
        headingSupplier, loopFreq, velCoeff, velIntercept, accelCoeff, headingP, headingI);
  }
}
