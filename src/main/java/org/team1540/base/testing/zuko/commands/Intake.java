package org.team1540.base.testing.zuko.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.TimedCommand;
import org.team1540.base.testing.zuko.Robot;

public class Intake extends CommandGroup {

  private double startTime;

  public Intake() {
    requires(Robot.intakeArm);
    requires(Robot.intakeRollers);
    requires(Robot.shooter);

    addParallel(new Command() {

      @Override
      protected void initialize() {
        Robot.shooter.set(-Robot.tuning.getFlywheelBackwardsValue());
        Robot.intakeRollers.set(Robot.tuning.getIntakeRollersValue());
        startTime = System.currentTimeMillis();
      }

      @Override
      protected void end() {
        Robot.shooter.stop();
        Robot.intakeRollers.stop();
      }

      @Override
      protected boolean isFinished() {
        return (System.currentTimeMillis() - startTime > 1000) &&
            Robot.shooter.getCurrent() > Robot.tuning.getFlywheelBackwardsCurrentThreshold();
      }

    });

    addParallel(new TimedCommand(1) {

      @Override
      protected void initialize() {
        Robot.intakeArm.set(Robot.tuning.getIntakeArmValue());
      }

      @Override
      protected void end() {
        Robot.intakeArm.set(0);
      }

    });

  }

}
