package org.team1540.base.util;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * A simple way to construct an {@link InstantCommand}. <p> To create a {@code SimpleCommand}
 * easily, simply pass it a no-argument lambda containing the code you would like to execute. For
 * example, to create a {@link InstantCommand} that requires the {@code Robot.shifter} {@link
 * Subsystem}, simply write:
 * <pre>
 *   Command shift = new SimpleCommand(() -> Robot.shifter.shift(), Robot.shifter);
 * </pre>
 *
 * Multiple {@code Subystems} can be added onto the end of the constructor to add multiple
 * requirements. <p> This can be used to quickly and easily put a button on the
 * SmartDashboard/Shuffleboard to run some code. Use {@link SmartDashboard#putData(Sendable)} to
 * pass it a newly created instance of this class.
 */
public class SimpleCommand extends InstantCommand {

  private Executable executable;

  /**
   * Creates a new {@code SimpleCommand}.
   *
   * @param action The code to run when the command executes.
   * @param requirements The {@link Subsystem Subsystems} required by the command (if any).
   */
  public SimpleCommand(Executable action, Subsystem... requirements) {
    executable = action;

    for (Subsystem requirement : requirements) {
      requires(requirement); // java_irl
    }
  }

  @Override
  protected void execute() {
    executable.execute();
  }
}
