package org.team1540.rooster.util.robots;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.team1540.rooster.preferencemanager.Preference;
import org.team1540.rooster.preferencemanager.PreferenceManager;
import org.team1540.rooster.wrappers.ChickenTalon;

/**
 * Self-contained robot class to characterize a drivetrain's acceleration term.
 *
 * When deployed to a three-motor-per-side robot with left motors on motors 1, 2, and 3 and right
 * motors on motors 4, 5, and 6, whenever the A button is pressed and held during teleop the robot
 * will carry out an acceleration characterization as described in Eli Barnett's paper "FRC
 * Drivetrain Characterization" until the button is released. (Encoders should be on motors 1 and
 * 4.) The results will be saved in a file in the /home/lvuser/dtmeasure directory named
 * "measureaccel-" followed by the Unix timestamp of the test start and ".csv".
 */
public class AccelerationCharacterizationRobot extends IterativeRobot {

  @Preference("kV")
  public double kV;
  @Preference("VIntercept")
  public double vIntercept;

  @Preference("Invert Left Motor")
  public boolean invertLeft = false;
  @Preference("Invert Right Motor")
  public boolean invertRight = true;
  @Preference(persistent = false)
  public int accelMeasurementWindow = 6;

  @Preference(persistent = false)
  public double setpoint = 0.6;

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

  private Notifier notifier = new Notifier(this::run);

  @SuppressWarnings("SuspiciousNameCombination")
  private void run() {
    if (!isOperatorControl() && csvWriter != null) {
      csvWriter.close();
      csvWriter = null;
    }
    if (leftVelocities.size() == accelMeasurementWindow) {
      leftVelocities.remove(0);
      rightVelocities.remove(0);
      leftVoltages.remove(0);
      rightVoltages.remove(0);
      times.remove(0);
    }
    double leftVelocity = driveLeftMotorA.getSelectedSensorVelocity();
    double rightVelocity = driveRightMotorA.getSelectedSensorVelocity();

    leftVelocities.add(leftVelocity);
    rightVelocities.add(rightVelocity);

    double accelCausingVoltageLeft =
        driveLeftMotorA.getMotorOutputVoltage() - (kV * leftVelocity
            + vIntercept);
    double accelCausingVoltageRight =
        driveRightMotorA.getMotorOutputVoltage() - (kV * rightVelocity
            + vIntercept);
    leftVoltages.add(accelCausingVoltageLeft);
    rightVoltages.add(accelCausingVoltageRight);
    times.add((double) System.currentTimeMillis() / 1000.0);

    if (leftVelocities.size() == accelMeasurementWindow) {
      double lAccel = bestFitSlope(times, leftVelocities);
      double rAccel = bestFitSlope(times, rightVelocities);
      SmartDashboard.putNumber("Left Accel", lAccel);
      SmartDashboard.putNumber("Right Accel", rAccel);
    }

    if (joystick.getRawButton(1)) { // if button A is pressed
      if (csvWriter == null) {
        // create a new CSV writer, reset everything
        reset();
        try {
          File dir = new File("/home/lvuser/dtmeasure/");
          if (!dir.exists()) {
            dir.mkdirs();
          }
          csvWriter = new PrintWriter(new File(
              "/home/lvuser/dtmeasure/measureaccel-" + System.currentTimeMillis() + ".csv"));
          csvWriter.println("lvoltage,laccel,rvoltage,raccel");
        } catch (FileNotFoundException e) {
          throw new RuntimeException(e);
        }
        driveLeftMotorA.set(ControlMode.PercentOutput, 0);
        driveRightMotorA.set(ControlMode.PercentOutput, 0);
      } else {
        SmartDashboard.putNumber("Left Output", driveLeftMotorA.getMotorOutputPercent());
        SmartDashboard.putNumber("Left Velocity", leftVelocity);
        SmartDashboard.putNumber("Right Output", driveRightMotorA.getMotorOutputPercent());
        SmartDashboard.putNumber("Right Velocity", rightVelocity);

        if (leftVelocities.size() == accelMeasurementWindow) {
          double lAccel = bestFitSlope(times, leftVelocities);
          double rAccel = bestFitSlope(times, rightVelocities);
          csvWriter.println(
              leftVoltages.get(1) + "," + lAccel + "," + rightVoltages.get(1) + "," + rAccel);
          System.out.println(leftVelocities.toString());
          System.out.println(times.toString());
          System.out.println(lAccel);
        }
        driveLeftMotorA.set(ControlMode.PercentOutput, setpoint);
        driveRightMotorA.set(ControlMode.PercentOutput, setpoint);
      }
    } else {
      if (csvWriter != null) {
        csvWriter.close();
        csvWriter = null;
      }
      driveLeftMotorA.set(ControlMode.PercentOutput, 0);
      driveRightMotorA.set(ControlMode.PercentOutput, 0);
    }
  }

  private Joystick joystick = new Joystick(0);

  private List<Double> leftVelocities = new LinkedList<>();
  private List<Double> leftVoltages = new LinkedList<>();
  private List<Double> rightVelocities = new LinkedList<>();
  private List<Double> rightVoltages = new LinkedList<>();
  private List<Double> times = new LinkedList<>();

  @Override
  public void robotInit() {
    PreferenceManager.getInstance().add(this);
    reset();
    notifier.startPeriodic(0.01);

    SmartDashboard.setDefaultNumber("Setpoint", 0.6);
  }

  @Override
  public void robotPeriodic() {
    Scheduler.getInstance().run(); // process preferences
  }

  @Override
  public void teleopPeriodic() {

  }

  @Override
  public void teleopInit() {
    reset();
    for (ChickenTalon talon : driveMotorAll) {
      talon.setBrake(true);
    }
  }

  @Override
  public void disabledInit() {
    for (ChickenTalon talon : driveMotorAll) {
      talon.setBrake(false);
    }
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

  private static double bestFitSlope(List<Double> xVals, List<Double> yVals) {
    SimpleRegression reg = new SimpleRegression();
    for (int i = 0; i < xVals.size(); i++) {
      reg.addData(xVals.get(i), yVals.get(i));
    }
    return reg.getSlope();
  }
}
