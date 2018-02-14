package org.team1540.base.testing.zuko.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.base.testing.zuko.OI;
import org.team1540.base.testing.zuko.Robot;
import org.team1540.base.testing.zuko.RobotUtil;

public class JoystickPortcullisArms extends Command {

  public JoystickPortcullisArms() {
    requires(Robot.portcullisArms);
  }

  @Override
  protected void execute() {
    Robot.portcullisArms.set(RobotUtil.deadzone(OI.getPortcullisArmsAxis(), 0.1));
  }

  @Override
  protected boolean isFinished() {
    return false;
  }

}
