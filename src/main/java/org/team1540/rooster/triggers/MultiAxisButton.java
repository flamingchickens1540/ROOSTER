package org.team1540.rooster.triggers;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A button based on a set of joystick axes.
 * This can be used to emulate a button that is triggered by the movement of any specified axis.
 */
public class MultiAxisButton extends Button {

  private GenericHID stick;
  private double threshold;
  private int[] axes;

  /**
   * Constructs an {@code AxisButton}.
   *
   * @param stick The axes's joystick
   * @param threshold The threshold for the button to be triggered (if any axes are over the threshold)
   * @throws NullPointerException If {@code stick} is {@code null}.
   */
  public MultiAxisButton(@NotNull GenericHID stick, double threshold) {
    this(stick, threshold, new int[] {0, 1, 2, 3, 4, 5});
  }

  /**
   * Constructs an {@code AxisButton}.
   *
   * @param stick The axes's joystick
   * @param axes The axes to use as a button
   * @param threshold The threshold for the button to be triggered (if any axes are over the threshold)
   * @throws NullPointerException If {@code stick} is {@code null}.
   */
  public MultiAxisButton(@NotNull GenericHID stick, double threshold, int[] axes) {
    this.stick = Objects.requireNonNull(stick);
    this.threshold = threshold;
    this.axes = axes;
  }

  @Override
  public boolean get() {
    for (int axis : axes) {
      if (Math.abs(stick.getRawAxis(axis)) >= Math.abs(threshold)) {
        return true;
      }
    }
    return false;
  }
}
