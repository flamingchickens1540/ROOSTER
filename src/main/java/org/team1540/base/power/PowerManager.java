package org.team1540.base.power;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Timer;
import java.rmi.AlreadyBoundException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

// Reminder that everything will need to be thread safe
public class PowerManager extends Thread {

  // Singleton
  private static PowerManager theManager = new PowerManager();

  static {
    theManager.start();
  }

  private int updateDelay = 5;
  private double spikePeak = 50;
  private double spikeLength = 2.0;
  private double target = 40;
  private boolean running = true;

  // Store the currently running PowerManageables
  // For the love of everything, so there are no race conditions, do not access this except though synchronized
  // methods
  private Set<PowerManageable> powerManaged = new HashSet<PowerManageable>();
  // Because we gotta grab the power info off of it
  private PowerDistributionPanel pdp = new PowerDistributionPanel();

  private PowerManager() {}

  /**
   * Gets the PowerManager.
   */
  public PowerManager getInstance() {
    return theManager;
  }

  @Override
  public void run() {
    Timer theTimer = new Timer();
    while (true) {
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

  // Separate method to block PowerManageable registration/unregistration while actually scaling the power.
  private synchronized void scalePower() {
    Map<PowerManageable, Double> powerManageableCurrents = new LinkedHashMap<>();

    // Find out what the highest priority is
    double highestPriority = Collections.max(powerManaged).getPriority();

    // For each PowerManageable, pass the priority into an arbitrary function, multiply that value by the
    // actual current draw, and store it in a map along with a running tally of the total
    double totalScaledCurrent = 0;
    for (PowerManageable currentSubsystem : powerManaged) {
      double scaledCurrent =
          scaleExponential(highestPriority, currentSubsystem.getPriority()) * currentSubsystem
              .getCurrent();
      powerManageableCurrents.put(currentSubsystem, scaledCurrent);
      totalScaledCurrent += scaledCurrent;
    }

    // Find a factor such that the new total equals the target
    double factor = target / totalScaledCurrent;

    // Multiply that factor by the ratio between the new power and the actual power and pass that
    // back to the PowerManageable
    for (PowerManageable currentSubsystem : powerManaged) {
      currentSubsystem.limitPower(powerManageableCurrents.get(currentSubsystem) * factor);
    }
  }

  // Separate method to block PowerManageable registration/unregistration while stopping scaling.
  private synchronized void stopScaling() {
    for (PowerManageable currentSubsystem : powerManaged) {
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
   * Run an arbitrary function to scale the priority of a given PowerManageable. <p> Currently uses
   * inverse natural exponential For those who like LaTeX, here's the function, where h is the
   * highest priority and x is the priority \frac{h}{e^{\left(h-x\right)}}
   *
   * @param highestPriority The priority of the highest priority PowerManageable currently running.
   * @param priority The priority of this PowerManageable.
   *
   * @return The scale factor for this PowerManageable.
   */
  private double scaleExponential(double highestPriority, double priority) {
    return highestPriority / (Math.exp(highestPriority - priority));
  }

	/*
    A comment on the following two methods: Why are they synchronized? Why not use a more thread-safe data structure
	that better supports the intended use case? Because it doesn't really matter.

	In theory, it'd be nice to be able to say "Hey, this PowerManageable isn't being used any more, let's ignore it when we're
	doing our calculations" while we're actually doing those calculations. But that actually isn't really a big deal.
	Any time we actually do need to register/unregister a PowerManageable, it'll happen immediately after we're done managing
	the power for that cycle. Furthermore, if we allocate it power it doesn't use, that's not a huge problem for
	everyone else. There's also no good reason to access the map outside of the methods defined here, so just don't do
	it.
	 */

  /**
   * Registers the PowerManageable as being used. Blocks power scaling.
   *
   * @param toRegister The PowerManageable to register.
   */
  synchronized void registerSubsystem(PowerManageable toRegister) throws AlreadyBoundException {
    // For every PowerManageable, add it to powerManaged; if it's already there, throw an exception
    if (powerManaged.contains(toRegister)) {
      throw new AlreadyBoundException("PowerManageable " + toRegister + " is already registered.");
    }
    powerManaged.add(toRegister);
  }

  /**
   * Unregisters the PowerManageable as being used. Blocks power scaling.
   *
   * @param toUnregister The PowerManageable to unregister.
   */
  synchronized void unregisterCommand(PowerManageable toUnregister) {
    powerManaged.remove(toUnregister);
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

  public boolean isRunning() {
    return running;
  }

  /**
   * Sets the state of the power manager. Set to {@code true} to enable power management, set to
   * {@code false} to disable management.
   *
   * @param running The state of the power manager.
   */
  public void setRunning(boolean running) {
    this.running = running;
  }
}
