package org.team1540.rooster.drive.pipeline;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.IMotorController;
import java.util.Objects;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * {@link Output} to pass drive commands to Talon SRX and Victor SPX motor controllers. For output
 * details, see the method documentation for {@link #accept(TankDriveData) accept()}.
 */
public class CTREOutput implements Output<TankDriveData> {

  private IMotorController left;
  private IMotorController right;
  private boolean closedLoop;

  /**
   * Command previously set motors according to the provided {@link TankDriveData}.
   * <p>If closed-loop is enabled ({@link #isClosedLoop()} returns {@code true}), when the provided
   * {@link TankDriveData} has non-empty position or velocity fields, that setpoint will be sent to
   * the motor controllers as a closed-loop setpoint, with any additional feed-forward sent via
   * throttle bump. (Position has priority over velocity when deciding which setpoint to send.) If
   * it is {@code false}, or if the provided {@link TankDriveData} has empty position and velocity
   * fields, {@link DriveData#additionalFeedForward} (or 0 if it is not present) will be passed in
   * as the motor throttle from -1 to 1 inclusive.
   * <p>Note that if the filled state (i.e. whether the optional actually contains a value) of the
   * position/velocity fields must be the same between the left and right side.
   *
   * @param tankDriveData The data to accept.
   */
  @SuppressWarnings("OptionalGetWithoutIsPresent")
  @Override
  @Contract(pure = true)
  public void accept(@NotNull TankDriveData tankDriveData) {
    if (tankDriveData.left.position.isPresent() && closedLoop) {
      if (tankDriveData.left.additionalFeedForward.isPresent()) {
        left.set(ControlMode.Position, tankDriveData.left.position.getAsDouble(),
            DemandType.ArbitraryFeedForward,
            tankDriveData.left.additionalFeedForward.getAsDouble());
        right.set(ControlMode.Position, tankDriveData.right.position.getAsDouble(),
            DemandType.ArbitraryFeedForward,
            tankDriveData.right.additionalFeedForward.getAsDouble());
      } else {
        left.set(ControlMode.Position, tankDriveData.left.position.getAsDouble());
        right.set(ControlMode.Position, tankDriveData.right.position.getAsDouble());
      }
    } else if (tankDriveData.left.velocity.isPresent() && closedLoop) {
      if (tankDriveData.left.additionalFeedForward.isPresent()) {
        left.set(ControlMode.Velocity, tankDriveData.left.velocity.getAsDouble(),
            DemandType.ArbitraryFeedForward,
            tankDriveData.left.additionalFeedForward.getAsDouble());
        right.set(ControlMode.Velocity, tankDriveData.right.velocity.getAsDouble(),
            DemandType.ArbitraryFeedForward,
            tankDriveData.right.additionalFeedForward.getAsDouble());
      } else {
        left.set(ControlMode.Velocity, tankDriveData.left.velocity.getAsDouble());
        right.set(ControlMode.Velocity, tankDriveData.right.velocity.getAsDouble());
      }
    } else {
      left.set(ControlMode.PercentOutput, tankDriveData.left.additionalFeedForward.orElse(0));
      right.set(ControlMode.PercentOutput, tankDriveData.right.additionalFeedForward.orElse(0));
    }
  }

  /**
   * Returns whether this {@code CTREOutput} commands its controllers in closed-loop mode if
   * possible. If {@code true}, when {@link #accept(TankDriveData) accept()} is called and the
   * provided {@link TankDriveData} has non-empty position or velocity fields, that setpoint will be
   * sent to the motor controllers as a closed-loop setpoint, with any additional feed-forward sent
   * via throttle bump. (Position has priority over velocity when deciding which setpoint to send.)
   * If {@code false}, or if the provided {@link TankDriveData} has empty position and velocity
   * fields, {@link DriveData#additionalFeedForward} (or 0 if it is not present) will be passed in
   * as the motor throttle from -1 to 1 inclusive.
   *
   * @return {@code true} if closed-loop control will be used, {@code false} otherwise.
   */
  @Contract(pure = true)
  public boolean isClosedLoop() {
    return closedLoop;
  }

  /**
   * Sets whether this {@code CTREOutput} commands its controllers in closed-loop mode if possible.
   * If {@code closedLoop} is {@code true}, when {@link #accept(TankDriveData) accept()} is called
   * and the provided {@link TankDriveData} has non-empty position or velocity fields, that setpoint
   * will be sent to the motor controllers as a closed-loop setpoint, with any additional
   * feed-forward sent via throttle bump. (Position has priority over velocity when deciding which
   * setpoint to send.) If it is {@code false}, or if the provided {@link TankDriveData} has empty
   * position and velocity fields, {@link DriveData#additionalFeedForward} (or 0 if it is not
   * present) will be passed in as the motor throttle from -1 to 1 inclusive.
   *
   * @param closedLoop Whether to use closed-loop, if possible.
   */
  public void setClosedLoop(boolean closedLoop) {
    this.closedLoop = closedLoop;
  }

  /**
   * Creates a new {@code CTREOutput}. This is equivalent to calling {@link
   * #CTREOutput(IMotorController, IMotorController, boolean)} with {@code closedLoop} equal to
   * {@code true}.
   *
   * @param left The left-side motor controller.
   * @param right The right-side motor controller.
   */
  public CTREOutput(@NotNull IMotorController left, @NotNull IMotorController right) {
    this(left, right, true);
  }

  /**
   * Creates a new {@code CTREOutput}.
   *
   * @param left The left-side motor controller.
   * @param right The right-side motor controller.
   * @param closedLoop Whether to command the controllers in closed-loop mode if possible. (See
   * {@link #isClosedLoop()}/{@link #setClosedLoop(boolean) setClosedLoop()}).
   */
  public CTREOutput(@NotNull IMotorController left, @NotNull IMotorController right,
      boolean closedLoop) {
    this.left = Objects.requireNonNull(left);
    this.right = Objects.requireNonNull(right);
    this.closedLoop = closedLoop;
  }
}
