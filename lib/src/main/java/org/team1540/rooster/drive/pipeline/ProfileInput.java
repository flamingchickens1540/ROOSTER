package org.team1540.rooster.drive.pipeline;

import edu.wpi.first.wpilibj.Timer;
import java.util.OptionalDouble;
import org.jetbrains.annotations.NotNull;
import org.team1540.rooster.motionprofiling.MotionProfile;
import org.team1540.rooster.motionprofiling.MotionProfile.Point;

public class ProfileInput implements
    org.team1540.rooster.drive.pipeline.Input<org.team1540.rooster.drive.pipeline.TankDriveData> {

  private MotionProfile left;
  private MotionProfile right;

  private Timer timer = new Timer();

  public ProfileInput(@NotNull MotionProfile left, @NotNull MotionProfile right) {
    this.left = left;
    this.right = right;
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

  @Override
  public org.team1540.rooster.drive.pipeline.TankDriveData get() {
    if (timer.get() <= 0) {
      timer.start();
    }

    double timeValue = timer.get();

    Point leftPoint = getCurrentSegment(left, timeValue);
    Point rightPoint = getCurrentSegment(right, timeValue);

    return new org.team1540.rooster.drive.pipeline.TankDriveData(
        new org.team1540.rooster.drive.pipeline.DriveData(
            OptionalDouble.of(leftPoint.position),
            OptionalDouble.of(leftPoint.velocity),
            OptionalDouble.of(leftPoint.acceleration),
            OptionalDouble.empty()),
        new org.team1540.rooster.drive.pipeline.DriveData(
            OptionalDouble.of(rightPoint.position),
            OptionalDouble.of(rightPoint.velocity),
            OptionalDouble.of(rightPoint.acceleration),
            OptionalDouble.empty()),
        OptionalDouble.of(leftPoint.heading),
        OptionalDouble.empty());
  }
}
