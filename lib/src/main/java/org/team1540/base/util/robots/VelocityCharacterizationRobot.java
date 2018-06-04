package org.team1540.base.util.robots;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Scheduler;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import org.team1540.base.preferencemanager.Preference;
import org.team1540.base.preferencemanager.PreferenceManager;
import org.team1540.base.wrappers.ChickenTalon;

/**
 * Self-contained robot class to characterize a drivetrain's velocity term.
 *
 * When deployed to a three-motor-per-side robot with left motors on motors 1, 2, and 3 and right
 * motors on motors 4, 5, and 6, whenever the A button is pressed and held during teleop the robot
 * will carry out a quasi-static velocity characterization as described in Eli Barnett's paper "FRC
 * Drivetrain Characterization" until the button is released.
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

  private PrintWriter csvWriter = null;

  private Joystick joystick = new Joystick(0);

  @Preference("Voltage Ramp Rate")
  public double rampRate = 0.020833333;

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

  @Override
  public void robotPeriodic() {
    if (!isOperatorControl() && csvWriter != null) {
      csvWriter.close();
      csvWriter = null;
    }
    Scheduler.getInstance().run();
  }

  @Override
  public void teleopPeriodic() {
    if (joystick.getRawButton(1)) { // if button A is pressed
      if (csvWriter == null) {
        // create a new CSV writer, reset everything
        reset();
        try {
          csvWriter = new PrintWriter(new File(
              "/home/lvuser/dtmeasure/measure-" + System.currentTimeMillis() + ".csv"));
          csvWriter.println("lvoltage,lvelocity,rvoltage,rvelocity");
        } catch (FileNotFoundException e) {
          throw new RuntimeException(e);
        }
        appliedOutput = 0;
        driveLeftMotorA.set(ControlMode.PercentOutput, 0);
        driveRightMotorA.set(ControlMode.PercentOutput, 0);
      } else {

        csvWriter.println(driveLeftMotorA.getMotorOutputVoltage() + ","
            + driveLeftMotorA.getSelectedSensorVelocity() + ","
            + driveRightMotorA.getMotorOutputVoltage() + ","
            + driveRightMotorA.getSelectedSensorVelocity());

        appliedOutput += rampRate * ((System.currentTimeMillis() - lastTime) / 1000.0);
        lastTime = System.currentTimeMillis();
        driveLeftMotorA.set(ControlMode.PercentOutput, appliedOutput);
        driveRightMotorA.set(ControlMode.PercentOutput, appliedOutput);
      }
    } else {
      appliedOutput = 0;
      if (csvWriter != null) {
        csvWriter.close();
        csvWriter = null;
      }
      driveLeftMotorA.set(ControlMode.PercentOutput, 0);
      driveRightMotorA.set(ControlMode.PercentOutput, 0);
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
    }
  }
}
