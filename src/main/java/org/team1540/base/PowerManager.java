package org.team1540.base;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Timer;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

// Reminder that everything will need to be thread safe
public class PowerManager extends Thread {
  // Singleton
  public static PowerManager theManager;

  // static { theManager.start(); }
  // controversial–will be uncommented or deleted later

  private int updateDelay = 5;
  private double spikePeak = 50;
  private double spikeLength = 2.0;
  private double target = 40;
  private boolean running = true;
  private RobotBase robot;

  // Store the subsystems being used by the currently running commands and their priorities as set by the sum of the
  // commands' priorities
  // For the love of everything, so there are no race conditions, do not access this except though synchronized
  // methods
  private Map<ChickenSubsystem, Double> runningSubsystems = new LinkedHashMap<>();
  // Because we gotta grab the power info off of it
  private PowerDistributionPanel pdp = new PowerDistributionPanel();

  private PowerManager(RobotBase robot) {
    this.robot = robot;
  }

  /**
   * Gets the PowerManager.
   */
  public PowerManager getInstance() {
    return theManager;
  }

  public void createPowerManager(RobotBase robot) {
    theManager = new PowerManager(robot);
  }

  @Override
  public void run() {
    Timer theTimer = new Timer();
    while (robot.isEnabled()) {
      if (running) {
        if (isSpiking()) {
          // Start a timer
          theTimer.start();

          if (theTimer.hasPeriodPassed(spikeLength)) {
            scalePower();
          }
        } else {
          stopScaling();
          theTimer.stop();
          theTimer.reset();
        }
      }

      try {
        sleep(updateDelay);
      } catch (InterruptedException e) {
        // end the thread
        return;
      }
    }
  }

  // Separate method to block command registration/unregistration while actually scaling the power.
  private synchronized void scalePower() {
    Map<ChickenSubsystem, Double> subsystemCurrents = new LinkedHashMap<>();

    // Find out what the highest priority is
    double highestPriority = Collections.max(runningSubsystems.values());

    // For each subsystem, pass the priority into an arbitrary function, multiply that value by the
    // actual current draw, and store it in a map along with a running tally of the total
    double totalScaledCurrent = 0;
    for (ChickenSubsystem currentSubsystem : runningSubsystems.keySet()) {
      double scaledCurrent = scaleExponential(highestPriority,
          runningSubsystems.get(currentSubsystem)) * currentSubsystem.getCurrent();
      subsystemCurrents.put(currentSubsystem, scaledCurrent);
      totalScaledCurrent += scaledCurrent;
    }

    // Find a factor such that the new total equals the target
    double factor = target / totalScaledCurrent;

    // Multiply that factor by the ratio between the new power and the actual power and pass that
    // back to the subsystem
    for (ChickenSubsystem currentSubsystem : subsystemCurrents.keySet()) {
      currentSubsystem.limitPower(subsystemCurrents.get(currentSubsystem) * factor);
    }
  }

  // Separate method to block command registration/unregistration while stopping scaling.
  private synchronized void stopScaling() {
    for (ChickenSubsystem currentSubsystem : runningSubsystems.keySet()) {
      currentSubsystem.stopLimitingPower();
    }
  }


  /**
   * Determines if the voltage is currently spiking. Literally just return pdp.getTotalCurrent() >
   * spikePeak
   *
   * @return Boolean representing if the voltage is spiking.
   */
  private boolean isSpiking() {
    return pdp.getTotalCurrent() > spikePeak;
  }

  /**
   * Run an arbitrary function to scale the priority of a given subsystem. <p> Currently uses
   * inverse natural exponential For those who like LaTeX, here's the function, where h is the
   * highest priority and x is the priority \frac{h}{e^{\left(h-x\right)}}
   *
   * @param highestPriority The priority of the highest priority subsystem currently running.
   * @param priority The priority of this subsystem.
   *
   * @return The scale factor for this subsystem.
   */
  private double scaleExponential(double highestPriority, double priority) {
    return highestPriority / (Math.exp(highestPriority - priority));
  }

	/*
    A comment on the following two methods: Why are they synchronized? Why not use a more thread-safe data structure
	that better supports the intended use case? Because it doesn't really matter.

	In theory, it'd be nice to be able to say "Hey, this command isn't being used any more, let's ignore it when we're
	doing our calculations" while we're actually doing those calculations. But that actually isn't really a big deal.
	Any time we actually do need to register/unregister a command, it'll happen immediately after we're done managing
	the power for that cycle. Furthermore, if we allocate it power it doesn't use, that's not a huge problem for
	everyone else. There's also no good reason to access the map outside of the methods defined here, so just don't do
	it.
	 */

  /**
   * Registers the command's subsystems as being used. Blocks power scaling.
   *
   * @param toRegister The command to register.
   */
  synchronized void registerCommand(ChickenCommand toRegister) {
    // For every subsystem the command uses, add it to runningSubsystems; if it's already in there, increment the
    // priority by the priority of the given command
    for (ChickenSubsystem currentSubsystem : toRegister.getUsedSubsystems()) {
      Double currentValue = runningSubsystems.get(currentSubsystem);
      if (currentValue == null) {
        currentValue = 0d;
      }
      runningSubsystems.put(currentSubsystem, currentValue + toRegister.getPriority());
    }
  }

  /**
   * Unregisters the command's subsystems as being used. Blocks power scaling.
   *
   * @param toUnregister The command to unregister.
   */
  synchronized void unregisterCommand(ChickenCommand toUnregister) {
    // For every subsystem the command uses, decrement its priority in runningSubsystems; if this causes its
    // priority to 0, it is removed and scaling for that command is stopped
    for (ChickenSubsystem currentSubsystem : toUnregister.getUsedSubsystems()) {
      runningSubsystems.replace(currentSubsystem,
          runningSubsystems.get(currentSubsystem) - toUnregister.getPriority());
      if (runningSubsystems.get(currentSubsystem) <= 0) {
        runningSubsystems.remove(currentSubsystem);
        currentSubsystem.stopLimitingPower();
      }
    }
  }

  public double getSpikePeak() {
    return spikePeak;
  }

  /**
   * Sets the required current value for the robot to be considered spiking. Defaults to 50A.
   *
   * @param spikePeak The minimum spike value, in amps.
   */
  public void setSpikePeak(double spikePeak) {
    this.spikePeak = spikePeak;
  }

  public double getSpikeLength() {
    return spikeLength;
  }

  /**
   * Sets how long the spike must spike for before doing anything. Defaults to 2 seconds.
   *
   * @param spikeLength The minimum actionable spike length, in seconds.
   */
  public void setSpikeLength(double spikeLength) {
    this.spikeLength = spikeLength;
  }

  public double getTarget() {
    return target;
  }

  /**
   * Sets the target value we want when starting to power-manage. Defaults to 40A.
   *
   * @param target The target value, in amps.
   */
  public void setTarget(double target) {
    this.target = target;
  }

  public int getUpdateDelay() {
    return updateDelay;
  }

  /**
   * Sets the time between power management cycles. Defaults to 5ms.
   *
   * @param updateDelay The time between power management cycles, in milliseconds.
   */
  public void setUpdateDelay(int updateDelay) {
    this.updateDelay = updateDelay;
  }

  /**
   * Allows the power manager to run. This method has no effect if the manager is already running.
   */
  public void start() {
    running = true;
  }

  /**
   * Stops the power manager. It can be restarted with a call to {@link #start()}.
   */
  public void kill() {
    running = false;
  }
}
