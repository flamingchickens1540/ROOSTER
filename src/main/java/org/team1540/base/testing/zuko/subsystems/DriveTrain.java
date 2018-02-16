package org.team1540.base.testing.zuko.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team1540.base.power.PowerManageable;
import org.team1540.base.power.PowerManager;
import org.team1540.base.testing.zuko.OI;
import org.team1540.base.testing.zuko.Robot;
import org.team1540.base.testing.zuko.RobotMap;
import org.team1540.base.wrappers.ChickenController;
import org.team1540.base.wrappers.ChickenTalon;
import org.team1540.base.wrappers.ChickenTalon.TalonControlMode;

public class DriveTrain extends Subsystem implements PowerManageable {

  private final ChickenTalon driveLeftTalon = new ChickenTalon(RobotMap.driveLeftA);
  private final ChickenTalon driveLeftBTalon = new ChickenTalon(RobotMap.driveLeftB);
  private final ChickenTalon driveLeftCTalon = new ChickenTalon(RobotMap.driveLeftC);

  private final ChickenTalon driveRightTalon = new ChickenTalon(RobotMap.driveRightA);
  private final ChickenTalon driveRightBTalon = new ChickenTalon(RobotMap.driveRightB);
  private final ChickenTalon driveRightCTalon = new ChickenTalon(RobotMap.driveRightC);

  private ChickenController[] allMotors = {driveLeftTalon, driveLeftBTalon, driveLeftCTalon,
      driveRightTalon, driveRightBTalon, driveRightCTalon};

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
    setDefaultCommand(new TankDrive());
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
    driveLeftTalon.setControlMode(ControlMode.Velocity);

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
    double total = 0;
    for (ChickenController currentController : allMotors) {
      total += currentController.getMotorOutputVoltage();
    }
    return total;
  }

  @Override
  public void setVoltageLimit(double limit) {
    for (ChickenController currentController : allMotors) {
      double realVoltage =
          currentController.getBusVoltage() + currentController.getMotorOutputVoltage();
      currentController.configPeakOutputForward(limit / realVoltage / allMotors.length);
      currentController.configPeakOutputReverse(-limit / realVoltage / allMotors.length);
    }
  }

  @Override
  public void stopLimitingPower() {
    for (ChickenController currentController : allMotors) {
      currentController.configPeakOutputForward(1);
      currentController.configPeakOutputReverse(-1);
    }
  }

  public class TankDrive extends Command {

    public TankDrive() {
      requires(Robot.driveTrain);
    }

    @Override
    protected void initialize() {
    }

    @Override
    protected void execute() {
      double lVal = -(OI.getDriveLeftAxis() + OI.getDriveLeftTrigger() - OI.getDriveRightTrigger());
      double rVal = OI.getDriveRightAxis() + OI.getDriveLeftTrigger() - OI.getDriveRightTrigger();
      driveLeftTalon.set(ControlMode.PercentOutput, lVal);
      driveRightTalon.set(ControlMode.PercentOutput, rVal);
    }

    @Override
    protected boolean isFinished() {
      return false;
    }

  }
}
