package org.team1540.base.commandutils.drive;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;
import org.team1540.base.templates.Drive;

public class SimplePidDriveFactory {

  private Drive drive;
  private double maxSetpoint;
  private boolean invertLeftAxis = false;
  private boolean invertRightAxis = false;
  private boolean invertLeftOutput = false;
  private boolean invertRightOutput = false;
  private boolean invertFwdTrigger = false;
  private boolean invertBackTrigger = false;

  public Command createSimplePidDrive(Joystick joystick, int leftAxis, int rightAxis,
      int fwdTrigger, int backTrigger) {
    return new SimplePidDrive(drive, maxSetpoint, joystick, leftAxis, rightAxis, fwdTrigger,
        backTrigger, invertLeftAxis, invertRightAxis, invertLeftOutput, invertRightOutput,
        invertFwdTrigger, invertBackTrigger);
  }
}
