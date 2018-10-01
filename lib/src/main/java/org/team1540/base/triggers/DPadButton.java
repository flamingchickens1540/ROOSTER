package org.team1540.base.triggers;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import java.util.Objects;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Used to map a button from the D-Pad on a controller to a button so
 * it can be used for triggering commands.
 */
public class DPadButton extends Button {
  /**
   * Enum representing the possible axes of a D-Pad.
   *
   * @deprecated Use {@link org.team1540.base.triggers.DPadAxis}
   */
  @Deprecated
  public enum DPadAxis {
    UP, DOWN, LEFT, RIGHT
  }

  private Joystick stick;
  private int pad;

  private org.team1540.base.triggers.DPadAxis axis;

  /**
   * Constructs a {@link DPadButton}.
   *
   * @deprecated Use {@link #DPadButton(Joystick, int, org.team1540.base.triggers.DPadAxis)}
   *
   * @param stick The joystick with the button.
   * @param pad The ID of the d-pad.
   * @param axis The axis of the button.
   */
  @Deprecated
  public DPadButton(Joystick stick, int pad, DPadAxis axis) {
    super();
    this.stick = stick;
    this.pad = pad;
    switch (axis) {
      case UP:
        this.axis = org.team1540.base.triggers.DPadAxis.UP;
        break;
      case DOWN:
        this.axis = org.team1540.base.triggers.DPadAxis.DOWN;
        break;
      case LEFT:
        this.axis = org.team1540.base.triggers.DPadAxis.LEFT;
        break;
      case RIGHT:
        this.axis = org.team1540.base.triggers.DPadAxis.RIGHT;
        break;
    }
  }

  /**
   * Constructs a {@link DPadButton}.
   *
   * @param stick The joystick with the button.
   * @param pad The ID of the d-pad.
   * @param axis The axis of the button.
   * @throws NullPointerException If {@code stick} or {@code axis} is {@code null}.
   * @throws IllegalArgumentException If {@code pad} is negative.
   */
  public DPadButton(@NotNull Joystick stick, int pad, @NotNull org.team1540.base.triggers.DPadAxis axis) {
    super();
    if (pad < 0) {
      throw new IllegalArgumentException("Pad cannot be negative");
    }

    this.stick = Objects.requireNonNull(stick);
    this.pad = pad;
    this.axis = Objects.requireNonNull(axis);
  }

  @Override
  @Contract(pure = true)
  public boolean get() {
    int pov = stick.getPOV(pad);

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
