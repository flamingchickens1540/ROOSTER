package org.team1540.base;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Timer;

import java.util.*;

// Reminder that everything will need to be thread safe
public class PowerManager extends Thread {

  // Singleton
  public static PowerManager theManager;

  /*
  Because these two should be really the same for your entire robot, they have to be edited here.
  I may change this in the future if we decide we need to change this on the fly for some reason
  In case you need someone to blame, @author Jonathan Edelman
  */
  // Required value to be considered spiking in amps
  private final double SPIKE_PEAK = 50;
  // How long the spike must spike for before doing anything in seconds
  private final double SPIKE_LENGTH = 2.0;
  // Target value when we start power managing
  private final double TARGET = 40;
  /**
   * Kill switch. Make it false to kill the power manager. No idea why you'd ever need to do this
   * though
   */
  public boolean keepRunning = true;

  // Store the subsystems being used by the currently running commands and their priorities as set by the sum of the
  // commands' priorities
  // For the love of everything, so there are no race conditions, do not access this except though synchronized
  // methods
  private Map<ChickenSubsystem, Double> runningSubsystems = new LinkedHashMap<>();
  // Because we gotta grab the power info off of it
  private PowerDistributionPanel pdp = new PowerDistributionPanel();
  // General check-ins, such as if the robot is enabled
  private RobotBase theRobot;

  private PowerManager(RobotBase theRobot) {
    this.theRobot = theRobot;
  }

  public void createPowerManager(RobotBase theRobot) {
    theManager = new PowerManager(theRobot);
  }

  @Override
  public void run() {

    Timer theTimer = new Timer();

    while (theRobot.isEnabled() && keepRunning) {
      if (isSpiking()) {
        // Start a timer
        theTimer.start();

        // Start the timer and check if it continues to spike at least for SPIKE_LENGTH
        while (isSpiking()) {
          if (theTimer.get() > SPIKE_LENGTH) {
            scalePower();
          }
        }

        // Stop scaling the power for everyone
        stopScaling();

        // Stop and reset the timer. Done here instead of at the start because we want to capture the spiking ASAP
        theTimer.stop();
        theTimer.reset();
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
    double factor = TARGET / totalScaledCurrent;

    // Multiply that factor by the ratio between the new power and the actual power and pass that
    // back to the subsystem
    for (ChickenSubsystem currentSubsystem : subsystemCurrents.keySet()) {
      currentSubsystem.setAbsolutePowerLimit(subsystemCurrents.get(currentSubsystem) * factor);
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
   * SPIKE_PEAK
   *
   * @return Boolean representing if the voltage is spiking.
   */
  private boolean isSpiking() {
    return pdp.getTotalCurrent() > SPIKE_PEAK;
  }

  /**
   * Run an arbitrary function to scale the priority of a given subsystem. <p> Currently uses
   * inverse natural exponential For those who like LaTeX, here's the function, where h is the
   * highest priority and x is the priority \frac{h}{e^{\left(h-x\right)}}
   *
   * @param highestPriority The priority of the highest priority subsystem currently running.
   * @param priority The priority of this subsystem.
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
    for (ChickenSubsystem currentSubsystem : toRegister.usedSubsystems) {
      Double currentValue = runningSubsystems.get(currentSubsystem);
      if (currentValue == null) {
        currentValue = 0d;
      }
      runningSubsystems.put(currentSubsystem, currentValue + toRegister.priority);
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
    for (ChickenSubsystem currentSubsystem : toUnregister.usedSubsystems) {
      runningSubsystems.replace(currentSubsystem,
          runningSubsystems.get(currentSubsystem) - toUnregister.priority);
      if (runningSubsystems.get(currentSubsystem) <= 0) {
        runningSubsystems.remove(currentSubsystem);
        currentSubsystem.stopLimitingPower();
      }
    }
  }

}
