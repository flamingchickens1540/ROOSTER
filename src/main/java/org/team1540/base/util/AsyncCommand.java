package org.team1540.base.util;

import edu.wpi.first.wpilibj.command.Command;

/**
 * A {@link Command} that spawns off a new {@link Thread} on initialization and interrupts it on
 * completion. That thread will call the {@link #run()} method at a user-specified interval with
 * precise timings.
 *
 * @see Thread
 */
public abstract class AsyncCommand extends Command {

  private Thread thread;

  private final Object intervalLock = new Object();

  // should ONLY be accessed through getter and setter as those are synced. Unsyced long access
  // runs the risk of reading half of the original value and half of the new value
  private long interval;
  // booleans are thread-safe as long as you're just reading and writing
  private boolean finished;

  /**
   * Constructs a new {@code AsyncCommand} with a preset periodic interval.
   *
   * @param interval The interval between {@link #run()} calls, in milliseconds.
   */
  public AsyncCommand(long interval) {
    this.interval = interval;
  }

  /**
   * Runs the action of this command.
   * <p>
   * This is equivalent to the {@link #execute()} method in normal commands. Note that this method
   * will be called in a seperate thread, and as such any public methods it calls, fields it uses,
   * etc. MUST be thread-safe.
   */
  protected abstract void run();

  @Override
  protected void initialize() {
    finished = false;
    // create a new thread and start it
    thread = new Thread(() -> {
      while (!finished) {
        run();
        if (!finished) {
          try {
            Thread.sleep(getInterval()); // get method to take advantage of synchronization
          } catch (InterruptedException e) {
            finished = true;
          }
        }
      }
    });

    thread.start();
  }

  /**
   * Interrupts the spawned thread. This method is so that implementations can define overrides of
   * {@link #interrupted()} that do not call {@link #end()}. Calling this on a command that is not
   * running has no effect.
   */
  protected void interrupt() {
    if (thread != null) {
      thread.interrupt();
      thread = null;
    }
  }


  /**
   * Interrupts the spawned thread, then calls {@link #end()}. Note that overrides of this method
   * MUST call {@link #interrupt()} to cause thread execution to stop properly.
   * <p>
   * {@inheritDoc}
   */
  @Override
  protected void interrupted() {
    interrupt();
    end();
  }

  @Override
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
  public long getInterval() {
    synchronized (intervalLock) {
      return interval;
    }
  }

  /**
   * Sets the periodic interval between calls to {@link #run()}.
   *
   * @param interval The interval, in milliseconds.
   */
  public void setInterval(long interval) {
    synchronized (intervalLock) {
      this.interval = interval;
    }
  }
}
