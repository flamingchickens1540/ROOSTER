package org.team1540.base.drive.pipeline;

import edu.wpi.first.wpilibj.Joystick;
import java.util.OptionalDouble;
import java.util.function.Supplier;
import org.team1540.base.Utilities;

public class TankJoystickInput implements Supplier<TankDriveData> {

  private Joystick joystick;
  private int leftAxis;
  private int rightAxis;
  private int fwdAxis;
  private int backAxis; // or "baxis" if you don't have much time
  private boolean invertLeft;
  private boolean invertRight;

  private double maxVelocity;
  private double deadzone = 0.1;

  @Override
  public TankDriveData get() {
    double triggerValue = Utilities.processDeadzone(joystick.getRawAxis(fwdAxis), deadzone)
        - Utilities.processDeadzone(joystick.getRawAxis(backAxis), deadzone);
    double leftThrottle = Utilities
        .processDeadzone(Utilities.invertIf(invertLeft, joystick.getRawAxis(leftAxis)), deadzone);
    double rightThrottle = Utilities
        .processDeadzone(Utilities.invertIf(invertRight, joystick.getRawAxis(rightAxis)), deadzone);

    return new TankDriveData(
        new DriveData(OptionalDouble.of(leftThrottle * maxVelocity)),
        new DriveData(OptionalDouble.of(rightThrottle * maxVelocity)),
        OptionalDouble.empty(),
        OptionalDouble.empty()
    );
  }
}
