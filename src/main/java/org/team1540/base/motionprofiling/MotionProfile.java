package org.team1540.base.motionprofiling;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import jaci.pathfinder.Trajectory;
import java.util.Set;

public class MotionProfile extends Command {

  private int slotId = 0;
  private Set<MotionProfilingProperties> motionProfiles;
  private Timer timer = new Timer();
  private double lastTime;
  private boolean isFinished = false;

  public MotionProfile(Set<MotionProfilingProperties> motionProfiles) {
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

  protected void initialize() {
    timer.start();
    lastTime = timer.get();
    isFinished = false;
  }

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

    /*
      Whoa! This is weird. Although everything is ordered base on time, that's prone to getting off
      and just never correcting.
      However, positions can collide. Thus, we search forward in time from the last point we
      calculated until we get a position we like.

      Note that there's no guessing what the velocity setpoint should be based on how long it took
      the last loop to complete–that is, if your jerk is high, you might have some issues with the
      setpoint being a little wonky, as though it's lagging. Otherwise you shouldn't really notice.
    */

    Trajectory thisTrajectory = currentProperty.getTrajectory();
    double dt = thisTrajectory.segments[0].dt;
    double encoderMultiplier = currentProperty.getEncoderTickRatio();
    double quadraturePosition = currentProperty.getGetEncoderPositionFunction().getAsDouble();

    // Start from the current time and find the closest point.
    int startIndex = Math.toIntExact(Math.round(currentTime / dt));

    // Very simple but reliable implementation commented out.
    int length = thisTrajectory.segments.length;
    int index = (startIndex < length) ? startIndex : length - 1;
    return thisTrajectory.segments[index].velocity / encoderMultiplier * 0.1;
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
