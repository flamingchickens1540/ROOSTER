package org.team1540.base.testing.zuko.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import org.team1540.base.testing.zuko.RobotMap;
import org.team1540.base.testing.zuko.commands.JoystickPortcullisArms;
import org.team1540.base.wrappers.ChickenTalon;
import org.team1540.base.wrappers.ChickenTalon.TalonControlMode;

public class PortcullisArms extends Subsystem {

  ChickenTalon leftArmTalon = new ChickenTalon(RobotMap.portcullisL);
  ChickenTalon rightArmTalon = new ChickenTalon(RobotMap.portcullisR);

  public PortcullisArms() {
    leftArmTalon.changeControlMode(TalonControlMode.PercentVbus);
    rightArmTalon.changeControlMode(TalonControlMode.PercentVbus);
    leftArmTalon.reverseOutput(false);
    rightArmTalon.reverseOutput(true);
  }

  public void set(double value) {
    leftArmTalon.set(-value);
    rightArmTalon.set(value);
  }

  @Override
  protected void initDefaultCommand() {
    setDefaultCommand(new JoystickPortcullisArms());
  }

}
