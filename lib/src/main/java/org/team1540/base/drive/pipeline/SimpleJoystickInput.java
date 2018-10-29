package org.team1540.base.drive.pipeline;

import edu.wpi.first.wpilibj.Joystick;
import java.util.OptionalDouble;
import org.team1540.base.Utilities;

public class SimpleJoystickInput implements Input<TankDriveData> {

  private Joystick joystick;
  private int leftAxis;
  private int rightAxis;
  private int fwdAxis;
  private int backAxis; // or "baxis" if you don't have much time
  private boolean invertLeft;
  private boolean invertRight;

  private double deadzone = 0.1;

  @Override
  public TankDriveData get() {
    double triggerValue = Utilities.processDeadzone(joystick.getRawAxis(fwdAxis), deadzone)
        - Utilities.processDeadzone(joystick.getRawAxis(backAxis), deadzone);
    double leftThrottle = Utilities.constrain(
        Utilities.processDeadzone(Utilities.invertIf(invertLeft, joystick.getRawAxis(leftAxis)),
            deadzone) + triggerValue,
        1
    );
    double rightThrottle = Utilities.constrain(
        Utilities.processDeadzone(Utilities.invertIf(invertRight, joystick.getRawAxis(rightAxis)),
            deadzone) + triggerValue,
        1
    );

    return new TankDriveData(
        new DriveData(
            OptionalDouble.empty(),
            OptionalDouble.empty(),
            OptionalDouble.empty(),
            OptionalDouble.of(leftThrottle)
        ),
        new DriveData(
            OptionalDouble.empty(),
            OptionalDouble.empty(),
            OptionalDouble.empty(),
            OptionalDouble.of(rightThrottle)
        ),
        OptionalDouble.empty(),
        OptionalDouble.empty()
    );
  }

  public SimpleJoystickInput(Joystick joystick, int leftAxis, int rightAxis, int fwdAxis,
      int backAxis, boolean invertLeft, boolean invertRight) {
    this(joystick, leftAxis, rightAxis, fwdAxis, backAxis, invertLeft, invertRight,
        0.1);
  }

  public SimpleJoystickInput(Joystick joystick, int leftAxis, int rightAxis, int fwdAxis,
      int backAxis, boolean invertLeft, boolean invertRight, double deadzone) {
    this.joystick = joystick;
    this.leftAxis = leftAxis;
    this.rightAxis = rightAxis;
    this.fwdAxis = fwdAxis;
    this.backAxis = backAxis;
    this.invertLeft = invertLeft;
    this.invertRight = invertRight;
    this.deadzone = deadzone;
  }
}
