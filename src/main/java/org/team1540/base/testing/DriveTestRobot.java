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
  Solenoid rightPneu = new Solenoid(2);

  @Override
  public void robotInit() {
    lMaster = new ChickenTalon(1);
    lMaster.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
    lMaster.setBrake(true);
    lMaster.configOpenloopRamp(0);
    lMaster.configClosedloopRamp(0);

    lSlave1 = new ChickenTalon(2);
    lSlave1.set(ControlMode.Follower, lMaster.getDeviceID());
    lSlave1.configOpenloopRamp(0);
    lSlave1.configClosedloopRamp(0);
    lSlave1.setBrake(true);

    lSlave2 = new ChickenTalon(3);
    lSlave2.set(ControlMode.Follower, lMaster.getDeviceID());
    lSlave2.configClosedloopRamp(0);
    lSlave2.configOpenloopRamp(0);
    lSlave2.setBrake(true);


    rMaster = new ChickenTalon(4);
    rMaster.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
    rMaster.setBrake(true);
    rMaster.configOpenloopRamp(0);
    rMaster.configClosedloopRamp(0);

    rSlave1 = new ChickenTalon(5);
    rSlave1.set(ControlMode.Follower, rMaster.getDeviceID());
    rSlave1.configOpenloopRamp(0);
    rSlave1.configClosedloopRamp(0);
    rSlave1.setBrake(true);

    rSlave2 = new ChickenTalon(6);
    rSlave2.set(ControlMode.Follower, rMaster.getDeviceID());
    rSlave2.configClosedloopRamp(0);
    rSlave2.configOpenloopRamp(0);
    rSlave2.setBrake(true);

    SmartDashboard.putBoolean("Shifters", true);

    joystick = new Joystick(0);
    SmartDashboard.putData("Compressor", compressor);
  }

  @Override
  public void robotPeriodic() {
    SmartDashboard.putNumber("Left", lMaster.getSelectedSensorPosition());
    SmartDashboard.putNumber("Right", rMaster.getSelectedSensorPosition());
    SmartDashboard.putNumber("LeftVel", lMaster.getSelectedSensorVelocity());
    SmartDashboard.putNumber("RightVel", rMaster.getSelectedSensorVelocity());
    SmartDashboard.putNumber("LeftVelRaw", lMaster.getQuadratureVelocity());
    SmartDashboard.putNumber("RightVelRaw", rMaster.getQuadratureVelocity());
  }

  @Override
  public void teleopPeriodic() {
    leftPneu.set(SmartDashboard.getBoolean("Shifters", false));
    rightPneu.set(!SmartDashboard.getBoolean("Shifters", false));
    lMaster.set(ControlMode.PercentOutput, joystick.getRawAxis(5));
    rMaster.set(ControlMode.PercentOutput, -joystick.getRawAxis(1));
  }
}
