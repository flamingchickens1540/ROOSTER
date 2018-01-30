package org.team1540.base.testing.templates.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.team1540.base.adjustables.AdjustableManager;
import org.team1540.base.adjustables.Tunable;
import org.team1540.base.commands.drive.AdvancedPidDrive;
import org.team1540.base.commands.drive.AdvancedPidDriveFactory;
import org.team1540.base.templates.CtreDrive;
import org.team1540.base.wrappers.ChickenController;
import org.team1540.base.wrappers.ChickenTalon;

public class AdvancedPidDriveTestRobot extends IterativeRobot {

  // tunables
  @Tunable("Velocity Setpoint")
  public double setpoint;
  @Tunable("Invert Left Axis")
  public boolean invertLeftAxis;
  @Tunable("Invert Right Axis")
  public boolean invertRightAxis;
  @Tunable("Invert Forward Trigger")
  public boolean invertFwdTrigger;
  @Tunable("Invert Back Trigger")
  public boolean invertBackTrigger;
  @Tunable("Invert Left Output")
  public boolean invertLeftOutput;
  @Tunable("Invert Right Output")
  public boolean invertRightOutput;
  @Tunable("Max Brake Percent")
  public double maxBrakePct;
  @Tunable("Max Brownout Cooldown")
  public double maxBrownoutCooldown;
  @Tunable("Use Brownout Alert")
  public boolean usingBrownoutAlert;
  @Tunable("Closed-Loop Ramp")
  public double closedLoopRamp;

  // internal fields
  private AdvancedPidDrive driveCmd;
  private ChickenTalon lMaster;
  private ChickenTalon lSlave1;
  private ChickenTalon lSlave2;
  private ChickenTalon rMaster;
  private ChickenTalon rSlave1;
  private ChickenTalon rSlave2;
  private Joystick joystick;

  @Override
  public void robotInit() {
    AdjustableManager.getInstance().add(this);

    joystick = new Joystick(0);
    lMaster = new ChickenTalon(1);
    lMaster.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
    lMaster.setBrake(true);
    lMaster.configClosedloopRamp(closedLoopRamp);

    lSlave1 = new ChickenTalon(2);
    lSlave1.set(ControlMode.Follower, lMaster.getDeviceID());
    lSlave1.configClosedloopRamp(closedLoopRamp);
    lSlave1.setBrake(true);

    lSlave2 = new ChickenTalon(3);
    lSlave2.set(ControlMode.Follower, lMaster.getDeviceID());
    lSlave2.configClosedloopRamp(closedLoopRamp);
    lSlave2.setBrake(true);

    rMaster = new ChickenTalon(4);
    rMaster.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
    rMaster.setBrake(true);
    rMaster.configClosedloopRamp(closedLoopRamp);

    rSlave1 = new ChickenTalon(5);
    rSlave1.set(ControlMode.Follower, rMaster.getDeviceID());
    rSlave1.configClosedloopRamp(closedLoopRamp);
    rSlave1.setBrake(true);

    rSlave2 = new ChickenTalon(6);
    rSlave2.set(ControlMode.Follower, rMaster.getDeviceID());
    rSlave2.configClosedloopRamp(closedLoopRamp);
    rSlave2.setBrake(true);

    driveCmd = new AdvancedPidDriveFactory()
        .setDrive(new DriveTrain())
        .setMaxSetpoint(setpoint)
        .setJoystick(joystick)
        .setLeftAxis(1)
        .setRightAxis(5)
        .setFwdTrigger(3)
        .setBackTrigger(2)
        .setInvertLeftAxis(invertLeftAxis)
        .setInvertRightAxis(invertRightAxis)
        .setInvertFwdTrigger(invertFwdTrigger)
        .setInvertBackTrigger(invertBackTrigger)
        .setInvertLeftOutput(invertLeftOutput)
        .setInvertRightOutput(invertRightOutput)
        .setMaxBrakePct(maxBrakePct)
        .setMaxBrownoutCooldown(maxBrownoutCooldown)
        .setUsingBrownoutAlert(usingBrownoutAlert)
        .createAdvancedPidDrive();
  }

  @Override
  public void robotPeriodic() {
    Scheduler.getInstance().run();

    driveCmd.setMaxSetpoint(setpoint);
    driveCmd.setJoystick(joystick);
    driveCmd.setLeftAxis(1);
    driveCmd.setRightAxis(5);
    driveCmd.setFwdTrigger(3);
    driveCmd.setBackTrigger(2);
    driveCmd.setInvertLeftAxis(invertLeftAxis);
    driveCmd.setInvertRightAxis(invertRightAxis);
    driveCmd.setInvertFwdTrigger(invertFwdTrigger);
    driveCmd.setInvertBackTrigger(invertBackTrigger);
    driveCmd.setInvertLeftOutput(invertLeftOutput);
    driveCmd.setInvertRightOutput(invertRightOutput);
    driveCmd.setMaxBrakePct(maxBrakePct);
    driveCmd.setMaxBrownoutCooldown(maxBrownoutCooldown);
    driveCmd.setUsingBrownoutAlert(usingBrownoutAlert);

    lMaster.configClosedloopRamp(closedLoopRamp);
    lSlave1.configClosedloopRamp(closedLoopRamp);
    lSlave2.configClosedloopRamp(closedLoopRamp);
    rMaster.configClosedloopRamp(closedLoopRamp);
    rSlave1.configClosedloopRamp(closedLoopRamp);
    rSlave2.configClosedloopRamp(closedLoopRamp);
  }

  @Override
  public void teleopPeriodic() {
    if (!driveCmd.isRunning()) {
      driveCmd.start();
    }
  }

  private class DriveTrain extends Subsystem implements CtreDrive {

    @Override
    public ChickenController getLeftMaster() {
      return lMaster;
    }

    @Override
    public ChickenController getRightMaster() {
      return rMaster;
    }

    @Override
    protected void initDefaultCommand() {
    }

    @Override
    public Subsystem getAttachedSubsystem() {
      return this;
    }
  }
}
