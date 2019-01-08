package org.team1540.rooster.drive;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.team1540.rooster.wrappers.ChickenController;

class PidDriveConfiguration {

  Subsystem subsystem;
  ChickenController left;
  ChickenController right;
  double maxVel;
  JoystickScaling scaling;
  double maxBrakePct;
  boolean invertLeftBrakeDirection;
  boolean invertRightBrakeDirection;
  double brakingStopZone;
  Joystick joystick;
  int leftAxis;
  boolean invertLeft;
  int rightAxis;
  boolean invertRight;
  int forwardTrigger;
  int backTrigger;
  double deadzone;
  double brakeOverrideThresh;
}
