package org.team1540.base.commandutils.drive;

import static org.team1540.base.Utilities.constrain;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;
import org.team1540.base.templates.Drive;

class SimplePidDrive extends Command {

  private final Drive drive;
  private final double maxSetpoint;
  private final Joystick joystick;
  private final int leftAxis;
  private final int rightAxis;
  private final int fwdTrigger;
  private final int backTrigger;
  private final boolean invertLeftAxis;
  private final boolean invertRightAxis;
  private final boolean invertLeftOutput;
  private final boolean invertRightOutput;
  private final boolean invertFwdTrigger;
  private final boolean invertBackTrigger;

  public SimplePidDrive(Drive drive, double maxSetpoint, Joystick joystick, int leftAxis,
      int rightAxis, int fwdTrigger, int backTrigger, boolean invertLeftAxis,
      boolean invertRightAxis, boolean invertLeftOutput, boolean invertRightOutput,
      boolean invertFwdTrigger, boolean invertBackTrigger) {
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
  }

  @Override
  protected void execute() {
    double leftPct = joystick.getRawAxis(leftAxis) * (invertLeftAxis ? -1 : 1);
    double rightPct = joystick.getRawAxis(rightAxis) * (invertRightAxis ? -1 : 1);

    double forwardAdd = joystick.getRawAxis(fwdTrigger) * (invertFwdTrigger ? -1 : 1);
    double backAdd = joystick.getRawAxis(backTrigger) * (invertBackTrigger ? -1 : 1);

    double leftOutput = (leftPct + forwardAdd - backAdd) * (invertLeftOutput ? -1 : 1);
    double rightOutput = (rightPct + forwardAdd - backAdd) * (invertRightOutput ? -1 : 1);

    drive.setLeftVelocity(constrain(leftOutput, 1));
    drive.setRightVelocity(constrain(rightOutput, 1));
  }

  @Override
  protected boolean isFinished() {
    return false;
  }
}
