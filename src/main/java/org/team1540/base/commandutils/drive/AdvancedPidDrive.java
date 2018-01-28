package org.team1540.base.commandutils.drive;

import static org.team1540.base.Utilities.constrain;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;
import org.team1540.base.templates.CtreDrive;
import org.team1540.base.wrappers.ChickenController;

public class AdvancedPidDrive extends Command {

  // settings
  private CtreDrive drive;
  private double maxSetpoint;
  private Joystick joystick;

  private int leftAxis;
  private int rightAxis;
  private int fwdTrigger;
  private int backTrigger;

  private boolean invertLeftAxis;
  private boolean invertRightAxis;
  private boolean invertLeftOutput;
  private boolean invertRightOutput;
  private boolean invertFwdTrigger;
  private boolean invertBackTrigger;

  private double maxBrakePct;
  private boolean minMotorOutput;

  private double deadzone;


  @Override
  protected void execute() {

    // find the setpoint taking into account triggers and inversions
    double forwardAdd = joystick.getRawAxis(fwdTrigger) * (invertFwdTrigger ? -1 : 1);
    double backAdd = joystick.getRawAxis(backTrigger) * (invertBackTrigger ? -1 : 1);
    double triggers = forwardAdd - backAdd;

    double leftSetpoint = constrain(
        (joystick.getRawAxis(leftAxis) * (invertLeftAxis ? -1 : 1)
            + triggers) * (invertLeftOutput ? -1 : 1),
        1);
    double rightSetpoint = constrain(
        (joystick.getRawAxis(rightAxis) * (invertRightAxis ? -1 : 1)
            + triggers) * (invertRightOutput ? -1 : 1),
        1);

    // store these in variables for easy acess
    ChickenController left = drive.getLeftMaster();
    ChickenController right = drive.getRightMaster();

    // prevent backdriving above a configured brake amount

    // first figure out which way is "backdriving"
    boolean leftGoingForward = left.getSelectedSensorVelocity() > (maxSetpoint * deadzone);
    boolean rightGoingForward = right.getSelectedSensorVelocity() > (maxSetpoint * deadzone);

    // limit motor output
    left.configPeakOutputForward(leftGoingForward ? 1.00 : maxBrakePct);
    left.configPeakOutputReverse(leftGoingForward ? maxBrakePct : 1.00);
    right.configPeakOutputForward(rightGoingForward ? 1.00 : maxBrakePct);
    right.configPeakOutputReverse(rightGoingForward ? maxBrakePct : 1.00);

    left.set(ControlMode.Velocity, constrain(leftSetpoint, 1));
    right.set(ControlMode.Velocity, constrain(rightSetpoint, 1));
  }

  @Override
  protected boolean isFinished() {
    return false;
  }
}
