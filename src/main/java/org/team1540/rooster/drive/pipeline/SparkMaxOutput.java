package org.team1540.rooster.drive.pipeline;

import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.team1540.rooster.functional.Output;


/**
 * {@link Output} to pass drive commands to REV Spark MAX motor controllers. For output details, see
 * the method documentation for {@link #accept(TankDriveData) accept()}.
 */
public class SparkMaxOutput implements Output<TankDriveData> {

  private CANSparkMax left;
  private CANSparkMax right;
  private boolean closedLoop;

  /**
   * Creates a new {@code SparkMaxOutput}. This is equivalent to calling {@link
   * #SparkMaxOutput(CANSparkMax, CANSparkMax, boolean)} with {@code closedLoop} equal to {@code
   * true}.
   *
   * @param left The left-side motor controller.
   * @param right The right-side motor controller.
   */
  public SparkMaxOutput(@NotNull CANSparkMax left, @NotNull CANSparkMax right) {
    this(left, right, true);
  }

  /**
   * Creates a new {@code SparkMaxOutput}.
   *
   * @param left The left-side motor controller.
   * @param right The right-side motor controller.
   * @param closedLoop Whether to command the controllers in closed-loop mode if possible. (See
   * {@link #isClosedLoop()}/{@link #setClosedLoop(boolean) setClosedLoop()}).
   */
  public SparkMaxOutput(@NotNull CANSparkMax left, @NotNull CANSparkMax right, boolean closedLoop) {
    this.left = left;
    this.right = right;
    this.closedLoop = closedLoop;
  }

  /**
   * Command previously set motors according to the provided {@link TankDriveData}.
   * <p>If closed-loop is enabled ({@link #isClosedLoop()} returns {@code true}), when the provided
   * {@link TankDriveData} has non-empty position or velocity fields, that setpoint will be sent to
   * the motor controllers as a closed-loop setpoint, with any additional feed-forward sent via
   * throttle bump. (Position has priority over velocity when deciding which setpoint to send.) If
   * it is {@code false}, or if the provided {@link TankDriveData} has empty position and velocity
   * fields, {@link DriveData#additionalFeedForward} (or 0 if it is not present) will be passed in
   * as the motor throttle from -1 to 1 inclusive.
   *
   * @param tankDriveData The data to accept.
   */
  @Override
  public void accept(TankDriveData tankDriveData) {
    processSide(tankDriveData.left, left);
    processSide(tankDriveData.right, right);
  }


  private void processSide(DriveData data, CANSparkMax controller) {
    if (data.position.isPresent() && closedLoop) {
      if (data.additionalFeedForward.isPresent()) {
        controller.getPIDController()
            .setReference(data.position.getAsDouble(), ControlType.kPosition, 0,
                data.additionalFeedForward.getAsDouble());
      } else {
        controller.getPIDController()
            .setReference(data.position.getAsDouble(), ControlType.kPosition);
      }
    } else if (data.velocity.isPresent() && closedLoop) {
      if (data.additionalFeedForward.isPresent()) {
        controller.getPIDController()
            .setReference(data.position.getAsDouble(), ControlType.kVelocity, 0,
                data.additionalFeedForward.getAsDouble());
      } else {
        controller.getPIDController()
            .setReference(data.position.getAsDouble(), ControlType.kVelocity);
      }
    } else {
      controller.set(data.additionalFeedForward.orElse(0));
    }
  }


  /**
   * Returns whether this {@code SparkMaxOutput} commands its controllers in closed-loop mode if
   * possible. If {@code true}, when {@link #accept(TankDriveData) accept()} is called and the
   * provided {@link TankDriveData} has non-empty position or velocity fields, that setpoint will be
   * sent to the motor controllers as a closed-loop setpoint, with any additional feed-forward sent
   * via throttle bump (position has priority over velocity when deciding which setpoint to send.)
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
   * Sets whether this {@code SparkMaxOutput} commands its controllers in closed-loop mode if
   * possible. If {@code closedLoop} is {@code true}, when {@link #accept(TankDriveData) accept()}
   * is called and the provided {@link TankDriveData} has non-empty position or velocity fields,
   * that setpoint will be sent to the motor controllers as a closed-loop setpoint, with any
   * additional feed-forward sent via throttle bump. (Position has priority over velocity when
   * deciding which setpoint to send.) If it is {@code false}, or if the provided {@link
   * TankDriveData} has empty position and velocity fields, {@link DriveData#additionalFeedForward}
   * (or 0 if it is not present) will be passed in as the motor throttle from -1 to 1 inclusive.
   *
   * @param closedLoop Whether to use closed-loop, if possible.
   */
  public void setClosedLoop(boolean closedLoop) {
    this.closedLoop = closedLoop;
  }
}
