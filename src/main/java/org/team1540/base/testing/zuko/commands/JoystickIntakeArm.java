package org.team1540.base.testing.zuko.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.base.testing.zuko.OI;
import org.team1540.base.testing.zuko.Robot;
import org.team1540.base.testing.zuko.RobotUtil;

public class JoystickIntakeArm extends Command {

  public JoystickIntakeArm() {
    requires(Robot.intakeArm);
  }

  @Override
  protected void execute() {
    Robot.intakeArm.set(RobotUtil.deadzone(OI.getIntakeArmAxis(), 0.1));
  }

  @Override
  protected boolean isFinished() {
    return false;
  }

}
