package org.team1540.base.testing.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team1540.base.adjustables.AdjustableManager;
import org.team1540.base.adjustables.Tunable;
import org.team1540.base.drive.PidDrive;
import org.team1540.base.drive.PidDriveFactory;
import org.team1540.base.drive.PowerJoystickScaling;
import org.team1540.base.wrappers.ChickenTalon;

public class PidDriveTestRobot extends IterativeRobot {

  @Tunable("Left Shifter")
  public boolean leftPneuVal = false;
  @Tunable("Right Shifter")
  public boolean rightPneuVal = true;
  @Tunable("P")
  public double p = 0.0;
  @Tunable("I")
  public double i = 0.0;
  @Tunable("D")
  public double d = 0.0;
  @Tunable("Velocity")
  public double velocity = 0.0;
  @Tunable("Invert left motor")
  public boolean invertLeftMotor = false;
  @Tunable("Invert right motor")
  public boolean invertRightMotor = false;
  @Tunable("Invert left sensor")
  public boolean invertLeftSensor = false;
  @Tunable("Invert right sensor")
  public boolean invertRightSensor = false;
  @Tunable("Invert left setpoint")
  public boolean invertLeftSetpoint = false;
  @Tunable("Invert right setpoint")
  public boolean invertRightSetpoint = false;
  @Tunable("Invert left brake")
  public boolean iLeftBrake = false;
  @Tunable("Invert right brake")
  public boolean iRightBrake;
  @Tunable("Closed-loop ramp")
  public double closedLoopRamp = 0.0;
  @Tunable("Joystick exponent")
  public double joystickExp = 2.0;
  @Tunable("Max brake pct")
  public double brakePct = 0.1;
  @Tunable("Brake Override Threshold")
  public double brakeOverThresh = 0.9;

  private Compressor compressor = new Compressor();
  private Joystick joystick;
  private ChickenTalon lMaster;
  private ChickenTalon lSlave1;
  private ChickenTalon lSlave2;
  private Solenoid leftPneu = new Solenoid(0);
  private ChickenTalon rMaster;
  private ChickenTalon rSlave1;
  private ChickenTalon rSlave2;
  private Solenoid rightPneu = new Solenoid(2);
  private ChickenTalon[] talons;
  private ChickenTalon[] lefts;
  private ChickenTalon[] rights;
  private PidDrive drive;
  private PowerJoystickScaling scaling;

  @Override
  public void robotInit() {
    AdjustableManager.getInstance().add(this);
    SmartDashboard.putData(new Compressor());
    joystick = new Joystick(0);

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

    talons = new ChickenTalon[]{lMaster, lSlave1, lSlave2, rMaster, rSlave1, rSlave2};
    lefts = new ChickenTalon[]{lMaster, lSlave1, lSlave2};
    rights = new ChickenTalon[]{rMaster, rSlave1, rSlave2};
    for (ChickenTalon talon : talons) {
      talon.configPeakOutputForward(1);
      talon.configPeakOutputReverse(-1);
    }
    scaling = new PowerJoystickScaling(joystickExp);
    Subsystem dummy = new Subsystem() {
      @Override
      protected void initDefaultCommand() {

      }
    };

    drive = new PidDriveFactory()
        .setSubsystem(dummy)
        .setLeft(lMaster)
        .setRight(rMaster)
        .setMaxVel(velocity)
        .setScaling(scaling)
        .setMaxBrakePct(brakePct)
        .setInvertLeftBrakeDirection(iLeftBrake)
        .setInvertRightBrakeDirection(iRightBrake)
        .setBrakingStopZone(0.1)
        .setJoystick(joystick)
        .setLeftAxis(1)
        .setInvertLeft(invertLeftSetpoint)
        .setRightAxis(5)
        .setInvertRight(invertRightSetpoint)
        .setForwardTrigger(3)
        .setBackTrigger(2)
        .setDeadzone(0.1)
        .setBrakeOverrideThresh(brakeOverThresh)
        .createPidDrive();
  }

  @Override
  public void disabledInit() {
  }

  @Override
  public void autonomousInit() {
  }

  @Override
  public void teleopInit() {
    drive.start();
  }

  @Override
  public void testInit() {
  }

  @Override
  public void robotPeriodic() {
    for (ChickenTalon talon : talons) {
      talon.config_kP(0, p);
      talon.config_kI(0, i);
      talon.config_kD(0, d);
      talon.config_kF(0, (double) 1023 / velocity);
      talon.configClosedloopRamp(closedLoopRamp);
      talon.setBrake(true);
    }
    for (ChickenTalon talon : lefts) {
      talon.setInverted(invertLeftMotor);
      talon.setSensorPhase(invertLeftSensor);
    }
    for (ChickenTalon talon : rights) {
      talon.setInverted(invertRightMotor);
      talon.setInverted(invertRightSensor);
    }

    drive.setInvertLeft(invertLeftSetpoint);
    drive.setInvertRight(invertRightSetpoint);
    drive.setInvertLeftBrakeDirection(iLeftBrake);
    drive.setInvertRightBrakeDirection(iRightBrake);
    drive.setMaxBrakePct(brakePct);
    drive.setMaxVel(velocity);
    drive.setBrakeOverrideThresh(brakeOverThresh);

    SmartDashboard.putNumber("Left Out", lMaster.getMotorOutputVoltage());
    SmartDashboard.putNumber("Right Out", rMaster.getMotorOutputVoltage());

    scaling.setPow(joystickExp);
    Scheduler.getInstance().run();
  }

  @Override
  public void disabledPeriodic() {
  }

  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void teleopPeriodic() {
    leftPneu.set(leftPneuVal);
    rightPneu.set(rightPneuVal);
  }
}
