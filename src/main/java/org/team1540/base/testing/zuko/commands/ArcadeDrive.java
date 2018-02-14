package org.team1540.base.testing.zuko.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.base.testing.zuko.OI;
import org.team1540.base.testing.zuko.Robot;

public class ArcadeDrive extends Command {

  public ArcadeDrive() {
    requires(Robot.driveTrain);
  }

  @Override
  protected void execute() {
    Robot.driveTrain.setLeft(OI.getDriveAccAxis() - OI.getDriveTurnAxis());
    Robot.driveTrain.setRight(OI.getDriveAccAxis() + OI.getDriveTurnAxis());
  }

  @Override
  protected boolean isFinished() {
    return false;
  }

}
