package org.team1540.base.util;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.ConditionalCommand;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * A simple {@link ConditionalCommand} that can be constructed with a lambda method or function
 * reference that supplies a {@code boolean}.
 */
public class SimpleConditionalCommand extends ConditionalCommand {

  private BooleanSupplier condition;

  /**
   * Constructs a new {@code SimpleConditionalCommand} that uses the provided {@link
   * BooleanSupplier} to determine whether to execute the provided {@link Command}.
   *
   * @param condition The {@code BooleanSupplier} to call for the condition.
   * @param onTrue The command to execute when the supplier supplies a true value, or {@code null}
   * if no command should be executed.
   * @throws NullPointerException If {@code condition} is {@code null}.
   */
  public SimpleConditionalCommand(@NotNull BooleanSupplier condition, @Nullable Command onTrue) {
    this(condition, onTrue, null);
  }

  /**
   * Constructs a new {@code SimpleConditionalCommand} that uses the provided {@link
   * BooleanSupplier} to determine which command to execute.
   *
   * @param condition The {@code BooleanSupplier} to call for the condition.
   * @param onTrue The command to execute when the supplier supplies a true value, or {@code null} *
   * if no command should be executed.
   * @param onFalse The command to execute when the supplier supplies a false value, or {@code null}
   * if no command should be executed.
   * @throws NullPointerException If {@code condition} is {@code null}.
   */
  public SimpleConditionalCommand(@NotNull BooleanSupplier condition, @Nullable Command onTrue,
      @Nullable Command onFalse) {
    super(onTrue, onFalse);
    this.condition = Objects.requireNonNull(condition);
  }

  @Override
  public boolean condition() {
    return condition.getAsBoolean();
  }

  @NotNull
  public BooleanSupplier getCondition() {
    return condition;
  }

  public void setCondition(@NotNull BooleanSupplier condition) {
    this.condition = condition;
  }
}
