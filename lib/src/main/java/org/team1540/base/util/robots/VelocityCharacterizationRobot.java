package org.team1540.base.util.robots;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.jetbrains.annotations.NotNull;
import org.team1540.base.preferencemanager.Preference;
import org.team1540.base.preferencemanager.PreferenceManager;
import org.team1540.base.wrappers.ChickenTalon;

/**
 * Self-contained robot class to characterize a drivetrain's velocity term.
 *
 * When deployed to a three-motor-per-side robot with left motors on motors 1, 2, and 3 and right
 * motors on motors 4, 5, and 6, whenever the A button is pressed and held during teleop the robot
 * will carry out a quasi-static velocity characterization as described in Eli Barnett's paper "FRC
 * Drivetrain Characterization" until the button is released. The results (kV, vIntercept, and an
 * R^2 value) are output to the SmartDashboard.
 */
public class VelocityCharacterizationRobot extends IterativeRobot {

  private ChickenTalon driveLeftMotorA = new ChickenTalon(1);
  private ChickenTalon driveLeftMotorB = new ChickenTalon(2);
  private ChickenTalon driveLeftMotorC = new ChickenTalon(3);
  private ChickenTalon[] driveLeftMotors = new ChickenTalon[]{driveLeftMotorA, driveLeftMotorB,
      driveLeftMotorC};
  private ChickenTalon driveRightMotorA = new ChickenTalon(4);
  private ChickenTalon driveRightMotorB = new ChickenTalon(5);
  private ChickenTalon driveRightMotorC = new ChickenTalon(6);
  private ChickenTalon[] driveRightMotors = new ChickenTalon[]{driveRightMotorA, driveRightMotorB,
      driveRightMotorC};
  private ChickenTalon[] driveMotorAll = new ChickenTalon[]{driveLeftMotorA, driveLeftMotorB,
      driveLeftMotorC, driveRightMotorA, driveRightMotorB, driveRightMotorC};
  private ChickenTalon[] driveMotorMasters = new ChickenTalon[]{driveLeftMotorA, driveRightMotorA};

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
    reset();
  }

  private static void putRegressionData(@NotNull SimpleRegression regression, String prefix) {
    // getSlope, getIntercept, and getRSquare all have the same criteria for returning NaN
    if (!Double.isNaN(regression.getSlope())) {
      SmartDashboard.putNumber(prefix + " Calculated kV", regression.getSlope());
      SmartDashboard.putNumber(prefix + " Calculated vIntercept", regression.predict(0));
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
  }

  @Override
  public void teleopPeriodic() {
    if (joystick.getRawButton(1)) { // if button A is pressed
      if (!running) {
        // reset everything
        reset();
        running = true;
        leftRegression.clear();
        rightRegression.clear();
        appliedOutput = 0;
        driveLeftMotorA.set(ControlMode.PercentOutput, 0);
        driveRightMotorA.set(ControlMode.PercentOutput, 0);
      } else {
        double leftVelocity = driveLeftMotorA.getSelectedSensorVelocity();
        if (leftVelocity != 0) {
          leftRegression.addData(leftVelocity, driveLeftMotorA.getMotorOutputVoltage());
        }

        double rightVelocity = driveRightMotorA.getSelectedSensorVelocity();
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

  @SuppressWarnings("Duplicates")
  public void reset() {
    driveLeftMotorA.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
    driveRightMotorA.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);

    driveLeftMotorA.setSensorPhase(true);

    for (ChickenTalon talon : driveLeftMotors) {
      talon.setInverted(invertLeft);
    }

    driveRightMotorA.setSensorPhase(true);

    for (ChickenTalon talon : driveRightMotors) {
      talon.setInverted(invertRight);
    }

    driveLeftMotorB.set(ControlMode.Follower, driveLeftMotorA.getDeviceID());
    driveLeftMotorC.set(ControlMode.Follower, driveLeftMotorA.getDeviceID());

    driveRightMotorB.set(ControlMode.Follower, driveRightMotorA.getDeviceID());
    driveRightMotorC.set(ControlMode.Follower, driveRightMotorA.getDeviceID());

    for (ChickenTalon talon : driveMotorAll) {
      talon.setBrake(true);
    }

    for (ChickenTalon talon : driveMotorAll) {
      talon.configClosedloopRamp(0);
      talon.configOpenloopRamp(0);
      talon.configPeakOutputForward(1);
      talon.configPeakOutputReverse(-1);
      talon.enableCurrentLimit(false);
      talon.configVoltageCompSaturation(12);
      talon.enableVoltageCompensation(true);
    }
  }
}