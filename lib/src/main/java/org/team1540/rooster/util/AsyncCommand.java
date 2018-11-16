package org.team1540.rooster.util;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.command.Command;
import org.jetbrains.annotations.Contract;

/**
 * A {@link Command} that spawns off a new {@link Notifier} on initialization and stops it. That
 * thread will call the {@link #runPeriodic()} ()} method at a user-specified interval with precise
 * timings.
 *
 * @see Thread
 */
public abstract class AsyncCommand extends Command {

  private double interval;
  private boolean finished = false;

  private Notifier notifier;

  /**
   * Constructs a new {@code AsyncCommand} with a preset periodic interval.
   *
   * @param interval The interval between {@link #run()} calls, in milliseconds.
   */
  public AsyncCommand(long interval) {
    this.interval = interval / 1000.0;
  }

  /**
   * Runs the action of this command.
   * <p>
   * This is equivalent to the {@link #execute()} method in normal commands. Note that this method
   * will be called in a seperate thread, and as such any public methods it calls, fields it uses,
   * etc. MUST be thread-safe.
   */
  protected void runPeriodic() {

  }

  /**
   * Runs initialization code for this command.
   *
   * This is equivalent to the {@link Command#initialize()} method.
   */
  protected void runInitial() {

  }

  /**
   * Called when the {@code AsyncCommand} ends peacefully. This is equivalent to {@link
   * Command#end()}.
   *
   * This method is called when the command ends peacefully, i.e. when {@link #markAsFinished()} was
   * called. However, it is possible that another command interrupts this command after {@link
   * #markAsFinished()} was called but before the command was removed from the scheduler, and as
   * such this method might not be called even if {@link #markAsFinished()} was called.
   */
  protected void runEnd() {

  }

  /**
   * Called when the {@code AsyncCommand} is interrupted.
   *
   * This method serves an identical purpose to {@link Command#interrupted()}. By default, this
   * method calls {@link #runEnd()}.
   */
  protected void runInterrupt() {
    runEnd();
  }

  @Override
  protected final void initialize() {
    finished = false;
    runInitial();

    notifier = new Notifier(() -> {
      if (!finished) {
        runPeriodic();
      }
    });

    notifier.startPeriodic(interval);
  }

  @Override
  protected final void interrupted() {
    notifier.stop();
    runInterrupt();
  }

  @Override
  protected final void end() {
    notifier.stop();
    runEnd();
  }

  @Override
  @Contract(pure = true)
  protected boolean isFinished() {
    return finished;
  }

  /**
   * Marks the command as finished. The {@link #run()} method will no longer be called, and the
   * command will be canceled on the next scheduler tick.
   */
  protected void markAsFinished() {
    this.finished = true;
  }

  /**
   * Gets the periodic interval between calls to {@link #run()}.
   *
   * @return The interval, in milliseconds.
   */
  @Contract(pure = true)
  public long getInterval() {
    return (long) interval * 1000;
  }

  /**
   * Sets the periodic interval between calls to {@link #run()}.
   *
   * @param interval The interval, in milliseconds.
   */
  public void setInterval(long interval) {
    this.interval = interval / 1000.0;
  }
}
