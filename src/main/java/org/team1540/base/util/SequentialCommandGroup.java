package org.team1540.base.util;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * Provides a convenience constructor for creating a {@link CommandGroup} of commands solely running
 * in sequence.
 *
 * @see ParallelCommandGroup
 */
public class SequentialCommandGroup extends CommandGroup {

  /**
   * Create a new {@code SequentialCommandGroup} that executes the given commands in the order they
   * were given. Each {@link Command} provided will be added using {@link
   * CommandGroup#addSequential(Command)}.
   *
   * @param commands The commands to execute.
   */
  public SequentialCommandGroup(Command... commands) {
    for (Command command : commands) {
      addSequential(command);
    }
  }
}
