package org.team1540.base.commandutils.drive;

import static java.lang.Math.abs;
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

    // figure out if there is any motor movement
    processPeakOutput(left);
    processPeakOutput(right);

    left.set(ControlMode.Velocity, constrain(leftSetpoint, 1));
    right.set(ControlMode.Velocity, constrain(rightSetpoint, 1));
  }

  private void processPeakOutput(ChickenController controller) {
    if (abs(controller.getSelectedSensorVelocity()) < abs(maxSetpoint * deadzone)) {
      /* We are moving at a non-negligible rate, so don't allow the motors to run full backwards,
      that stalls the motors and eats power */

      // figure out which way is "braking"
      boolean goingForward = controller.getSelectedSensorVelocity() > 0;

      // if "braking" is running motors forward limit the forward throttle; if not, limit the back
      controller.configPeakOutputForward(goingForward ? 1.00 : maxBrakePct);
      controller.configPeakOutputReverse(goingForward ? maxBrakePct : 1.00);
    } else {
      /* Since we aren't going to be stalling the motors for an extended period of time as velocity
      goes to zero, the motors can go to maximum in either direction */
      controller.configPeakOutputForward(1.00);
      controller.configPeakOutputReverse(1.00);
    }
  }

  @Override
  protected void end() {
    // reset peak output
    drive.getLeftMaster().configPeakOutputForward(1.00);
    drive.getLeftMaster().configPeakOutputReverse(1.00);
    drive.getRightMaster().configPeakOutputForward(1.00);
    drive.getRightMaster().configPeakOutputReverse(1.00);

  }

  @Override
  protected boolean isFinished() {
    return false;
  }
}
