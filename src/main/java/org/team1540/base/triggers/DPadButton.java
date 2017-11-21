package org.team1540.base.triggers;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;

/**
 * Used to map a button from the D-Pad on a controller to a button so
 * it can be used for triggering commands.
 */
public class DPadButton extends Button {
  /**
   * Enum representing the possible axes of a D-Pad.
   */
  public enum DPadAxis {
    UP, DOWN, LEFT, RIGHT
  }

  private Joystick stick;
  private int pad;

  private DPadAxis axis;

  /**
   * Constructs a {@link DPadButton}.
   *
   * @param stick The joystick with the button.
   * @param pad The ID of the d-pad.
   * @param axis The axis of the button.
   */
  public DPadButton(Joystick stick, int pad, DPadAxis axis) {
    super();
    this.stick = stick;
    this.pad = pad;
    this.axis = axis;
  }

  @Override
  public boolean get() {
    int pov = stick.getPOV(pad);

    return process(pov, axis);
  }

  /**
   * Helper function so that it can be properly unit-tested. Should not be called directly.
   */
  public static boolean process(int pov, DPadAxis axis) {
    switch (axis) {
      case UP:
        return pov == 315 || pov == 0 || pov == 45;
      case DOWN:
        return pov == 135 || pov == 180 || pov == 225;
      case LEFT:
        return pov == 225 || pov == 270 || pov == 315;
      case RIGHT:
        return pov == 45 || pov == 90 || pov == 135;
      default:
        return false;
    }
  }
}
