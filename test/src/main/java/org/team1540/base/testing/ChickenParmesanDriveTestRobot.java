package org.team1540.base.testing;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.command.Scheduler;
import org.team1540.base.Utilities;
import org.team1540.base.preferencemanager.Preference;
import org.team1540.base.preferencemanager.PreferenceManager;
import org.team1540.base.wrappers.ChickenTalon;

public class ChickenParmesanDriveTestRobot extends IterativeRobot {

  private XboxController joystick = new XboxController(0);

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


  @Preference("Invert Left Motor")
  public boolean invertLeft = false;
  @Preference("Invert Right Motor")
  public boolean invertRight = true;


  private static final double kThrottleDeadband = 0.1;
  private static final double kWheelDeadband = 0.1;

  // These factor determine how fast the wheel traverses the "non linear" sine curve.
  private static final double kHighWheelNonLinearity = 0.65;
  private static final double kLowWheelNonLinearity = 0.5;

  private static final double kHighNegInertiaScalar = 4.0;

  private static final double kLowNegInertiaThreshold = 0.65;
  private static final double kLowNegInertiaTurnScalar = 3.5;
  private static final double kLowNegInertiaCloseScalar = 4.0;
  private static final double kLowNegInertiaFarScalar = 5.0;

  private static final double kHighSensitivity = 0.65;
  private static final double kLowSensitiity = 0.65;

  private static final double kQuickStopDeadband = 0.5;
  private static final double kQuickStopWeight = 0.1;
  private static final double kQuickStopScalar = 5.0;

  private double mOldWheel = 0.0;
  private double mQuickStopAccumlator = 0.0;
  private double mNegInertiaAccumlator = 0.0;

  public DriveSignal cheesyDrive(double throttle, double wheel, boolean isQuickTurn,
      boolean isHighGear) {

    wheel = Utilities.processDeadzone(wheel, kWheelDeadband);
    throttle = Utilities.processDeadzone(throttle, kThrottleDeadband);

    double negInertia = wheel - mOldWheel;
    mOldWheel = wheel;

    double wheelNonLinearity;
    if (isHighGear) {
      wheelNonLinearity = kHighWheelNonLinearity;
      final double denominator = Math.sin(Math.PI / 2.0 * wheelNonLinearity);
      // Apply a sin function that's scaled to make it feel better.
      wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) / denominator;
      wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) / denominator;
    } else {
      wheelNonLinearity = kLowWheelNonLinearity;
      final double denominator = Math.sin(Math.PI / 2.0 * wheelNonLinearity);
      // Apply a sin function that's scaled to make it feel better.
      wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) / denominator;
      wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) / denominator;
      wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) / denominator;
    }

    double leftPwm, rightPwm, overPower;
    double sensitivity;

    double angularPower;
    double linearPower;

    // Negative inertia!
    double negInertiaScalar;
    if (isHighGear) {
      negInertiaScalar = kHighNegInertiaScalar;
      sensitivity = kHighSensitivity;
    } else {
      if (wheel * negInertia > 0) {
        // If we are moving away from 0.0, aka, trying to get more wheel.
        negInertiaScalar = kLowNegInertiaTurnScalar;
      } else {
        // Otherwise, we are attempting to go back to 0.0.
        if (Math.abs(wheel) > kLowNegInertiaThreshold) {
          negInertiaScalar = kLowNegInertiaFarScalar;
        } else {
          negInertiaScalar = kLowNegInertiaCloseScalar;
        }
      }
      sensitivity = kLowSensitiity;
    }
    double negInertiaPower = negInertia * negInertiaScalar;
    mNegInertiaAccumlator += negInertiaPower;

    wheel = wheel + mNegInertiaAccumlator;
    if (mNegInertiaAccumlator > 1) {
      mNegInertiaAccumlator -= 1;
    } else if (mNegInertiaAccumlator < -1) {
      mNegInertiaAccumlator += 1;
    } else {
      mNegInertiaAccumlator = 0;
    }
    linearPower = throttle;

    // Quickturn!
    if (throttle == 0) {
      if (Math.abs(linearPower) < kQuickStopDeadband) {
        double alpha = kQuickStopWeight;
        mQuickStopAccumlator = (1 - alpha) * mQuickStopAccumlator
            + alpha * Utilities.constrain(wheel, 1.0) * kQuickStopScalar;
      }
      overPower = 1.0;
      angularPower = wheel;
    } else {
      overPower = 0.0;
      angularPower = Math.abs(throttle) * wheel * sensitivity - mQuickStopAccumlator;
      if (mQuickStopAccumlator > 1) {
        mQuickStopAccumlator -= 1;
      } else if (mQuickStopAccumlator < -1) {
        mQuickStopAccumlator += 1;
      } else {
        mQuickStopAccumlator = 0.0;
      }
    }

    rightPwm = leftPwm = linearPower;
    leftPwm += angularPower;
    rightPwm -= angularPower;

    if (leftPwm > 1.0) {
      rightPwm -= overPower * (leftPwm - 1.0);
      leftPwm = 1.0;
    } else if (rightPwm > 1.0) {
      leftPwm -= overPower * (rightPwm - 1.0);
      rightPwm = 1.0;
    } else if (leftPwm < -1.0) {
      rightPwm += overPower * (-1.0 - leftPwm);
      leftPwm = -1.0;
    } else if (rightPwm < -1.0) {
      leftPwm += overPower * (-1.0 - rightPwm);
      rightPwm = -1.0;
    }
    return new DriveSignal(leftPwm, rightPwm);
  }

  @Override
  public void robotInit() {
    PreferenceManager.getInstance().add(this);
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

  @Override
  public void robotPeriodic() {
    Scheduler.getInstance().run();
    for (ChickenTalon talon : new ChickenTalon[]{driveLeftMotorA, driveLeftMotorB,
        driveLeftMotorC}) {
      talon.setInverted(invertLeft);
    }

    for (ChickenTalon talon : new ChickenTalon[]{driveRightMotorA, driveRightMotorB,
        driveRightMotorC}) {
      talon.setInverted(invertRight);
    }
  }

  @Override
  public void teleopPeriodic() {
    DriveSignal sig = cheesyDrive(-joystick.getY(Hand.kLeft), joystick.getX(Hand.kRight),
        joystick.getBumper(Hand.kRight), joystick.getBumper(Hand.kLeft));

    driveLeftMotorA.set(ControlMode.PercentOutput, sig.left);
    driveRightMotorA.set(ControlMode.PercentOutput, sig.right);
  }

  private class DriveSignal {

    double left;
    double right;

    public DriveSignal(double left, double right) {
      this.left = left;
      this.right = right;
    }
  }
}
