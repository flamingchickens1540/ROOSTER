package org.team1540.base.motionprofiling;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;

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
}
