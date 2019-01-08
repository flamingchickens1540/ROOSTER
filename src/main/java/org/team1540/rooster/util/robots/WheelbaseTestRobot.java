package org.team1540.rooster.util.robots;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team1540.rooster.preferencemanager.Preference;
import org.team1540.rooster.preferencemanager.PreferenceManager;
import org.team1540.rooster.util.SimpleCommand;
import org.team1540.rooster.wrappers.ChickenTalon;

/**
 * Class to determine a robot's wheelbase width. For use instructions, load onto a robot and check
 * the console.
 */
public class WheelbaseTestRobot extends IterativeRobot {

  @Preference(persistent = false)
  public boolean logDataToCSV = false;
  @Preference(persistent = false)
  public int lMotor1ID = -1;
  @Preference(persistent = false)
  public int lMotor2ID = -1;
  @Preference(persistent = false)
  public int lMotor3ID = -1;
  @Preference(persistent = false)
  public int rMotor1ID = -1;
  @Preference(persistent = false)
  public int rMotor2ID = -1;
  @Preference(persistent = false)
  public int rMotor3ID = -1;
  @Preference(persistent = false)
  public boolean invertLeftMotor = false;
  @Preference(persistent = false)
  public boolean invertRightMotor = false;
  @Preference(persistent = false)
  public boolean invertLeftSensor = false;
  @Preference(persistent = false)
  public boolean invertRightSensor = false;
  @Preference(persistent = false)
  public boolean brake = false;
  @Preference(persistent = false)
  public double encoderTPU = 1;
  @Preference(persistent = false)
  public double setpoint = 0.5;

  private ChickenTalon lMotor1;
  private ChickenTalon lMotor2;
  private ChickenTalon lMotor3;
  private ChickenTalon rMotor1;
  private ChickenTalon rMotor2;
  private ChickenTalon rMotor3;

  private Joystick joystick = new Joystick(0);

  @Override
  public void robotInit() {
    System.out.println("Initializing Wheelbase Test Robot");
    System.out.println(
        "To change the motors to be used, change the preference values and then run the Reset command to "
            + "allow the values to take effect. To disable a motor, set its motor ID to -1. Motor 1 will be "
            + "configured as the master Talon and motors 2, 3, and 4 will be slaved to it in follower mode.");

    PreferenceManager.getInstance().add(this);

    Command reset = new SimpleCommand("Reset", () -> {
      if (lMotor1ID != -1) {
        lMotor1 = new ChickenTalon(lMotor1ID);
        lMotor1.setSensorPhase(invertLeftSensor);
      } else {
        System.err.println("Left Motor 1 must be set!");
        return;
      }
      if (lMotor2ID != -1) {
        lMotor2 = new ChickenTalon(lMotor2ID);
        lMotor2.set(ControlMode.Follower, lMotor1.getDeviceID());
      } else {
        if (lMotor2 != null) {
          lMotor2.set(ControlMode.PercentOutput, 0);
        }
        lMotor2 = null;
      }
      if (lMotor3ID != -1) {
        lMotor3 = new ChickenTalon(lMotor3ID);
        lMotor3.set(ControlMode.Follower, lMotor1.getDeviceID());
      } else {
        if (lMotor3 != null) {
          lMotor3.set(ControlMode.PercentOutput, 0);
        }
        lMotor3 = null;
      }

      if (rMotor1ID != -1) {
        rMotor1 = new ChickenTalon(rMotor1ID);
        rMotor1.setSensorPhase(invertRightSensor);
      } else {
        System.err.println("Right Motor 1 must be set!");
        return;
      }
      if (rMotor2ID != -1) {
        rMotor2 = new ChickenTalon(rMotor2ID);
        rMotor2.set(ControlMode.Follower, rMotor1.getDeviceID());
      } else {
        if (rMotor2 != null) {
          rMotor2.set(ControlMode.PercentOutput, 0);
        }
        rMotor2 = null;
      }
      if (rMotor3ID != -1) {
        rMotor3 = new ChickenTalon(rMotor3ID);
        rMotor3.set(ControlMode.Follower, rMotor1.getDeviceID());
      } else {
        if (rMotor3 != null) {
          rMotor3.set(ControlMode.PercentOutput, 0);
        }
        rMotor3 = null;
      }
      for (ChickenTalon motor : new ChickenTalon[]{lMotor1, lMotor2, lMotor3, rMotor1, rMotor2,
          rMotor3}) {
        if (motor != null) {
          motor.configClosedloopRamp(0);
          motor.configOpenloopRamp(0);
          motor.configPeakOutputForward(1);
          motor.configPeakOutputReverse(-1);
          motor.enableCurrentLimit(false);
          motor.setBrake(brake);
        }
      }

      for (ChickenTalon motor : new ChickenTalon[]{lMotor1, lMotor2, lMotor3}) {
        motor.setInverted(invertLeftMotor);
      }
      for (ChickenTalon motor : new ChickenTalon[]{rMotor1, rMotor2, rMotor3}) {
        motor.setInverted(invertRightMotor);
      }
    });
    reset.setRunWhenDisabled(true);
    reset.start();
    SmartDashboard.putData(reset);

    Command zero = new SimpleCommand("Zero", () -> {
      if (lMotor1 != null) {
        lMotor1.setSelectedSensorPosition(0);
      }

      if (rMotor1 != null) {
        rMotor1.setSelectedSensorPosition(0);
      }
    });
    zero.setRunWhenDisabled(true);
    SmartDashboard.putData(zero);
  }

  @Override
  public void teleopInit() {
    System.out.println("Zero encoders, then press A until the robot completes 10 revolutions");
  }

  @Override
  public void teleopPeriodic() {
    if (joystick.getRawButton(1)) { // button A
      lMotor1.set(ControlMode.PercentOutput, setpoint);
      rMotor1.set(ControlMode.PercentOutput, -setpoint);
    } else {
      lMotor1.set(ControlMode.PercentOutput, 0);
      rMotor1.set(ControlMode.PercentOutput, 0);
    }
  }

  @Override
  public void robotPeriodic() {
    Scheduler.getInstance().run();
    if (lMotor1 != null && rMotor1 != null) {
      SmartDashboard.putNumber("LPOS", lMotor1.getSelectedSensorPosition());
      SmartDashboard.putNumber("RPOS", rMotor1.getSelectedSensorPosition());

      double leftDistance = lMotor1.getSelectedSensorPosition();
      double rightDistance = -rMotor1.getSelectedSensorPosition();

      SmartDashboard.putNumber("Left Distance", leftDistance);
      SmartDashboard.putNumber("Right Distance", rightDistance);

      SmartDashboard.putNumber("Calculated width (assuming 10 rots)",
          (((leftDistance + rightDistance) / 2) / (10 * Math.PI)) / encoderTPU);
    }
  }
}
