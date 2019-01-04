package org.team1540.rooster.testing;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.team1540.rooster.preferencemanager.Preference;
import org.team1540.rooster.util.AsyncCommand;

public class AsyncCommandTestRobot extends IterativeRobot {

  Timer toggleTimer = new Timer();
  Command command1 = new NonAsyncTest();
  Command command2 = new AsyncTest();

  @Preference(value = "End commands", persistent = false)
  public boolean endCommands = false;

  private static Subsystem subsystem = new Subsystem() {
    @Override
    protected void initDefaultCommand() {

    }
  };

  @Override
  public void robotPeriodic() {
    Scheduler.getInstance().run();
    if (toggleTimer.get() <= 0) {
      toggleTimer.reset();
      toggleTimer.start();
    }

    if (toggleTimer.get() > 1) {
      toggleTimer.reset();
      if (command1.isRunning()) {
        if (endCommands) {
          command1.cancel();
        }
        command2.start();
      } else {
        if (endCommands) {
          command2.cancel();
        }
        command1.start();
      }
    }
  }

  private static class NonAsyncTest extends Command {

    public NonAsyncTest() {
      requires(subsystem);
      setRunWhenDisabled(true);
    }

    @Override
    protected void initialize() {
      System.out.println(System.currentTimeMillis() + ": Non-async command initialized");
    }

    @Override
    protected void execute() {
      System.out.println(System.currentTimeMillis() + ": Non-async command executed");

    }

    @Override
    protected boolean isFinished() {
      return false;
    }

    @Override
    protected void end() {
      System.out.println(System.currentTimeMillis() + ": Non-async command ended");
    }

    @Override
    protected void interrupted() {
      System.out.println(System.currentTimeMillis() + ": Non-async command interrupted");
    }
  }

  private static class AsyncTest extends AsyncCommand {

    public AsyncTest() {
      super(10);
      setRunWhenDisabled(true);
      requires(subsystem);
    }

    @Override
    protected void runInitial() {
      System.out.println(System.currentTimeMillis() + ": Async command initialized");
    }

    @Override
    protected void runEnd() {
      System.out.println(System.currentTimeMillis() + ": Async command ended");
    }

    @Override
    protected void runInterrupt() {
      System.out.println(System.currentTimeMillis() + ": Async command interrupted");
    }

    @Override
    protected void runPeriodic() {
      System.out.println(System.currentTimeMillis() + ": Async command executed");
    }
  }
}
