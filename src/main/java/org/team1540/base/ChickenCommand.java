package org.team1540.base;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.IllegalUseOfCommandException;
import edu.wpi.first.wpilibj.command.Subsystem;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class ChickenCommand extends Command implements Comparable<ChickenCommand> {

  public ChickenCommand() {
  }

  public ChickenCommand(String name) {
    super(name);
  }

  public ChickenCommand(double timeout) {
    super(timeout);
  }

  public ChickenCommand(String name, double timeout) {
    super(name, timeout);
  }

  /**
   * The subsystems used by this command. Used for power management. Should be overridden as
   * necessary.
   */
  private final Set<ChickenSubsystem> usedSubsystems = new LinkedHashSet<>(0);

  private double priority = 0.0;

  /**
   * Adds a power-managed subsystem to this subsystem's requirements.
   *
   * This method functions exactly the same as {@link Command#requires(Subsystem) requires()},
   * except that it also registers the subsystem for power management. (For detailed info on {@link
   * Command#requires(Subsystem) requires()}, consult the WPILib documentation.) For adding
   * subsystems not governed by power management (e. g. shifters) simply call {@link
   * Command#requires(Subsystem) requires()} as normal.
   *
   * @param subsystem the {@link Subsystem} required
   *
   * @throws IllegalArgumentException if subsystem is null
   * @throws IllegalUseOfCommandException if this command has started before or if it has been given
   * to a {@link CommandGroup}
   * @see ChickenSubsystem
   * @see Subsystem
   */
  protected synchronized void addRequirement(ChickenSubsystem subsystem) {
    super.requires(subsystem);

    usedSubsystems.add(subsystem);
  }

  @Override
  protected void initialize() {
    PowerManager.theManager.registerCommand(this);
  }

  @Override
  protected void end() {
    // Rather than having theManager check if the command is still running
    PowerManager.theManager.unregisterCommand(this);
  }

  /**
   * Compare two ChickenCommands by priority.
   *
   * @param o ChickenCommand to compare to.
   *
   * @return priority.compareTo(o.priority)
   */
  public int compareTo(ChickenCommand o) {
    return Double.compare((priority), o.priority);
  }

  /**
   * Get the priority of this command. Used for power management. Should be overriden as necessary.
   */
  public double getPriority() {
    return priority;
  }

  public void setPriority(double priority) {
    this.priority = priority;
  }

  public Set<ChickenSubsystem> getUsedSubsystems() {
    return usedSubsystems;
  }
}
