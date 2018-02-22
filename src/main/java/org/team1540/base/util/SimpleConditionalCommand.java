package org.team1540.base.util;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.ConditionalCommand;
import java.util.function.BooleanSupplier;


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
   * @param onTrue The command to execute when the supplier supplies a true value.
   */
  public SimpleConditionalCommand(BooleanSupplier condition, Command onTrue) {
    this(condition, onTrue, null);
  }

  /**
   * Constructs a new {@code SimpleConditionalCommand} that uses the provided {@link
   * BooleanSupplier} to determine which command to execute.
   *
   * @param condition The {@code BooleanSupplier} to call for the condition.
   * @param onTrue The command to execute when the supplier supplies a true value.
   * @param onFalse The command to execute when the supplier supplies a false value.
   */
  public SimpleConditionalCommand(BooleanSupplier condition, Command onTrue, Command onFalse) {
    super(onTrue, onFalse);
    this.condition = condition;
  }

  @Override
  public boolean condition() {
    return condition.getAsBoolean();
  }

  public BooleanSupplier getCondition() {
    return condition;
  }

  public void setCondition(BooleanSupplier condition) {
    this.condition = condition;
  }
}
