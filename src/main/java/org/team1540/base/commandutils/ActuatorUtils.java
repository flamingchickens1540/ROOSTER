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
   * The returned command functions by a fairly simple method which may be improved in later
   * library releases. Every tick (which occur every 20ms or so) it adds the speed to the position
   * that it's commanding the PID to to go to. This results in a smooth movement as long as speed
   * is kept relatively low (a low fraction of the motor's maximum speed) so that the change in
   * commanded position does not outstrip the change in actual position.
   *
   * @param actuator The actuator to move.
   * @param endPos The position to move the actuator to, in whatever the actuator's units are.
   * @param tolerance The error (between the requested end position and the actuator's reported
   *     position) below which the command will end.
   * @param speed The speed that the actuator should move at, in your actuator's units per 20ms
   *     (roughly).
   *
   * @return A {@link Command} that moves the provided {@link Actuator} to {@code endPos} at {@code
   *     speed} per tick and stopping when the error is less than {@code tolerance}.
   *
   * @see Actuator
   * @see Command
   */
  public static Command createMoveToPositionCommand(Actuator actuator, double endPos, double tolerance, double speed) {
    return new MoveToPositionCommand(actuator, endPos, tolerance, speed);
  }

  private static class MoveToPositionCommand extends Command {
    final Actuator actuator;
    final double endPos;
    final double speed;
    final double tolerance;
    double commandedPos;

    private MoveToPositionCommand(Actuator actuator, double endPos, double tolerance, double speed) {
      super("Move " + actuator.getSubsystem().getName() + " to " + endPos + " at " + speed);
      requires(actuator.getSubsystem());
      this.actuator = actuator;
      this.endPos = endPos;
      this.tolerance = tolerance;
      this.speed = speed;
    }

    @Override
    protected void initialize() {
      commandedPos = actuator.getPosition();
    }

    @Override
    protected void execute() {
      actuator.setPosition(commandedPos += signum(endPos - commandedPos) * speed);
    }

    @Override
    protected boolean isFinished() {
      return abs(actuator.getPosition() - endPos) < tolerance;
    }
  }
}
