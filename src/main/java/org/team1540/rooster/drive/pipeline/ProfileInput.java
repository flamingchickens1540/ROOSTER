package org.team1540.rooster.drive.pipeline;

import edu.wpi.first.wpilibj.Timer;
import java.util.OptionalDouble;
import org.jetbrains.annotations.NotNull;
import org.team1540.rooster.functional.Input;
import org.team1540.rooster.motionprofiling.MotionProfile;
import org.team1540.rooster.motionprofiling.MotionProfile.Point;

/**
 * Class to get commanded drive values from a set of motion profiles.
 *
 * This class is non-reusable; the first call to {@link #get()} begins a timer to determine where
 * the input is in the profile. To execute multiple profiles, create multiple {@code
 * ProfileInputs}.
 */
public class ProfileInput implements Input<TankDriveData> {

  private MotionProfile left;
  private MotionProfile right;

  private Timer timer = new Timer();

  private boolean finished;

  /**
   * Create a new {@code ProfileInput}.
   *
   * @param left The left-side profile to execute.
   * @param right The right-side profile to execute.
   */
  public ProfileInput(@NotNull MotionProfile left, @NotNull MotionProfile right) {
    this.left = left;
    this.right = right;
  }

  @NotNull
  private Point getCurrentSegment(@NotNull MotionProfile trajectory, double currentTime) {
    // Start from the current time and find the closest point.
    int startIndex = Math.toIntExact(Math.round(currentTime / trajectory.get(0).dt));

    int length = trajectory.size();
    int index = startIndex;
    if (startIndex >= length - 1) {
      index = length - 1;
      finished = true;
    }
    return trajectory.get(index);
  }

  @Override
  public TankDriveData get() {
    if (timer.get() <= 0) {
      timer.start();
    }

    double timeValue = timer.get();

    Point leftPoint = getCurrentSegment(left, timeValue);
    Point rightPoint = getCurrentSegment(right, timeValue);

    return new TankDriveData(
        new DriveData(
            OptionalDouble.of(leftPoint.position),
            OptionalDouble.of(leftPoint.velocity),
            OptionalDouble.of(leftPoint.acceleration),
            OptionalDouble.empty()),
        new DriveData(
            OptionalDouble.of(rightPoint.position),
            OptionalDouble.of(rightPoint.velocity),
            OptionalDouble.of(rightPoint.acceleration),
            OptionalDouble.empty()),
        OptionalDouble.of(leftPoint.heading),
        OptionalDouble.empty());
  }

  /**
   * Returns whether the end of the profile has been reached.
   *
   * @return {@code true} if the end of the profile has been reached, {@code false} otherwise.
   */
  public boolean isFinished() {
    return finished;
  }
}
