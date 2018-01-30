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
  private final PowerDistributionPanel pdp;

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

  private double deadzone;

  private boolean usingBrownoutAlert;
  private double maxBrownoutCooldown;

  // algo fields
  private boolean inBrownoutCooldown;
  private Timer brownoutCooldownTimer;

  AdvancedPidDrive(Configuration config) {
    this.drive = config.drive;
    this.maxSetpoint = config.maxSetpoint;
    this.joystick = config.joystick;
    this.leftAxis = config.leftAxis;
    this.rightAxis = config.rightAxis;
    this.fwdTrigger = config.fwdTrigger;
    this.backTrigger = config.backTrigger;
    this.invertLeftAxis = config.invertLeftAxis;
    this.invertRightAxis = config.invertRightAxis;
    this.invertLeftOutput = config.invertLeftOutput;
    this.invertRightOutput = config.invertRightOutput;
    this.invertFwdTrigger = config.invertFwdTrigger;
    this.invertBackTrigger = config.invertBackTrigger;
    this.maxBrakePct = config.maxBrakePct;
    this.deadzone = config.deadzone;
    this.usingBrownoutAlert = config.usingBrownoutAlert;
    this.maxBrownoutCooldown = config.maxBrownoutCooldown;

    requires(drive.getAttachedSubsystem());

    rumbleCommand = VibrationManager.getVibrationCommand(joystick, maxBrownoutCooldown, 1);
    pdp = new PowerDistributionPanel();

    brownoutCooldownTimer = new Timer();
    inBrownoutCooldown = false;
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

  static class Configuration extends PidDriveConfiguration {

    CtreDrive drive;
    double maxBrakePct;
    double deadzone;
    boolean usingBrownoutAlert;
    double maxBrownoutCooldown;

  }

  public CtreDrive getDrive() {
    return drive;
  }

  public void setDrive(CtreDrive drive) {
    this.drive = drive;
  }

  public double getMaxSetpoint() {
    return maxSetpoint;
  }

  public void setMaxSetpoint(double maxSetpoint) {
    this.maxSetpoint = maxSetpoint;
  }

  public Joystick getJoystick() {
    return joystick;
  }

  public void setJoystick(Joystick joystick) {
    this.joystick = joystick;
  }

  public int getLeftAxis() {
    return leftAxis;
  }

  public void setLeftAxis(int leftAxis) {
    this.leftAxis = leftAxis;
  }

  public int getRightAxis() {
    return rightAxis;
  }

  public void setRightAxis(int rightAxis) {
    this.rightAxis = rightAxis;
  }

  public int getFwdTrigger() {
    return fwdTrigger;
  }

  public void setFwdTrigger(int fwdTrigger) {
    this.fwdTrigger = fwdTrigger;
  }

  public int getBackTrigger() {
    return backTrigger;
  }

  public void setBackTrigger(int backTrigger) {
    this.backTrigger = backTrigger;
  }

  public boolean isInvertLeftAxis() {
    return invertLeftAxis;
  }

  public void setInvertLeftAxis(boolean invertLeftAxis) {
    this.invertLeftAxis = invertLeftAxis;
  }

  public boolean isInvertRightAxis() {
    return invertRightAxis;
  }

  public void setInvertRightAxis(boolean invertRightAxis) {
    this.invertRightAxis = invertRightAxis;
  }

  public boolean isInvertLeftOutput() {
    return invertLeftOutput;
  }

  public void setInvertLeftOutput(boolean invertLeftOutput) {
    this.invertLeftOutput = invertLeftOutput;
  }

  public boolean isInvertRightOutput() {
    return invertRightOutput;
  }

  public void setInvertRightOutput(boolean invertRightOutput) {
    this.invertRightOutput = invertRightOutput;
  }

  public boolean isInvertFwdTrigger() {
    return invertFwdTrigger;
  }

  public void setInvertFwdTrigger(boolean invertFwdTrigger) {
    this.invertFwdTrigger = invertFwdTrigger;
  }

  public boolean isInvertBackTrigger() {
    return invertBackTrigger;
  }

  public void setInvertBackTrigger(boolean invertBackTrigger) {
    this.invertBackTrigger = invertBackTrigger;
  }

  public double getMaxBrakePct() {
    return maxBrakePct;
  }

  public void setMaxBrakePct(double maxBrakePct) {
    this.maxBrakePct = maxBrakePct;
  }

  public double getDeadzone() {
    return deadzone;
  }

  public void setDeadzone(double deadzone) {
    this.deadzone = deadzone;
  }

  public boolean isUsingBrownoutAlert() {
    return usingBrownoutAlert;
  }

  public void setUsingBrownoutAlert(boolean usingBrownoutAlert) {
    this.usingBrownoutAlert = usingBrownoutAlert;
  }

  public double getMaxBrownoutCooldown() {
    return maxBrownoutCooldown;
  }

  public void setMaxBrownoutCooldown(double maxBrownoutCooldown) {
    this.maxBrownoutCooldown = maxBrownoutCooldown;
  }
}
