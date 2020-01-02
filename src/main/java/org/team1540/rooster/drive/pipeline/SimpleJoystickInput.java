package org.team1540.rooster.drive.pipeline;

import edu.wpi.first.wpilibj.Joystick;
import org.team1540.rooster.functional.Input;
import org.team1540.rooster.util.ControlUtils;
import org.team1540.rooster.util.MathUtils;

/**
 * Simple tank-style input from a WPILib {@link Joystick}. The left and right joysticks are used to
 * control the left and right sides of the robot respectively. Additionally, there is an optional
 * "forward axis" and "back axis"; if the forward axis is bound to one trigger on an Xbox controller
 * and the back to the other, the forward trigger will cause the robot to drive straight forward and
 * the back trigger will cause it to drive straight backwards. This {@link Input} creates a {@link
 * TankDriveData} with only the feed-forward fields set on the left and right sides.
 */
public class SimpleJoystickInput implements Input<TankDriveData> {

  private Joystick joystick;
  private int leftAxis;
  private int rightAxis;
  private int fwdAxis;
  private int backAxis; // or "baxis" if you don't have much time
  private boolean invertLeft;
  private boolean invertRight;

  private double deadzone;

  @Override
  public TankDriveData get() {
      double triggerValue;
      if (fwdAxis != -1 && backAxis != -1) {
          triggerValue = ControlUtils.deadzone(joystick.getRawAxis(fwdAxis), deadzone)
              - ControlUtils.deadzone(joystick.getRawAxis(backAxis), deadzone);
      } else {
          triggerValue = 0;
      }
      double leftThrottle = MathUtils.constrain(
          ControlUtils.deadzone(
              MathUtils.negateDoubleIf(invertLeft, joystick.getRawAxis(leftAxis)), deadzone
          ) + triggerValue, 1);
      double rightThrottle = MathUtils.constrain(
          ControlUtils.deadzone(
              MathUtils.negateDoubleIf(invertRight, joystick.getRawAxis(rightAxis)), deadzone
          ) + triggerValue, 1);

      return new TankDriveData().withAdditionalFeedForwards(leftThrottle, rightThrottle);
  }

  /**
   * Creates a new {@code SimpleJoystickInput} with a deadzone of 0.1 and no forward/back axis
   * control.
   *
   * @param joystick The {@link Joystick} to use.
   * @param leftAxis The axis number (as provided to {@link Joystick#getRawAxis(int)} for the left
   * side.
   * @param rightAxis The axis number (as provided to {@link Joystick#getRawAxis(int)} for the right
   * side.
   * @param invertLeft Whether to invert the axis value of the left axis.
   * @param invertRight Whether to invert the axis value of the right axis.
   */
  public SimpleJoystickInput(Joystick joystick, int leftAxis, int rightAxis,
      boolean invertLeft, boolean invertRight) {
    this(joystick, leftAxis, rightAxis, -1, -1, invertLeft,
        invertRight);
  }

  /**
   * Creates a new {@code SimpleJoystickInput} with a deadzone of 0.1.
   *
   * @param joystick The {@link Joystick} to use.
   * @param leftAxis The axis number (as provided to {@link Joystick#getRawAxis(int)} for the left
   * side.
   * @param rightAxis The axis number (as provided to {@link Joystick#getRawAxis(int)} for the right
   * side.
   * @param fwdAxis The axis number (as provided to {@link Joystick#getRawAxis(int)} for the forward
   * axis, or -1 for none.
   * @param backAxis The axis number (as provided to {@link Joystick#getRawAxis(int)} for the back
   * axis, or -1 for none.
   * @param invertLeft Whether to invert the axis value of the left axis.
   * @param invertRight Whether to invert the axis value of the right axis.
   */
  public SimpleJoystickInput(Joystick joystick, int leftAxis, int rightAxis, int fwdAxis,
      int backAxis, boolean invertLeft, boolean invertRight) {
    this(joystick, leftAxis, rightAxis, fwdAxis, backAxis, invertLeft, invertRight,
        0.1);
  }

  /**
   * Creates a new {@code SimpleJoystickInput}.
   *
   * @param joystick The {@link Joystick} to use.
   * @param leftAxis The axis number (as provided to {@link Joystick#getRawAxis(int)} for the left
   * side.
   * @param rightAxis The axis number (as provided to {@link Joystick#getRawAxis(int)} for the right
   * side.
   * @param fwdAxis The axis number (as provided to {@link Joystick#getRawAxis(int)} for the forward
   * axis.
   * @param backAxis The axis number (as provided to {@link Joystick#getRawAxis(int)} for the back
   * axis.
   * @param invertLeft Whether to invert the axis value of the left axis.
   * @param invertRight Whether to invert the axis value of the right axis.
   * @param deadzone The deadzone for the axes (see {@link ControlUtils#deadzone(double, double)}).
   */
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
