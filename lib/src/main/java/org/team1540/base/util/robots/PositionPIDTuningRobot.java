package org.team1540.base.util.robots;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team1540.base.preferencemanager.Preference;
import org.team1540.base.preferencemanager.PreferenceManager;
import org.team1540.base.wrappers.ChickenTalon;

/**
 * Robot class to tune a position PID controller.
 *
 * To change the motors to be tuned, change the preference values and then restart the robot code to
 * allow the values to take effect. To disable a motor, set its motor ID to -1. Motor 1 will be 
 * configured as the master Talon and motors 2, 3, and 4 will be slaved to it in follower mode.
 */
public class PositionPIDTuningRobot extends IterativeRobot {

  @Preference("P")
  public double p;
  @Preference("I")
  public double i;
  @Preference("D")
  public double d;
  @Preference("I-Zone")
  public int iZone;
  @Preference("Setpoint")
  public double setpoint;
  @Preference("Invert Sensor")
  public boolean invertSensor;
  @Preference("Invert Output")
  public boolean invertOutput;
  @Preference("Motor 1 ID")
  public int motor1ID;
  @Preference("Motor 2 ID")
  public int motor2ID;
  @Preference("Motor 3 ID")
  public int motor3ID;
  @Preference("Motor 4 ID")
  public int motor4ID;

  private ChickenTalon motor1 = null;
  private ChickenTalon motor2 = null;
  private ChickenTalon motor3 = null;
  private ChickenTalon motor4 = null;

  @Override
  public void robotInit() {
    System.out.println("Initializing Position PID Tuner Robot");
    System.out.println(
        "To change the motors to be tuned, change the preference values and then restart the robot code to\n"
            + " * allow the values to take effect. To disable a motor, set its motor ID to -1. Motor 1 will be \n"
            + " * configured as the master Talon and motors 2, 3, and 4 will be slaved to it in follower mode.");
    PreferenceManager.getInstance().add(this);
    Scheduler.getInstance().run();
    if (motor1ID != -1) {
      motor1 = new ChickenTalon(motor1ID);
    } else {
      System.err.println("Motor 1 must be set!");
      return;
    }
    if (motor2ID != -1) {
      motor2 = new ChickenTalon(motor2ID);
      motor2.set(ControlMode.Follower, motor1.getDeviceID());
    }
    if (motor3ID != -1) {
      motor3 = new ChickenTalon(motor3ID);
      motor3.set(ControlMode.Follower, motor1.getDeviceID());
    }
    if (motor4ID != -1) {
      motor4 = new ChickenTalon(motor4ID);
      motor4.set(ControlMode.Follower, motor1.getDeviceID());
    }

    for (ChickenTalon motor : new ChickenTalon[]{motor1, motor2, motor3, motor4}) {
      if (motor != null) {
        motor.configClosedloopRamp(0);
        motor.configOpenloopRamp(0);
        motor.configPeakOutputForward(1);
        motor.configPeakOutputReverse(-1);
        motor.enableCurrentLimit(false);
        motor.config_kF(0, 0);
      }
    }
  }

  @Override
  public void teleopPeriodic() {
    if (motor1 != null) {
      motor1.set(ControlMode.Position, setpoint);
      motor1.config_kP(0, p);
      motor1.config_kI(0, i);
      motor1.config_kD(0, d);
      motor1.config_IntegralZone(0, iZone);
    }
  }

  @Override
  public void robotPeriodic() {
    Scheduler.getInstance().run();
    for (ChickenTalon motor : new ChickenTalon[]{motor1, motor2, motor3, motor4}) {
      if (motor != null) {
        motor.setInverted(invertOutput);
      }
    }
    if (motor1 != null) {
      motor1.setSensorPhase(invertSensor);
      SmartDashboard.putNumber("Position", motor1.getSelectedSensorPosition());
      SmartDashboard.putNumber("Velocity", motor1.getSelectedSensorVelocity());
      SmartDashboard.putNumber("Throttle", motor1.getMotorOutputPercent());
      SmartDashboard.putNumber("Current", motor1.getOutputCurrent());
      SmartDashboard.putNumber("Error", motor1.getClosedLoopError());
      SmartDashboard.putNumber("Integral Accumulator", motor1.getIntegralAccumulator());
    }
  }
}
