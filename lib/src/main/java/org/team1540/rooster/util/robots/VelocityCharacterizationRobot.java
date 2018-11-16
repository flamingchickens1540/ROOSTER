package org.team1540.rooster.util.robots;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.jetbrains.annotations.NotNull;
import org.team1540.rooster.preferencemanager.Preference;
import org.team1540.rooster.preferencemanager.PreferenceManager;
import org.team1540.rooster.util.SimpleCommand;
import org.team1540.rooster.wrappers.ChickenTalon;

//TODO MORE DOCS

/**
 * Self-contained robot class to characterize a drivetrain's velocity term.
 *
 * Whenever the A button is pressed and held during teleop the robot will carry out a quasi-static
 * velocity characterization as described in Eli Barnett's paper "FRC Drivetrain Characterization"
 * until the button is released. The results (kV, vIntercept, and an R^2 value) are output to the
 * SmartDashboard.
 */
public class VelocityCharacterizationRobot extends IterativeRobot {

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
  public double tpu = 1;


  private ChickenTalon driveLeftMotorA;
  private ChickenTalon driveLeftMotorB;
  private ChickenTalon driveLeftMotorC;
  private ChickenTalon driveRightMotorA;
  private ChickenTalon driveRightMotorB;
  private ChickenTalon driveRightMotorC;

  private SimpleRegression leftRegression = new SimpleRegression();
  private SimpleRegression rightRegression = new SimpleRegression();
  private boolean running = false;

  private Joystick joystick = new Joystick(0);

  private static final double SATURATION_VOLTAGE = 12;
  @Preference("Voltage Ramp Rate (V/sec)")
  public double rampRate = 0.25;

  @Preference("Invert Left Motor")
  public boolean invertLeft = false;
  @Preference("Invert Right Motor")
  public boolean invertRight = true;

  private double appliedOutput = 0;

  public long lastTime;

  @Override
  public void robotInit() {
    PreferenceManager.getInstance().add(this);
    Command reset = new SimpleCommand("Reset", () -> {
      if (lMotor1ID != -1) {
        driveLeftMotorA = new ChickenTalon(lMotor1ID);
      } else {
        System.err.println("Left Motor 1 must be set!");
        return;
      }
      if (lMotor2ID != -1) {
        driveLeftMotorB = new ChickenTalon(lMotor2ID);
        driveLeftMotorB.set(ControlMode.Follower, driveLeftMotorA.getDeviceID());
      } else {
        if (driveLeftMotorB != null) {
          driveLeftMotorB.set(ControlMode.PercentOutput, 0);
        }
        driveLeftMotorB = null;
      }
      if (lMotor3ID != -1) {
        driveLeftMotorC = new ChickenTalon(lMotor3ID);
        driveLeftMotorC.set(ControlMode.Follower, driveLeftMotorA.getDeviceID());
      } else {
        if (driveLeftMotorC != null) {
          driveLeftMotorC.set(ControlMode.PercentOutput, 0);
        }
        driveLeftMotorC = null;
      }

      if (rMotor1ID != -1) {
        driveRightMotorA = new ChickenTalon(rMotor1ID);
      } else {
        System.err.println("Right Motor 1 must be set!");
        return;
      }
      if (rMotor2ID != -1) {
        driveRightMotorB = new ChickenTalon(rMotor2ID);
        driveRightMotorB.set(ControlMode.Follower, driveRightMotorA.getDeviceID());
      } else {
        if (driveRightMotorB != null) {
          driveRightMotorB.set(ControlMode.PercentOutput, 0);
        }
        driveRightMotorB = null;
      }
      if (rMotor3ID != -1) {
        driveRightMotorC = new ChickenTalon(rMotor3ID);
        driveRightMotorC.set(ControlMode.Follower, driveRightMotorA.getDeviceID());
      } else {
        if (driveRightMotorC != null) {
          driveRightMotorC.set(ControlMode.PercentOutput, 0);
        }
        driveRightMotorC = null;
      }
      for (ChickenTalon motor : new ChickenTalon[]{driveLeftMotorA, driveLeftMotorB,
          driveLeftMotorC, driveRightMotorA, driveRightMotorB,
          driveRightMotorC}) {
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
      if (driveLeftMotorA != null) {
        driveLeftMotorA.setSelectedSensorPosition(0);
      }

      if (driveRightMotorA != null) {
        driveRightMotorA.setSelectedSensorPosition(0);
      }
    });
    zero.setRunWhenDisabled(true);
    SmartDashboard.putData(zero);
  }

  private static void putRegressionData(@NotNull SimpleRegression regression, String prefix) {
    // getSlope, getIntercept, and getRSquare all have the same criteria for returning NaN
    if (!Double.isNaN(regression.getSlope())) {
      SmartDashboard.putNumber(prefix + " Calculated kV", regression.getSlope());
      SmartDashboard.putNumber(prefix + " Calculated vIntercept", regression.getIntercept());
      SmartDashboard.putNumber(prefix + " rSquared", regression.getRSquare());
    } else {
      SmartDashboard.putNumber(prefix + " Calculated kV", 0);
      SmartDashboard.putNumber(prefix + " Calculated vIntercept", 0);
      SmartDashboard.putNumber(prefix + " rSquared", 0);
    }
  }

  @Override
  public void robotPeriodic() {
    putRegressionData(leftRegression, "Left");
    putRegressionData(rightRegression, "Right");
    Scheduler.getInstance().run();
    if (driveLeftMotorA != null) {
      driveLeftMotorA.setSensorPhase(invertLeftSensor);
    }
    for (ChickenTalon talon : new ChickenTalon[]{driveLeftMotorA, driveLeftMotorB,
        driveLeftMotorC}) {
      if (talon != null) {
        talon.setInverted(invertLeftMotor);
      }
    }

    if (driveRightMotorA != null) {
      driveRightMotorA.setSensorPhase(invertRightSensor);
    }
    for (ChickenTalon talon : new ChickenTalon[]{driveRightMotorA, driveRightMotorB,
        driveRightMotorC}) {
      if (talon != null) {
        talon.setInverted(invertRightMotor);
      }
    }
  }

  @Override
  public void teleopPeriodic() {
    if (joystick.getRawButton(1)) { // if button A is pressed
      if (!running) {
        // reset everything
        running = true;
        leftRegression.clear();
        rightRegression.clear();
        appliedOutput = 0;
        driveLeftMotorA.set(ControlMode.PercentOutput, 0);
        driveRightMotorA.set(ControlMode.PercentOutput, 0);
      } else {
        double leftVelocity = (driveLeftMotorA.getSelectedSensorVelocity() * 10) / tpu;
        if (leftVelocity != 0) {
          leftRegression.addData(leftVelocity, driveLeftMotorA.getMotorOutputVoltage());
        }

        double rightVelocity = (driveRightMotorA.getSelectedSensorVelocity() * 10) / tpu;
        if (rightVelocity != 0) {
          rightRegression.addData(leftVelocity, driveRightMotorA.getMotorOutputVoltage());
        }

        appliedOutput +=
            (rampRate / SATURATION_VOLTAGE) * ((System.currentTimeMillis() - lastTime) / 1000.0);
        lastTime = System.currentTimeMillis();
        driveLeftMotorA.set(ControlMode.PercentOutput, appliedOutput);
        driveRightMotorA.set(ControlMode.PercentOutput, appliedOutput);
      }
    } else {
      appliedOutput = 0;
      driveLeftMotorA.set(ControlMode.PercentOutput, 0);
      driveRightMotorA.set(ControlMode.PercentOutput, 0);
      running = false;
    }
    lastTime = System.currentTimeMillis();
  }
}
