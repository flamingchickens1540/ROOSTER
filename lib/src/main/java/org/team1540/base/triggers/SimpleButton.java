package org.team1540.base.triggers;

import edu.wpi.first.wpilibj.buttons.Button;
import java.util.function.BooleanSupplier;

/**
 * A simple {@link Button} that can be constructed with a lambda method or function reference that
 * supplies a {@code boolean}.
 */
public class SimpleButton extends Button {

  private BooleanSupplier supplier;

  /**
   * Constructs a new {@code SimpleButton} that uses the provided {@link BooleanSupplier} to
   * determine its state.
   *
   * @param supplier The {@code BooleanSupplier} to call for the button's state.
   */
  public SimpleButton(BooleanSupplier supplier) {
    this.supplier = supplier;
  }

  @Override
  public boolean get() {
    return supplier.getAsBoolean();
  }

  public BooleanSupplier getSupplier() {
    return supplier;
  }

  public void setSupplier(BooleanSupplier supplier) {
    this.supplier = supplier;
  }
}
