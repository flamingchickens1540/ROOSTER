package org.team1540.base.testing.templates.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.team1540.base.adjustables.AdjustableManager;
import org.team1540.base.adjustables.Tunable;
import org.team1540.base.commandutils.drive.PidDrive;
import org.team1540.base.commandutils.drive.PidDriveFactory;
import org.team1540.base.templates.Drive;
import org.team1540.base.wrappers.ChickenTalon;

public class PidDriveTestRobot extends IterativeRobot {

  private PidDrive pidDriveCommand;
  private DriveTrain drive;
  private Joystick joystick;

  @Tunable("P")
  public double p = 0.0;
  @Tunable("I")
  public double i = 0.0;
  @Tunable("D")
  public double d = 0.0;
  private double f;

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

  @Tunable("Setpoint")
  public double setpoint = 0;

  @Override
  public void robotInit() {
    PidDriveFactory factory = new PidDriveFactory()
        .setDrive(drive)
        .setInvertLeftAxis(invertLeftSetpoint)
        .setInvertRightAxis(invertRightSetpoint)
        .setInvertLeftOutput(invertLeftMotor)
        .setInvertRightOutput(invertRightMotor)
        .setMaxSetpoint(setpoint);

    this.drive = new DriveTrain();
    this.joystick = new Joystick(0);
    this.pidDriveCommand = factory.createSimplePidDrive(joystick, 1, 5, 3, 2);
    AdjustableManager.getInstance().add(this);
    f = 1023 / setpoint;
  }

  @Override
  public void robotPeriodic() {
    Scheduler.getInstance().run();

    pidDriveCommand.setInvertLeftAxis(invertLeftSetpoint);
    pidDriveCommand.setInvertRightAxis(invertRightSetpoint);
    pidDriveCommand.setInvertLeftOutput(invertLeftMotor);
    pidDriveCommand.setInvertRightOutput(invertRightMotor);
    pidDriveCommand.setMaxSetpoint(setpoint);

    drive.updateTunables();
  }

  private class DriveTrain extends Subsystem implements Drive {

    ChickenTalon lMaster;
    ChickenTalon lSlave1;
    ChickenTalon lSlave2;
    ChickenTalon rMaster;
    ChickenTalon rSlave1;
    ChickenTalon rSlave2;

    DriveTrain() {
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
    }

    @Override
    public double getLeftVelocity() {
      return lMaster.getSelectedSensorVelocity();
    }

    @Override
    public double getRightVelocity() {
      return rMaster.getSelectedSensorVelocity();
    }

    @Override
    public void setLeftVelocity(double velocity) {
      lMaster.set(ControlMode.Velocity, velocity * (invertLeftSetpoint ? -1 : 1));
    }

    @Override
    public void setLeftThrottle(double throttle) {
      lMaster.set(ControlMode.PercentOutput, throttle);
    }

    @Override
    public void setRightThrottle(double throttle) {
      rMaster.set(ControlMode.PercentOutput, throttle);
    }

    @Override
    public void setRightVelocity(double velocity) {
      rMaster.set(ControlMode.Velocity, velocity * (invertRightSetpoint ? -1 : 1));
    }

    @Override
    public Subsystem getAttachedSubsystem() {
      return this;
    }

    @Override
    protected void initDefaultCommand() {
    }

    void updateTunables() {
      lMaster.config_kP(0, p);
      lMaster.config_kI(0, i);
      lMaster.config_kD(0, d);
      lMaster.config_kF(0, f);
      rMaster.config_kP(0, p);
      rMaster.config_kI(0, i);
      rMaster.config_kD(0, d);
      rMaster.config_kF(0, f);
      lMaster.setInverted(invertLeftMotor);
      lMaster.setSensorPhase(invertLeftSensor);
      rMaster.setInverted(invertRightMotor);
      rMaster.setSensorPhase(invertRightSensor);
    }
  }
}
