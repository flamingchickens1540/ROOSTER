package org.team1540.base.triggers;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;

/**
 * Version of {@link DPadButton} that ignores diagonal inputs.
 */
public class StrictDPadButton extends Button {

  private Joystick stick;
  private int pad;

  private DPadAxis axis;

  /**
   * Constructs a {@link StrictDPadButton}.
   *
   * @param stick The joystick with the button.
   * @param pad The ID of the d-pad.
   * @param axis The axis of the button.
   */
  public StrictDPadButton(Joystick stick, int pad, DPadAxis axis) {
    super();
    this.stick = stick;
    this.pad = pad;
    this.axis = axis;
  }

  @Override
  public boolean get() {
    int pov = stick.getPOV(pad);

    switch (axis) {
      case UP:
        return pov == 0;
      case DOWN:
        return pov == 180;
      case LEFT:
        return pov == 270;
      case RIGHT:
        return pov == 90;
      default:
        return false;
    }
  }
}
