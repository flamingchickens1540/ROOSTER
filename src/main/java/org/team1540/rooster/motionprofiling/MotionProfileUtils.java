package org.team1540.rooster.motionprofiling;

import jaci.pathfinder.Trajectory;
import java.util.Arrays;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.team1540.rooster.motionprofiling.MotionProfile.Point;

public class MotionProfileUtils {

  private MotionProfileUtils() {
  }

  /**
   * Creates a ROOSTER {@link MotionProfile} from a Pathfinder {@link Trajectory}.
   *
   * @param trajectory The {@link Trajectory} to convert.
   * @return A {@link MotionProfile} containing the same points. Profile points are copied over, so
   * subsequent changes to the {@link Trajectory} will not affect the produced {@link
   * MotionProfile}.
   */
  @Contract("_ -> new")
  @NotNull
  public static MotionProfile createProfile(@NotNull Trajectory trajectory) {
    return new MotionProfile(Arrays.stream(trajectory.segments).map(
        s -> new Point(s.dt, s.x, s.y, s.position, s.velocity, s.acceleration, s.jerk, s.heading))
        .toArray(Point[]::new));
  }

}
