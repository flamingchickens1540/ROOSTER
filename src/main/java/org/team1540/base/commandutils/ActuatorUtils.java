package org.team1540.base.commandutils;

import static java.lang.Math.abs;
import static java.lang.Math.signum;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.base.templates.Actuator;

/**
 * Static utility functions that create commands relating to {@link Actuator Actuators}.
 *
 * @see Actuator
 */
public class ActuatorUtils {
  /**
   * Creates a command that moves the actuator to a position.
   * <p>
   * The returned command functions by a fairly simple method. Every tick (which occur every 20ms or
   * so) it adds the speed to the position that it's commanding the PID to to go to. This results in
   * a smooth movement as long as speed is kept relatively low (a low fraction of the motor's
   * maximum speed) so that the change in commanded position does not outstrip the change in actual
   * position.
   *
   * @param actuator The actuator to move.
   * @param endPos The position to move the actuator to, in whatever the actuator's units are.
   * @param tolerance The error (between the requested end position and the actuator's reported
   *     position) below which the command will end.
   * @param speed The speed that the actuator should move at, in your actuator's units per 20ms
   *     (roughly).
   * @param currentLimit The maximum current above which the arm will stop moving temporarily,
   *     in amps.
   * @param minConsecutiveSpikeTicks The minimum ticks with current above {@code currentLimit}
   *     before the actuator will stop moving. This results in a minimum spike duration of {@code
   *     minConsecutiveSpikeTicks} * 20.
   *
   * @return A {@link Command} that moves the provided {@link Actuator} to {@code endPos} at {@code
   *     speed} per tick and stopping when the error is less than {@code tolerance}.
   *
   * @see Actuator
   * @see Command
   */
  public static Command createMoveToPositionCommand(Actuator actuator, double endPos,
      double tolerance, double speed, double currentLimit, int minConsecutiveSpikeTicks) {
    return new MoveToPositionCommand(actuator, endPos, tolerance, speed, currentLimit, minConsecutiveSpikeTicks);
  }

  private static class MoveToPositionCommand extends Command {
    final Actuator actuator;
    final double currentLimit;
    final double endPos;
    final int minConsecutiveSpikeTicks;
    final double speed;
    final double tolerance;
    double commandedPos;
    int consecutiveSpikeTicks;

    private MoveToPositionCommand(Actuator actuator, double endPos, double tolerance, double speed,
        double currentLimit, int minConsecutiveSpikeTicks) {
      super("Move " + actuator.getSubsystem().getName() + " to " + endPos + " at " + speed);
      requires(actuator.getSubsystem());

      this.actuator = actuator;
      this.endPos = endPos;
      this.tolerance = tolerance;
      this.speed = speed;
      this.minConsecutiveSpikeTicks = minConsecutiveSpikeTicks;
      this.currentLimit = currentLimit;
    }

    @Override
    protected void initialize() {
      commandedPos = actuator.getPosition();
      consecutiveSpikeTicks = 0;
    }

    @Override
    protected void execute() {
      if (actuator.getCurrent() > currentLimit) {
        // current is spiking
        consecutiveSpikeTicks++;
      } else {
        consecutiveSpikeTicks = 0;
      }

      if (consecutiveSpikeTicks > minConsecutiveSpikeTicks) {
        // current has been spiking long enough to stop moving
        commandedPos = actuator.getPosition();
      } else {
        commandedPos += signum(endPos - commandedPos) * speed;
      }
      actuator.setPosition(commandedPos);
    }

    @Override
    protected boolean isFinished() {
      return abs(actuator.getPosition() - endPos) < tolerance;
    }
  }
}
