package org.team1540.base.testing.zuko.commands;

import edu.wpi.first.wpilibj.command.InstantCommand;
import org.team1540.base.testing.zuko.Robot;

public class CancelShooter extends InstantCommand {

  public CancelShooter() {
    requires(Robot.shooter);
  }

  @Override
  protected void initialize() {
    Robot.shooter.stop();
    System.out.println("hi");
  }

}
