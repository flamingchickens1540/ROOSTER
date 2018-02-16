package org.team1540.base.testing.zuko.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.base.testing.zuko.Robot;

public class Eject extends Command {

  public Eject() {
    requires(Robot.shooter);
    requires(Robot.intakeRollers);
  }

  @Override
  protected void initialize() {
    Robot.shooter.set(-Robot.tuning.getFlywheelBackwardsValue());
    Robot.intakeRollers.set(-Robot.tuning.getIntakeRollersValue());
  }

  @Override
  protected void end() {
    Robot.shooter.stop();
    Robot.intakeRollers.stop();
  }

  @Override
  protected boolean isFinished() {
    return false;
  }

}
