package org.team1540.base.util;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * Provides a convenience constructor for creating a {@link CommandGroup} of commands solely running
 * in parellel.
 *
 * @see SequentialCommandGroup
 */
public class ParallelCommandGroup extends CommandGroup {

  /**
   * Create a new {@code ParallelCommandGroup} that executes the given commands in parallel. Each
   * {@link Command} provided will be added using {@link CommandGroup#addParallel(Command)}.
   *
   * @param commands The commands to execute.
   */
  public ParallelCommandGroup(Command... commands) {
    for (Command command : commands) {
      addParallel(command);
    }
  }
}
