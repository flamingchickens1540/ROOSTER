package org.team1540.base.triggers;

import edu.wpi.first.wpilibj.buttons.Button;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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
   * @throws NullPointerException If {@code supplier} is {@code null}.
   */
  public SimpleButton(@NotNull BooleanSupplier supplier) {
    this.supplier = Objects.requireNonNull(supplier);
  }

  @Override
  @Contract(pure = true)
  public boolean get() {
    return supplier.getAsBoolean();
  }

  @NotNull
  @Contract(pure = true)
  public BooleanSupplier getSupplier() {
    return supplier;
  }

  public void setSupplier(@NotNull BooleanSupplier supplier) {
    this.supplier = supplier;
  }
}
