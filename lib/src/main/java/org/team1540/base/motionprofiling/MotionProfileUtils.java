package org.team1540.base.motionprofiling;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import jaci.pathfinder.Trajectory;
import java.util.Arrays;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.team1540.base.motionprofiling.MotionProfile.Point;

public class MotionProfileUtils {

  private MotionProfileUtils() {
  }

  /**
   * Create a {@link SetpointConsumer} that sets the setpoint of the provided CTRE motor controller
   * with no adjustment to the position setpoint.
   *
   * @param motorController The motor controller.
   * @return A {@code SetpointConsumer} to use for motion profiling.
   */
  public static SetpointConsumer createSetpointConsumer(BaseMotorController motorController) {
    return createSetpointConsumer(motorController, 1);
  }

  /**
   * Create a {@link SetpointConsumer} that sets the setpoint of the provided CTRE motor
   * controller, multiplying the position setpoint by the adjustmant.
   *
   * @param motorController The motor controller.
   * @return A {@code SetpointConsumer} to use for motion profiling.
   */
  public static SetpointConsumer createSetpointConsumer(BaseMotorController motorController,
      double adjustment) {
    return (setpoint, bump) ->
        motorController
            .set(ControlMode.Position, setpoint * adjustment, DemandType.ArbitraryFeedForward,
                bump);
  }

  /**
   * Creates a ROOSTER {@link MotionProfile} from a Pathfinder {@link Trajectory}.
   *
   * @param trajectory The {@link Trajectory} to convert.
   * @return A {@link MotionProfile} containing the same points. Profile points are copied over, so
   * subsequent changes to the {@link Trajectory} will not affect the produced {@link MotionProfile}.
   */
  @Contract("_ -> new")
  @NotNull
  public static MotionProfile createProfile(@NotNull Trajectory trajectory) {
    return new MotionProfile((Point[]) Arrays.stream(trajectory.segments).map(s -> new Point(s.dt, s.x, s.y, s.position, s.velocity, s.acceleration, s.jerk, s.heading)).toArray());
  }

}
