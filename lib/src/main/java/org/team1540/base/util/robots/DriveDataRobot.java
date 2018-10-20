package org.team1540.base.util.robots;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Date;
import org.team1540.base.Utilities;
import org.team1540.base.preferencemanager.Preference;
import org.team1540.base.preferencemanager.PreferenceManager;
import org.team1540.base.util.SimpleCommand;
import org.team1540.base.wrappers.ChickenTalon;

/**
 * Class for collecting various drive data.
 */
public class DriveDataRobot extends IterativeRobot {

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

  private ChickenTalon lMotor1;
  private ChickenTalon lMotor2;
  private ChickenTalon lMotor3;
  private ChickenTalon rMotor1;
  private ChickenTalon rMotor2;
  private ChickenTalon rMotor3;

  private Joystick joystick = new Joystick(0);

  private PrintWriter csvWriter = null;

  private double lPowerTot = 0;
  private double rPowerTot = 0;

  private double lastMeasureTime;
  private double currentPowerRight;
  private double currentPowerLeft;

  @Override
  public void robotInit() {
    lastMeasureTime = System.currentTimeMillis();
    PreferenceManager.getInstance().add(this);
    System.out.println("Initializing Drive Data Collection Robot");
    System.out.println(
        "To change the motors to be tuned, change the preference values and then run the Reset command to "
            + "allow the values to take effect. To disable a motor, set its motor ID to -1. Motor 1 will be "
            + "configured as the master Talon and motors 2, 3, and 4 will be slaved to it in follower mode.");

    Command reset = new SimpleCommand("Reset", () -> {
      if (lMotor1ID != -1) {
        lMotor1 = new ChickenTalon(lMotor1ID);
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

      lPowerTot = 0;
      rPowerTot = 0;
    });
    zero.setRunWhenDisabled(true);
    SmartDashboard.putData(zero);
  }

  @Override
  public void teleopPeriodic() {
    if (logDataToCSV) {
      if (csvWriter == null) {
        // create a new CSV writer, reset everything
        try {
          File file = new File(
              "/home/lvuser/drivedata/drivedata-" + new Date().toString().replace(' ', '-')
                  + ".csv");
          System.out.println("Creating new CSV file at " + file.toString());
          csvWriter = new PrintWriter(file);
          csvWriter.println(
              "lpos,lvel,lvolt,lcurr,lthrot,lpwr,lpwrtot,rpos,rvel,rvolt,rcurr,rthrot,rpwr,rpwrtot"
          );
        } catch (FileNotFoundException e) {
          throw new RuntimeException(e);
        }
      } else {
        csvWriter.println(lMotor1.getSelectedSensorPosition() + ","
            + lMotor1.getSelectedSensorVelocity() + ","
            + lMotor1.getMotorOutputVoltage() + ","
            + lMotor1.getOutputCurrent() + ","
            + lMotor1.getMotorOutputPercent() + ","
            + currentPowerLeft + ","
            + lPowerTot + ","
            + rMotor1.getSelectedSensorPosition() + ","
            + rMotor1.getSelectedSensorVelocity() + ","
            + rMotor1.getMotorOutputVoltage() + ","
            + rMotor1.getOutputCurrent() + ","
            + rMotor1.getMotorOutputPercent() + ","
            + currentPowerRight + ","
            + rPowerTot);
      }
    }

    double trigger = Utilities.processDeadzone(joystick.getRawAxis(2), 0.1) - Utilities
        .processDeadzone(joystick.getRawAxis(3), 0.1);
    lMotor1.set(ControlMode.PercentOutput,
        Utilities.constrain(Utilities.processDeadzone(joystick.getRawAxis(1), 0.1) + trigger, 1));
    rMotor1.set(ControlMode.PercentOutput,
        Utilities.constrain(Utilities.processDeadzone(joystick.getRawAxis(5), 0.1) + trigger, 1));
  }

  @Override
  public void robotPeriodic() {
    Scheduler.getInstance().run();

    if (lMotor1 != null && rMotor1 != null) {
      SmartDashboard.putNumber("LPOS", lMotor1.getSelectedSensorPosition());
      SmartDashboard.putNumber("RPOS", rMotor1.getSelectedSensorPosition());
      SmartDashboard.putNumber("LVEL", lMotor1.getSelectedSensorVelocity());
      SmartDashboard.putNumber("RVEL", rMotor1.getSelectedSensorVelocity());
      SmartDashboard.putNumber("LTHROT", lMotor1.getMotorOutputPercent());
      SmartDashboard.putNumber("RTHROT", rMotor1.getMotorOutputPercent());
      SmartDashboard.putNumber("LCURR",
          lMotor1.getOutputCurrent() + (lMotor2 != null ? lMotor2.getOutputCurrent() : 0) + (
              lMotor3 != null ? lMotor3.getOutputCurrent() : 0));
      SmartDashboard.putNumber("RCURR",
          rMotor1.getOutputCurrent() + (rMotor2 != null ? rMotor2.getOutputCurrent() : 0) + (
              rMotor3 != null ? rMotor3.getOutputCurrent() : 0));
      SmartDashboard.putNumber("LVOLT", lMotor1.getMotorOutputVoltage());
      SmartDashboard.putNumber("RVOLT", rMotor1.getMotorOutputVoltage());

      currentPowerLeft = ((lMotor1.getOutputCurrent() * lMotor1.getMotorOutputVoltage())
          + (lMotor2 != null ? (lMotor2.getOutputCurrent() * lMotor2.getMotorOutputVoltage()) : 0)
          + (lMotor3 != null ? (lMotor3.getOutputCurrent() * lMotor3.getMotorOutputVoltage()) : 0));
      currentPowerRight = ((rMotor1.getOutputCurrent() * rMotor1.getMotorOutputVoltage())
          + (rMotor2 != null ? (rMotor2.getOutputCurrent() * rMotor2.getMotorOutputVoltage()) : 0)
          + (rMotor3 != null ? (rMotor3.getOutputCurrent() * rMotor3.getMotorOutputVoltage()) : 0));

      SmartDashboard.putNumber("LPWR", currentPowerLeft);
      SmartDashboard.putNumber("RPWR", currentPowerRight);

      if (isOperatorControl()) {
        lPowerTot += ((System.currentTimeMillis() - lastMeasureTime) / 1000.0) * currentPowerLeft;
        rPowerTot += ((System.currentTimeMillis() - lastMeasureTime) / 1000.0) * currentPowerRight;
      } else {
        currentPowerLeft = 0;
        currentPowerRight = 0;
      }

      SmartDashboard.putNumber("LPWRTOT", lPowerTot);
      SmartDashboard.putNumber("RPWRTOT", rPowerTot);
    }
    if (!(isOperatorControl() && logDataToCSV) && csvWriter != null) {
      csvWriter.close();
      csvWriter = null;
      System.out.println("Finished writing CSV file");
    }

    lastMeasureTime = System.currentTimeMillis();

    if (lMotor1 != null) {
      lMotor1.setSensorPhase(invertLeftSensor);
    }
    for (ChickenTalon talon : new ChickenTalon[]{lMotor1, lMotor2, lMotor3}) {
      if (talon != null) {
        talon.setInverted(invertLeftMotor);
      }
    }

    if (rMotor1 != null) {
      rMotor1.setSensorPhase(invertRightSensor);
    }
    for (ChickenTalon talon : new ChickenTalon[]{rMotor1, rMotor2, rMotor3}) {
      if (talon != null) {
        talon.setInverted(invertRightMotor);
      }
    }
  }
}
