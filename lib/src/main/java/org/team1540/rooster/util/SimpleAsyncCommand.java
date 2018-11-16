package org.team1540.rooster.util;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

/**
 * A simple way to construct an {@link AsyncCommand}. <p> To create a {@code SimpleCommand} easily,
 * simply pass it a no-argument lambda containing the code you would like to execute. For example,
 * to create a {@link AsyncCommand} that loops every 30 milliseconds and  requires the {@code
 * Robot.shifter} {@link Subsystem}, simply write:
 * <pre>
 *   Command shift = new SimpleAsyncCommand("Shift", 30, () -&gt; Robot.shifter.shift(), Robot.shifter);
 * </pre>
 * Multiple {@code Subystems} can be added onto the end of the constructor to add multiple
 * requirements. <p> This can be used to quickly and easily put a button on the
 * SmartDashboard/Shuffleboard to run some code. Use {@link SmartDashboard#putData(Sendable)} to
 * pass it a newly created instance of this class.
 *
 * @see SimpleAsyncCommand
 * @see SimpleLoopCommand
 * @see Command
 */
public class SimpleAsyncCommand extends AsyncCommand {

  private final Executable executable;

  /**
   * Constructs a new {@code SimpleAsyncCommand} with a preset periodic interval that runs the
   * provided {@link Executable}.
   *
   * @param interval The interval between calls to the {@link Executable}, in milliseconds.
   */
  public SimpleAsyncCommand(@NotNull String name, long interval, @NotNull Executable executable,
      @NotNull Subsystem... requirements) {
    super(interval);
    this.setName(Objects.requireNonNull(name));

    this.executable = Objects.requireNonNull(executable);
    for (Subsystem requirement : requirements) {
      requires(Objects.requireNonNull(requirement));
    }
  }

  @Override
  protected void runPeriodic() {
    executable.execute();
  }
}
