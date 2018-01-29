package org.team1540.base.commands.drive;

import static java.lang.Math.abs;
import static org.team1540.base.Utilities.constrain;
import static org.team1540.base.Utilities.processAxisDeadzone;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import org.team1540.base.templates.CtreDrive;
import org.team1540.base.triggers.VibrationManager;
import org.team1540.base.wrappers.ChickenController;

public class AdvancedPidDrive extends Command {

  // pdp object for easy access
  private final PowerDistributionPanel pdp = new PowerDistributionPanel();

  // rumble command
  private final Command rumbleCommand;

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

  private boolean usingBrownoutAlert;
  private double maxBrownoutCooldown;

  // algo fields
  private boolean inBrownoutCooldown = false;
  private Timer brownoutCooldownTimer = new Timer();

  public AdvancedPidDrive(CtreDrive drive, double maxSetpoint,
      Joystick joystick, int leftAxis, int rightAxis, int fwdTrigger, int backTrigger,
      boolean invertLeftAxis, boolean invertRightAxis, boolean invertLeftOutput,
      boolean invertRightOutput, boolean invertFwdTrigger, boolean invertBackTrigger,
      double maxBrakePct, boolean minMotorOutput, double deadzone, boolean usingBrownoutAlert,
      double maxBrownoutCooldown) {
    this.drive = drive;
    this.maxSetpoint = maxSetpoint;
    this.joystick = joystick;
    this.leftAxis = leftAxis;
    this.rightAxis = rightAxis;
    this.fwdTrigger = fwdTrigger;
    this.backTrigger = backTrigger;
    this.invertLeftAxis = invertLeftAxis;
    this.invertRightAxis = invertRightAxis;
    this.invertLeftOutput = invertLeftOutput;
    this.invertRightOutput = invertRightOutput;
    this.invertFwdTrigger = invertFwdTrigger;
    this.invertBackTrigger = invertBackTrigger;
    this.maxBrakePct = maxBrakePct;
    this.minMotorOutput = minMotorOutput;
    this.deadzone = deadzone;
    this.usingBrownoutAlert = usingBrownoutAlert;
    this.maxBrownoutCooldown = maxBrownoutCooldown;
    rumbleCommand = VibrationManager.getVibrationCommand(joystick, maxBrownoutCooldown, 1);
  }

  @Override
  protected void execute() {

    if (!inBrownoutCooldown) {
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

      processPeakOutput(left);
      processPeakOutput(right);

      if (usingBrownoutAlert) {
        // check if the RIO is browning out. If so, set the motors to neutral and vibrate the controller
        // until the joysticks are released / some time has passed
        if (pdp.getVoltage() < 6.8) {
          // we're browning out (first-stage brownout triggers at 6.8 volts per
          // https://wpilib.screenstepslive.com/s/4485/m/cs_hardware/l/289498-roborio-brownout-and-understanding-current-draw#Stage1-OutputDisable)
          brownoutCooldownTimer.reset();
          brownoutCooldownTimer.start();
          inBrownoutCooldown = true;
          left.set(ControlMode.PercentOutput, 0);
          right.set(ControlMode.PercentOutput, 0);
          rumbleCommand.start();
          return;
        }
      }

      left.set(ControlMode.Velocity, constrain(leftSetpoint, 1));
      right.set(ControlMode.Velocity, constrain(rightSetpoint, 1));
    } else {
      if (brownoutCooldownTimer.hasPeriodPassed(maxBrownoutCooldown)
          || (processAxisDeadzone(joystick.getRawAxis(leftAxis), deadzone) == 0
          && processAxisDeadzone(joystick.getRawAxis(rightAxis), deadzone) == 0
          && processAxisDeadzone(joystick.getRawAxis(fwdTrigger), deadzone) == 0
          && processAxisDeadzone(joystick.getRawAxis(backTrigger), deadzone) == 0)
          ) {
        brownoutCooldownTimer.stop();
        inBrownoutCooldown = false;
        rumbleCommand.cancel();
      }
    }
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
