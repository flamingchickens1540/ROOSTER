package org.team1540.base.testing.zuko.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team1540.base.power.PowerManageable;
import org.team1540.base.power.PowerManager;
import org.team1540.base.testing.zuko.Robot;
import org.team1540.base.testing.zuko.RobotMap;
import org.team1540.base.testing.zuko.RobotUtil;
import org.team1540.base.wrappers.ChickenTalon;
import org.team1540.base.wrappers.ChickenTalon.TalonControlMode;

public class DriveTrain extends Subsystem implements PowerManageable {

  private final ChickenTalon driveLeftTalon = new ChickenTalon(RobotMap.driveLeftA);
  private final ChickenTalon driveLeftBTalon = new ChickenTalon(RobotMap.driveLeftB);
  private final ChickenTalon driveLeftCTalon = new ChickenTalon(RobotMap.driveLeftC);

  private final ChickenTalon driveRightTalon = new ChickenTalon(RobotMap.driveRightA);
  private final ChickenTalon driveRightBTalon = new ChickenTalon(RobotMap.driveRightB);
  private final ChickenTalon driveRightCTalon = new ChickenTalon(RobotMap.driveRightC);

  public DriveTrain() {
    driveRightTalon.changeControlMode(TalonControlMode.PercentVbus);
    driveRightBTalon.changeControlMode(TalonControlMode.Follower);
    driveRightCTalon.changeControlMode(TalonControlMode.Follower);
    driveLeftTalon.changeControlMode(TalonControlMode.PercentVbus);
    driveLeftBTalon.changeControlMode(TalonControlMode.Follower);
    driveLeftCTalon.changeControlMode(TalonControlMode.Follower);
    driveRightTalon.reverseOutput(false);
    driveLeftTalon.reverseOutput(false);
    driveRightTalon.reverseSensor(true);
    driveLeftTalon.reverseSensor(true);
    driveRightBTalon.set(driveRightTalon.getDeviceID());
    driveRightCTalon.set(driveRightTalon.getDeviceID());
    driveLeftBTalon.set(driveLeftTalon.getDeviceID());
    driveLeftCTalon.set(driveLeftTalon.getDeviceID());
    PowerManager.getInstance().registerPowerManageable(this);
  }

  @Override
  protected void initDefaultCommand() {
    actuallyInitDefaultCommand();
  }

  public void actuallyInitDefaultCommand() {
    setDefaultCommand(Robot.driveModeChooser.getSelected());
  }

  public void tankDrive(double leftValue, double rightValue) {
    double deadzone = 0.1;
    driveLeftTalon.set(ControlMode.PercentOutput, -RobotUtil.deadzone(leftValue, deadzone));
    driveRightTalon.set(ControlMode.PercentOutput, RobotUtil.deadzone(rightValue, deadzone));
  }

  public void setLeft(double value) {
    driveLeftTalon.set(-value);
  }

  public void setRight(double value) {
    driveRightTalon.set(value);
  }

  public double getDriveLeftTalonVelocity() {
    return driveLeftTalon.getQuadratureVelocity();
  }

  public double getDriveRightTalonVelocity() {
    return driveRightTalon.getQuadratureVelocity();
  }

  public double getDriveLeftTalonPosition() {
    return driveLeftTalon.getQuadraturePosition();
  }

  public double getDriveRightTalonPosition() {
    return driveRightTalon.getQuadraturePosition();
  }

  public void setLeftVelocity(double velocity) {
    driveLeftTalon.set(ControlMode.Velocity, velocity);
  }

  public void setRightVelocity(double velocity) {
    driveRightTalon.set(ControlMode.Velocity, velocity);
  }

  public void prepareForMotionProfiling() {
    driveRightTalon.setControlMode(ControlMode.Velocity);
    driveRightBTalon.setControlMode(ControlMode.Follower);
    driveRightCTalon.setControlMode(ControlMode.Follower);
    driveLeftTalon.setControlMode(ControlMode.Velocity);
    driveLeftBTalon.setControlMode(ControlMode.Follower);
    driveLeftCTalon.setControlMode(ControlMode.Follower);
    driveRightBTalon.set(driveRightTalon.getDeviceID());
    driveRightCTalon.set(driveRightTalon.getDeviceID());
    driveLeftBTalon.set(driveLeftTalon.getDeviceID());
    driveLeftCTalon.set(driveLeftTalon.getDeviceID());

    driveRightTalon.setInverted(true);
    driveRightBTalon.setInverted(true);
    driveRightCTalon.setInverted(true);
    driveLeftTalon.setInverted(false);
    driveLeftBTalon.setInverted(false);
    driveLeftCTalon.setInverted(false);
    driveRightTalon.setSensorPhase(false);
    driveLeftTalon.setSensorPhase(true);

    // This needs to be here, as PIDFiZone values are stored in memory
    driveLeftTalon.config_IntegralZone(driveLeftTalon.getDefaultPidIdx(), 1000);
    driveRightTalon.config_IntegralZone(driveRightTalon.getDefaultPidIdx(), 1000);
    driveLeftTalon.config_kI(driveLeftTalon.getDefaultPidIdx(), 0.01);
    driveRightTalon.config_kI(driveRightTalon.getDefaultPidIdx(), 0.01);
    driveLeftTalon.config_kF(driveLeftTalon.getDefaultPidIdx(), 0.1);
    driveRightTalon.config_kF(driveRightTalon.getDefaultPidIdx(), 0.1);
    driveLeftTalon.configClosedloopRamp(0);
    driveLeftBTalon.configClosedloopRamp(0);
    driveLeftCTalon.configClosedloopRamp(0);
    driveRightTalon.configClosedloopRamp(0);
    driveRightBTalon.configClosedloopRamp(0);
    driveRightCTalon.configClosedloopRamp(0);

    driveLeftTalon.setSelectedSensorPosition(0);
    driveRightTalon.setSelectedSensorPosition(0);
  }

  public void displayAutoInfo() {
    SmartDashboard
        .putNumber("lSetpoint", driveLeftTalon.getClosedLoopTarget(0));
    SmartDashboard
        .putNumber("rSetpoint", driveRightTalon.getClosedLoopTarget(0));
    SmartDashboard
        .putNumber("lOutput", driveLeftTalon.getMotorOutputPercent());
    SmartDashboard
        .putNumber("rOutput", driveRightTalon.getMotorOutputPercent());
    SmartDashboard.putNumber("lVelocity", driveLeftTalon.getQuadratureVelocity());
    SmartDashboard.putNumber("rVelocity", driveRightTalon.getQuadratureVelocity());
//    SmartDashboard.putBoolean("lMotorPhase", driveLeftTalon.getInverted());
//    SmartDashboard.putBoolean("rMotorPhase", driveRightTalon.getInverted());
  }

  public void displayGeneralInfo() {
    SmartDashboard.putBoolean("isVoltageDipping", PowerManager.getInstance().isVoltageDipping());
    SmartDashboard.putBoolean("isLimiting", PowerManager.getInstance().isLimiting());
  }

  @Override
  public double getPriority() {
    return 10;
  }

  @Override
  public void setPriority(double priority) {

  }

  @Override
  public double getVoltage() {
    return driveLeftTalon.getMotorOutputVoltage() + driveLeftBTalon.getMotorOutputVoltage() +
        driveLeftCTalon.getMotorOutputVoltage() + driveRightTalon.getMotorOutputVoltage() +
        driveRightBTalon.getMotorOutputVoltage() + driveRightCTalon.getMotorOutputVoltage();
  }

  @Override
  public void setLimit(double limit) {
    double realLimit = limit / 6;
    SmartDashboard.putNumber("realLimit", realLimit);
    driveLeftTalon.configPeakOutputForward(Math.toIntExact(Math.round(realLimit)));
    driveLeftBTalon.configPeakOutputForward(Math.toIntExact(Math.round(realLimit)));
    driveLeftCTalon.configPeakOutputForward(Math.toIntExact(Math.round(realLimit)));
    driveRightTalon.configPeakOutputForward(Math.toIntExact(Math.round(realLimit)));
    driveRightBTalon.configPeakOutputForward(Math.toIntExact(Math.round(realLimit)));
    driveRightCTalon.configPeakOutputForward(Math.toIntExact(Math.round(realLimit)));
    driveLeftTalon.configPeakOutputReverse(Math.toIntExact(Math.round(realLimit)));
    driveLeftBTalon.configPeakOutputReverse(Math.toIntExact(Math.round(realLimit)));
    driveLeftCTalon.configPeakOutputReverse(Math.toIntExact(Math.round(realLimit)));
    driveRightTalon.configPeakOutputReverse(Math.toIntExact(Math.round(realLimit)));
    driveRightBTalon.configPeakOutputReverse(Math.toIntExact(Math.round(realLimit)));
    driveRightCTalon.configPeakOutputReverse(Math.toIntExact(Math.round(realLimit)));
    driveLeftTalon.configForwardSoftLimitEnable(true);
    driveLeftBTalon.configForwardSoftLimitEnable(true);
    driveLeftCTalon.configForwardSoftLimitEnable(true);
    driveRightTalon.configForwardSoftLimitEnable(true);
    driveRightBTalon.configForwardSoftLimitEnable(true);
    driveRightCTalon.configForwardSoftLimitEnable(true);
    driveLeftTalon.configReverseSoftLimitEnable(true);
    driveLeftBTalon.configReverseSoftLimitEnable(true);
    driveLeftCTalon.configReverseSoftLimitEnable(true);
    driveRightTalon.configReverseSoftLimitEnable(true);
    driveRightBTalon.configReverseSoftLimitEnable(true);
    driveRightCTalon.configReverseSoftLimitEnable(true);
  }

  @Override
  public void stopLimitingPower() {
    driveLeftTalon.configForwardSoftLimitEnable(false);
    driveLeftBTalon.configForwardSoftLimitEnable(false);
    driveLeftCTalon.configForwardSoftLimitEnable(false);
    driveRightTalon.configForwardSoftLimitEnable(false);
    driveRightBTalon.configForwardSoftLimitEnable(false);
    driveRightCTalon.configForwardSoftLimitEnable(false);
    driveLeftTalon.configReverseSoftLimitEnable(false);
    driveLeftBTalon.configReverseSoftLimitEnable(false);
    driveLeftCTalon.configReverseSoftLimitEnable(false);
    driveRightTalon.configReverseSoftLimitEnable(false);
    driveRightBTalon.configReverseSoftLimitEnable(false);
    driveRightCTalon.configReverseSoftLimitEnable(false);
  }
}
