package org.team1540.base.testing.templates.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team1540.base.adjustables.AdjustableManager;
import org.team1540.base.adjustables.Tunable;
import org.team1540.base.wrappers.ChickenTalon;

public class DrivePIDTuningRobot extends IterativeRobot {

  Joystick joystick;
  ChickenTalon lMaster;
  ChickenTalon lSlave1;
  ChickenTalon lSlave2;
  Solenoid leftPneu = new Solenoid(0);
  ChickenTalon rMaster;
  ChickenTalon rSlave1;
  ChickenTalon rSlave2;
  Solenoid rightPneu = new Solenoid(2);

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
  @Tunable("F")
  public double f = 0.0;
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

  @Override
  public void robotInit() {
    lMaster = new ChickenTalon(1);
    lMaster.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
    lMaster.setEncoderCodesPerRev(1024);
    lSlave1 = new ChickenTalon(2);
    lSlave1.set(ControlMode.Follower, lMaster.getDeviceID());
    lSlave2 = new ChickenTalon(3);
    lSlave2.set(ControlMode.Follower, lMaster.getDeviceID());

    rMaster = new ChickenTalon(4);
    rMaster.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
    rMaster.setEncoderCodesPerRev(1024);
    rSlave1 = new ChickenTalon(5);
    rSlave1.set(ControlMode.Follower, rMaster.getDeviceID());
    rSlave2 = new ChickenTalon(6);
    rSlave2.set(ControlMode.Follower, rMaster.getDeviceID());

    AdjustableManager.getInstance().add(this);
    UsbCamera camera = CameraServer.getInstance().startAutomaticCapture("Camera", 0);
    camera.setResolution(640, 480);
    MjpegServer mjpegServer = new MjpegServer("Camera Server", 1181);
    mjpegServer.setSource(camera);
    SmartDashboard.putData(new Compressor());
  }

  @Override
  public void teleopInit() {
  }

  @Override
  public void robotPeriodic() {
    lMaster.config_kP(0, p);
    lMaster.config_kI(0, i);
    lMaster.config_kD(0, d);
    lMaster.config_kF(0, f);
    rMaster.config_kP(0, p);
    rMaster.config_kI(0, i);
    rMaster.config_kD(0, d);
    rMaster.config_kF(0, f);
    SmartDashboard.putNumber("Left vel", lMaster.getSelectedSensorVelocity());
    SmartDashboard.putNumber("Right vel", rMaster.getSelectedSensorVelocity());
    SmartDashboard.putNumber("Left throttle", lMaster.getMotorOutputPercent());
    SmartDashboard.putNumber("Right throttle", rMaster.getMotorOutputPercent());
    SmartDashboard.putNumber("Left error", lMaster.getClosedLoopError());
    SmartDashboard.putNumber("Right error", rMaster.getClosedLoopError());
    SmartDashboard.putNumber("Left target", lMaster.getClosedLoopTarget(0));
    SmartDashboard.putNumber("Right target", rMaster.getClosedLoopTarget(0));
    lMaster.setInverted(invertLeftMotor);
    lMaster.setSensorPhase(invertLeftSensor);
    rMaster.setInverted(invertRightMotor);
    rMaster.setSensorPhase(invertRightSensor);
    AdjustableManager.getInstance().update();
  }

  @Override
  public void teleopPeriodic() {
    leftPneu.set(leftPneuVal);
    rightPneu.set(rightPneuVal);
    lMaster.set(ControlMode.Velocity, invertLeftSetpoint ? -velocity : velocity);
    rMaster.set(ControlMode.Velocity, invertRightSetpoint ? -velocity : velocity);
  }
}
