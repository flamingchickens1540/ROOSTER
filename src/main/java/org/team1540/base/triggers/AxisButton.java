package org.team1540.base.triggers;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;

/**
 * A button based on a joystick axis. This can be used to emulate a button using a trigger or
 * joystick.
 */
public class AxisButton extends Button {
  private Joystick stick;
  private double threshold;
  private int axis;

  /**
   * Constructs an {@code AxisButton}.
   *
   * @param stick The axis's joystick
   * @param axis The axis to use as a button
   * @param threshold The threshold for the button to be triggered
   */
  public AxisButton(Joystick stick, double threshold, int axis) {
    this.stick = stick;
    this.threshold = threshold;
    this.axis = axis;
  }

  @Override
  public boolean get() {
    return (Math.abs(stick.getRawAxis(axis)) >= Math.abs(threshold)
        && Math.signum(stick.getRawAxis(axis)) == Math.signum(threshold));
  }
}
