package org.team1540.base.testing;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team1540.base.wrappers.ChickenTalon;

public class DriveTestRobot extends IterativeRobot {
  Compressor compressor = new Compressor();
  Joystick joystick;
  ChickenTalon lMaster;
  ChickenTalon lSlave1;
  ChickenTalon lSlave2;
  Solenoid leftPneu = new Solenoid(0);
  ChickenTalon rMaster;
  ChickenTalon rSlave1;
  ChickenTalon rSlave2;
  Solenoid rightPneu = new Solenoid(1);

  @Override
  public void robotInit() {
    lMaster = new ChickenTalon(1);
    lMaster.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
    lSlave1 = new ChickenTalon(2);
    lSlave1.set(ControlMode.Follower, lMaster.getDeviceID());
    lSlave2 = new ChickenTalon(3);
    lSlave2.set(ControlMode.Follower, lMaster.getDeviceID());

    rMaster = new ChickenTalon(4);
    rMaster.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
    rSlave1 = new ChickenTalon(5);
    rSlave1.set(ControlMode.Follower, rMaster.getDeviceID());
    rSlave2 = new ChickenTalon(6);
    rSlave2.set(ControlMode.Follower, rMaster.getDeviceID());

    joystick = new Joystick(0);
    SmartDashboard.putData("Compressor", compressor);
  }

  @Override
  public void robotPeriodic() {
    SmartDashboard.putNumber("Left", lMaster.getSelectedSensorPosition());
    SmartDashboard.putNumber("Right", rMaster.getSelectedSensorPosition());
    SmartDashboard.putNumber("LeftVel", lMaster.getSelectedSensorVelocity());
    SmartDashboard.putNumber("RightVel", rMaster.getSelectedSensorVelocity());
    leftPneu.set(true);
    rightPneu.set(false);
  }

  @Override
  public void teleopPeriodic() {
    lMaster.set(joystick.getRawAxis(1));
    rMaster.set(joystick.getRawAxis(5));
  }
}
