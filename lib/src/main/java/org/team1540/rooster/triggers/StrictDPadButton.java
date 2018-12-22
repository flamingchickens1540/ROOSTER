package org.team1540.rooster.triggers;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.buttons.Button;
import java.util.Objects;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Version of {@link DPadButton} that ignores diagonal inputs.
 */
public class StrictDPadButton extends Button {

  private @NotNull GenericHID stick;
  private int pad;

  private DPadAxis axis;

  /**
   * Constructs a {@link StrictDPadButton}.
   *
   * @param stick The joystick with the button.
   * @param pad The ID of the d-pad.
   * @param axis The axis of the button.
   * @throws NullPointerException If {@code stick} or {@code axis} is {@code null}.
   * @throws IllegalArgumentException If {@code pad} is negative.
   */
  public StrictDPadButton(@NotNull GenericHID stick, int pad,
      @NotNull org.team1540.rooster.triggers.DPadAxis axis) {
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
