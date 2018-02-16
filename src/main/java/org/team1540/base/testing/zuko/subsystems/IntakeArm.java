package org.team1540.base.testing.zuko.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import org.team1540.base.testing.zuko.RobotMap;
import org.team1540.base.testing.zuko.commands.JoystickIntakeArm;
import org.team1540.base.wrappers.ChickenTalon;
import org.team1540.base.wrappers.ChickenTalon.TalonControlMode;

public class IntakeArm extends Subsystem {

  private final ChickenTalon armTalon = new ChickenTalon(RobotMap.intakeArm);

  public IntakeArm() {
    armTalon.changeControlMode(TalonControlMode.PercentVbus);
    armTalon.reverseOutput(false);
    armTalon.reverseSensor(false);
  }

  @Override
  protected void initDefaultCommand() {
    setDefaultCommand(new JoystickIntakeArm());
  }

  public void set(double value) {
    armTalon.set(-value);
  }

  public double getCurrent() {
    return armTalon.getOutputCurrent();
  }

}
