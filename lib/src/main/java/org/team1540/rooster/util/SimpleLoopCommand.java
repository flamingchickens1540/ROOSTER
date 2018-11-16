package org.team1540.rooster.util;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

/**
 * A simple way to construct a {@link Command} which executes every tick. <p> To create a {@code
 * SimpleLoopCommand} easily, simply pass it a no-argument lambda containing the code you would like
 * to execute. For example, to create a {@link Command} that requires the {@code Robot.shifter}
 * {@link Subsystem}, simply write:
 * <pre>
 *   Command shift = new SimpleLoopCommand(() -&gt; Robot.shifter.shift(), Robot.shifter);
 * </pre>
 *
 * Multiple {@code Subystems} can be added onto the end of the constructor to add multiple
 * requirements. <p> This can be used to quickly and easily put a button on the
 * SmartDashboard/Shuffleboard to run some code. Use {@link SmartDashboard#putData(Sendable)} to
 * pass it a newly created instance of this class.
 */
public class SimpleLoopCommand extends Command {

  private Executable executable;

  /**
   * Creates a new {@code SimpleLoopCommand}.
   *
   * @param name The name of the commmand. This is required to avoid everything being called
   * SimpleCommand.
   * @param action The code to run when the command executes.
   * @param requirements The {@link Subsystem Subsystems} required by the command (if any).
   * @throws IllegalArgumentException If any parameters are {@code null}.
   */
  public SimpleLoopCommand(@NotNull String name, @NotNull Executable action,
      @NotNull Subsystem... requirements) {
    super(Objects.requireNonNull(name));
    executable = Objects.requireNonNull(action);

    for (Subsystem requirement : requirements) {
      requires(Objects.requireNonNull(requirement));
    }
  }

  @Override
  protected void execute() {
    executable.execute();
  }

  @Override
  protected boolean isFinished() {
    return false;
  }
}
