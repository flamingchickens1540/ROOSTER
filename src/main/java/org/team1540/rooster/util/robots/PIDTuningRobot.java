package org.team1540.rooster.util.robots;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.team1540.rooster.preferencemanager.Preference;
import org.team1540.rooster.preferencemanager.PreferenceManager;
import org.team1540.rooster.util.OIUtils;
import org.team1540.rooster.util.SimpleCommand;
import org.team1540.rooster.wrappers.ChickenTalon;

/**
 * Robot class to tune a Motion Magic PID controller.
 *
 * To change the motors to be tuned, change the preference values and then restart the robot code to
 * allow the values to take effect. To disable a motor, set its motor ID to -1. Motor 1 will be
 * configured as the master Talon and motors 2, 3, and 4 will be slaved to it in follower mode.
 */
public class PIDTuningRobot extends IterativeRobot {

  @Preference(value = "P", persistent = false)
  public double p;
  @Preference(value = "I", persistent = false)
  public double i;
  @Preference(value = "D", persistent = false)
  public double d;
  @Preference(value = "F", persistent = false)
  public double f;
  @Preference(value = "I-Zone", persistent = false)
  public int iZone;
  @Preference(value = "Max I-Accum", persistent = false)
  public int maxIAccum;
  @Preference(value = "Max Acceleration", persistent = false)
  public int maxAccel;
  @Preference(value = "Max Velocity", persistent = false)
  public int maxVel;
  @Preference(value = "Setpoint", persistent = false)
  public double setpoint;
  @Preference(value = "Invert Sensor", persistent = false)
  public boolean invertSensor;
  @Preference(value = "Invert Output", persistent = false)
  public boolean invertOutput;
  @Preference(value = "Motor 1 ID", persistent = false)
  public int motor1ID;
  @Preference(value = "Motor 2 ID", persistent = false)
  public int motor2ID;
  @Preference(value = "Motor 3 ID", persistent = false)
  public int motor3ID;
  @Preference(value = "Motor 4 ID", persistent = false)
  public int motor4ID;
  @Preference(value = "Enable PID", persistent = false)
  public boolean enablePID;

  @Nullable
  private ChickenTalon motor1;
  @Nullable
  private ChickenTalon motor2;
  @Nullable
  private ChickenTalon motor3;
  @Nullable
  private ChickenTalon motor4;

  @NotNull
  private Joystick joystick = new Joystick(0);

  @NotNull
  private SendableChooser<ControlMode> controlModeChooser = new SendableChooser<>();

  @Override
  public void robotInit() {
    System.out.println("Initializing PID Tuner Robot");
    System.out.println(
        "To change the motors to be tuned, change the preference values and then run the Reset command to\n"
            + " * allow the values to take effect. To disable a motor, set its motor ID to -1. Motor 1 will be \n"
            + " * configured as the master Talon and motors 2, 3, and 4 will be slaved to it in follower mode.");
    PreferenceManager.getInstance().add(this);
    Scheduler.getInstance().run(); // allow the PreferenceManager to update

    controlModeChooser.addDefault("Position", ControlMode.Position);
    controlModeChooser.addDefault("Velocity", ControlMode.Velocity);
    controlModeChooser.addDefault("MotionMagic", ControlMode.MotionMagic);

    SmartDashboard.putData("Control Mode Chooser", controlModeChooser);
    Command reset = new SimpleCommand("Reset", () -> {
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
        }
      }
    });
    reset.setRunWhenDisabled(true);
    reset.start();
    SmartDashboard.putData(reset);

    Command zero = new SimpleCommand("Zero Position", () -> {
      if (motor1 != null) {
        motor1.setSelectedSensorPosition(0);
      }
    });
    zero.setRunWhenDisabled(true);
    SmartDashboard.putData(zero);
  }

  @Override
  public void teleopPeriodic() {
    if (motor1 != null) {
      motor1.config_kP(0, p);
      motor1.config_kI(0, i);
      motor1.config_kD(0, d);
      motor1.config_kF(0, f);
      motor1.config_IntegralZone(0, iZone);
      motor1.configMaxIntegralAccumulator(0, maxIAccum);
      motor1.configMotionCruiseVelocity(maxVel);
      motor1.configMotionAcceleration(maxAccel);
      if (enablePID) {
        motor1.set(controlModeChooser.getSelected(), setpoint);
      } else {
        motor1
            .set(ControlMode.PercentOutput, OIUtils.processDeadzone(joystick.getRawAxis(1), 0.1));
      }
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
      SmartDashboard.putNumber("Target", motor1.getClosedLoopTarget(0));
      SmartDashboard.putNumber("Integral Accumulator", motor1.getIntegralAccumulator());
    }
  }
}
