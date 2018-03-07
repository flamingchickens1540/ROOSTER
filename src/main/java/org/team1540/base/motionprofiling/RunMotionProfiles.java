package org.team1540.base.motionprofiling;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import jaci.pathfinder.Trajectory;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Executes a set of motion profiles (with respective properties.)
 */
public class RunMotionProfiles extends Command {

  private int slotId = 0;
  private Set<MotionProfilingProperties> motionProfiles;
  private Timer timer = new Timer();
  private double lastTime;
  private boolean isFinished = false;

  public RunMotionProfiles(MotionProfilingProperties... properties) {
    realConstructor(new HashSet<>(Arrays.asList(properties)));
  }

  public RunMotionProfiles(Set<MotionProfilingProperties> motionProfiles) {
    realConstructor(motionProfiles);
  }

  private void realConstructor(Set<MotionProfilingProperties> motionProfiles) {
    this.motionProfiles = motionProfiles;
  }

  public int getSlotId() {
    return slotId;
  }

  public void setSlotId(int slotId) {
    this.slotId = slotId;
  }

  public Set<MotionProfilingProperties> getMotionProfiles() {
    return motionProfiles;
  }

  public void setMotionProfiles(
      Set<MotionProfilingProperties> motionProfiles) {
    this.motionProfiles = motionProfiles;
  }

  @Override
  protected void initialize() {
    timer.start();
    lastTime = timer.get();
    isFinished = false;
  }

  @Override
  protected void execute() {
    for (MotionProfilingProperties currentProperty : motionProfiles) {
      // Each controller's setpoint is calculated at a slightly different time, but this doesn't
      // matter, since the motion profile is "continuous."
      double velocity = getVelocitySetpoint(currentProperty, timer.get(), lastTime);
      currentProperty.getSetMotorVelocityFunction().accept(velocity);
    }

    lastTime = timer.get();
  }

  private double getVelocitySetpoint(MotionProfilingProperties currentProperty, double currentTime,
      double lastTime) {

    Trajectory thisTrajectory = currentProperty.getTrajectory();
    double dt = thisTrajectory.segments[0].dt;
    double encoderMultiplier = currentProperty.getEncoderTickRatio();
    double quadraturePosition = currentProperty.getGetEncoderPositionFunction().getAsDouble();

    // Start from the current time and find the closest point.
    int startIndex = Math.toIntExact(Math.round(currentTime / dt));

    int length = thisTrajectory.segments.length;
    int index = startIndex;
    if (startIndex >= length - 1) {
      index = length - 1;
      isFinished = true;
    }
    return thisTrajectory.segments[index].velocity / encoderMultiplier * 0.1;
  }

  /**
   * Sets the velocity to 0 for all properties and sets isFinished to true.
   */
  @Override
  protected void interrupted() {
    for (MotionProfilingProperties currentProperty : motionProfiles) {
      currentProperty.getSetMotorVelocityFunction().accept(0);
    }
    isFinished = true;
  }

  /**
   * Calculate if a number is between (inclusive) two other numbers
   *
   * @param x The target
   * @param x1 Lower bound
   * @param x2 Upper bound
   * @return if x is between x1 and x2
   */
  private boolean isBetween(double x, double x1, double x2) {
    return (x >= x1 && x <= x2 || x >= x2 && x <= x1);
  }

  private double linearInterpolation(double x, double lowX, double lowY, double highX,
      double highY) {
    return (x - lowX) * (highY - lowY) / (highX - lowX) + lowY;
  }

  @Override
  protected boolean isFinished() {
    return isFinished;
  }
}
