package org.team1540.base;

import edu.wpi.first.wpilibj.command.Command;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class ChickenCommand extends Command implements Comparable<ChickenCommand> {

	/**
	 * The subsystems used by this command. Used for power management. Should be overridden as necessary.
	 */
	final Set<ChickenSubsystem> usedSubsystems = new LinkedHashSet<>(0);

	/**
	 * Get the priority of this command. Used for power management. Should be overriden as necessary.
	 */
	public final double priority = 0.0;

	@Override
	public void initialize() {
		PowerManager.theManager.registerCommand(this);
	}

	@Override
	public void end() {
		// Rather than having theManager check if the command is still running
		PowerManager.theManager.unregisterCommand(this);
	}

	/**
	 * Compare two ChickenCommands by priority.
	 * @param o ChickenCommand to compare to.
	 * @return priority.compareTo(o.priority)
	 */
	public int compareTo(ChickenCommand o) {
		return Double.compare((priority), o.priority);
	}
}
