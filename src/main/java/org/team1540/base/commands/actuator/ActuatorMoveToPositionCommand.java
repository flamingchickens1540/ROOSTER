package org.team1540.base.commands.actuator;

import static java.lang.Math.abs;
import static java.lang.Math.signum;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.base.templates.Actuator;

class ActuatorMoveToPositionCommand extends Command {
  final Actuator actuator;
  final double currentLimit;
  final double endPos;
  final int minConsecutiveSpikeTicks;
  final double speed;
  final double tolerance;
  double commandedPos;
  int consecutiveSpikeTicks;

  ActuatorMoveToPositionCommand(Actuator actuator, double endPos, double tolerance, double speed,
      double currentLimit, int minConsecutiveSpikeTicks) {
    super("Move " + actuator.getAttachedSubsystem().getName() + " to " + endPos + " at " + speed);
    requires(actuator.getAttachedSubsystem());

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
