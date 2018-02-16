package org.team1540.base.testing.zuko.subsystems;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.team1540.base.testing.zuko.Robot;
import org.team1540.base.testing.zuko.RobotMap;
import org.team1540.base.wrappers.ChickenTalon;
import org.team1540.base.wrappers.ChickenTalon.TalonControlMode;

public class Shooter extends Subsystem {

  private final ChickenTalon flywheelLeftTalon = new ChickenTalon(RobotMap.flywheelL);
  private final ChickenTalon flywheelRightTalon = new ChickenTalon(RobotMap.flywheelR);

  public Shooter() {
    flywheelLeftTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
    flywheelLeftTalon.reverseSensor(false);
    flywheelLeftTalon.reverseOutput(false);
    flywheelLeftTalon.configNominalOutputVoltage(+0f, -0f);
    flywheelLeftTalon.configPeakOutputVoltage(+12f, -12f);
    flywheelLeftTalon.configEncoderCodesPerRev(1024);
//        flywheelLeftTalon.changeControlMode(TalonControlMode.Speed);
    flywheelLeftTalon.changeControlMode(TalonControlMode.PercentVbus);
    flywheelRightTalon.reverseSensor(false);
    flywheelRightTalon.reverseOutput(false);
    flywheelRightTalon.changeControlMode(TalonControlMode.Follower);
    flywheelRightTalon.set(flywheelLeftTalon.getDeviceID());
  }

  @Override
  protected void initDefaultCommand() {

  }

  public void set(double value) {
//		flywheelLeftTalon.changeControlMode(TalonControlMode.Speed);
    flywheelLeftTalon.changeControlMode(TalonControlMode.PercentVbus);
    flywheelRightTalon.changeControlMode(TalonControlMode.Follower);
    flywheelRightTalon.set(flywheelLeftTalon.getDeviceID());
    flywheelLeftTalon.set(value);

  }

  public void stop() {
    flywheelLeftTalon.changeControlMode(TalonControlMode.PercentVbus);
    flywheelRightTalon.changeControlMode(TalonControlMode.PercentVbus);
    flywheelLeftTalon.set(0);
    flywheelRightTalon.set(0);
  }

  public double getSetpoint() {
//		return flywheelLeftTalon.getSetpoint();
    return 0;
  }

  public double getSpeed() {
//		return flywheelLeftTalon.getSpeed();
    return flywheelLeftTalon.getEncVelocity();
  }

  public void setSpeed(double rpm) {
//		flywheelLeftTalon.changeControlMode(TalonControlMode.Speed);
    flywheelLeftTalon.changeControlMode(TalonControlMode.PercentVbus);
    flywheelRightTalon.changeControlMode(TalonControlMode.Follower);
    flywheelRightTalon.set(flywheelLeftTalon.getDeviceID());
    flywheelLeftTalon.setSetpoint(rpm);
  }

  public double getCurrent() {
    return flywheelLeftTalon.getOutputCurrent();
  }

  public boolean upToSpeed(double rpm) {
    return Math.abs(rpm - getSpeed()) < Robot.tuning.getFlywheelSpeedMarginOfError();
  }

}
