package org.team1540.base.testing.zuko.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import org.team1540.base.testing.zuko.RobotMap;
import org.team1540.base.wrappers.ChickenTalon;
import org.team1540.base.wrappers.ChickenTalon.TalonControlMode;

public class IntakeRollers extends Subsystem {

  private final ChickenTalon armRollerTalon = new ChickenTalon(RobotMap.intakeArmRollers);
  private final ChickenTalon rollersTalon = new ChickenTalon(RobotMap.intakeRollers);

  public IntakeRollers() {
//		armRollerTalon.reverseSensor(false);
//		armRollerTalon.reverseOutput(false);
//		rollersTalon.reverseSensor(false);
//		rollersTalon.reverseOutput(true);
    armRollerTalon.changeControlMode(TalonControlMode.PercentVbus);
    rollersTalon.changeControlMode(TalonControlMode.PercentVbus);
  }

  @Override
  protected void initDefaultCommand() {

  }

  public void set(double value) {
    armRollerTalon.set(-value);
    rollersTalon.set(-value);
  }

  public void setOnlySecondRoller(double value) {
    rollersTalon.set(-value);
  }

  public void stop() {
    set(0);
  }

}
