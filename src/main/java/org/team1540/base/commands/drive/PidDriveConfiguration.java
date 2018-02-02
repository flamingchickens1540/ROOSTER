package org.team1540.base.commands.drive;

import edu.wpi.first.wpilibj.Joystick;

class PidDriveConfiguration {

  double maxSetpoint;
  Joystick joystick;
  int leftAxis;
  int rightAxis;
  int fwdTrigger;
  int backTrigger;
  boolean invertLeftAxis;
  boolean invertRightAxis;
  boolean invertLeftOutput;
  boolean invertRightOutput;
  boolean invertFwdTrigger;
  boolean invertBackTrigger;

}
