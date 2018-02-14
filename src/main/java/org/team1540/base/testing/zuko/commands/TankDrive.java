package org.team1540.base.testing.zuko.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.base.testing.zuko.OI;
import org.team1540.base.testing.zuko.Robot;

public class TankDrive extends Command {

  public TankDrive() {
    requires(Robot.driveTrain);
  }

  @Override
  protected void execute() {
    Robot.driveTrain
        .tankDrive(OI.getDriveLeftAxis() + OI.getDriveLeftTrigger() - OI.getDriveRightTrigger(),
            OI.getDriveRightAxis() + OI.getDriveLeftTrigger() - OI.getDriveRightTrigger());
  }

  @Override
  protected boolean isFinished() {
    return false;
  }

}
