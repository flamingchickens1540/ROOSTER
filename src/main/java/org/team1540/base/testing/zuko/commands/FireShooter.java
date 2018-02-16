package org.team1540.base.testing.zuko.commands;

import edu.wpi.first.wpilibj.command.TimedCommand;
import org.team1540.base.testing.zuko.Robot;

public class FireShooter extends TimedCommand {

  public FireShooter() {
    super(1);
    requires(Robot.intakeRollers);
    requires(Robot.shooter);
  }

  @Override
  protected void initialize() {
    Robot.intakeRollers.setOnlySecondRoller(Robot.tuning.getIntakeRollersValue());
  }

  @Override
  protected void end() {
    Robot.intakeRollers.stop();
    Robot.shooter.stop();
  }

}
