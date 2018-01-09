package org.team1540.base.power;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

// Reminder that everything will need to be thread safe
@SuppressWarnings("unused")
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

  private double margin = 5;
  private boolean running = true;
  private boolean isLimiting = false;

  // Store the currently running PowerManageables
  // For the love of everything, so there are no race conditions, do not access this except though synchronized blocks
  private final Set<PowerManageable> powerManaged = Collections.synchronizedSet(new HashSet<>());
  private final Object powerLock = new Object();
  // Because we gotta grab the power info off of it
  private final PowerDistributionPanel pdp = new PowerDistributionPanel();

  private PowerManager() {}

  /**
   * Gets the PowerManager.
   */
  public static PowerManager getInstance() {
    return theManager;
  }

  @Override
  public void run() {
    Timer theTimer = new Timer();
    while (true) {
      // No whiles in here as that'd stop the last block from executing
      if (running) {
        SmartDashboard.putNumber("Power Timer: ", theTimer.get());
        if (isSpiking()) {
          if (theTimer.get() <= 0) {
//            System.out.println("Starting timer");
            // Calling the timer when it's already started seems to reset it.
            theTimer.start();
          }
          if (theTimer.get() > spikeLength) {
//            System.out.println("Timer passed");
            isLimiting = true;
            scalePower();
          }
        } else {
          System.out.println("Not spiking");
          isLimiting = false;
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

  /**
   * Separate method to block PowerManageable registration/unregistration while actually scaling the
   * power.
   */
  private void scalePower() {
    synchronized (powerLock) {

      Map<PowerManageable, Double> powerManageableCurrents = new LinkedHashMap<>();

      // Find out what the highest priority is
      double highestPriority = Collections.max(powerManaged).getPriority();

      // For each PowerManageable, pass the priority into an arbitrary function, multiply that value by the
      // actual current draw, and store it in a map along with a running tally of the total
      double totalScaledCurrent = 0;
      for (PowerManageable currentManageable : powerManaged) {
        double scaledCurrent =
            scaleExponential(highestPriority, currentManageable.getPriority()) * currentManageable
                .getCurrent();
        powerManageableCurrents.put(currentManageable, scaledCurrent);
        totalScaledCurrent += scaledCurrent;
      }

      // Find a factor such that the new total equals the target
      double factor = target / totalScaledCurrent;

      // Multiply that factor by the ratio between the new power and the actual power and pass that
      // back to the PowerManageable
      for (PowerManageable currentManageable : powerManaged) {
        currentManageable.limitPower(powerManageableCurrents.get(currentManageable) * factor);
      }
    }
  }

  /**
   * Separate method to block PowerManageable registration/deregistration while stopping scaling.
   */
  private void stopScaling() {
    synchronized (powerLock) {
      for (PowerManageable currentManageable : powerManaged) {
        currentManageable.stopLimitingPower();
      }
    }
  }


  /**
   * Determines if the voltage is currently spiking. If power limiting is not engaged,
   * returns pdp.getTotalCurrent() &gt; spikePeak. If power limiting is engaged, returns
   * target - pdp.getTotalCurrent() &gt; margin.
   *
   * @return Boolean representing if the voltage is spiking.
   */
  public boolean isSpiking() {
    if (!isLimiting) {
      System.out.println("not limiting yet: " + (pdp.getTotalCurrent() > spikePeak));
      return true;
//      return pdp.getTotalCurrent() > spikePeak;
    } else {
      System.out.println("spiked: " + (pdp.getTotalCurrent() > target - margin));
      return pdp.getTotalCurrent() > target - margin;
    }
  }

  /**
   * Determine if power limiting has kicked in.
   *
   * @return True if power limiting has kicked in, false otherwise
   */
  public boolean isLimiting() {
    return isLimiting;
  }

  /**
   * Run an arbitrary function to scale the priority of a given {@link PowerManageable}. <p> Currently uses
   * inverse natural exponential For those who like LaTeX, here's the function, where h is the
   * highest priority and x is the priority \frac{h}{e^{\left(h-x\right)}}
   *
   * @param highestPriority The priority of the highest priority {@link PowerManageable} currently running.
   * @param priority The priority of this {@link PowerManageable}.
   *
   * @return The scale factor for this {@link PowerManageable}.
   */
  private double scaleExponential(double highestPriority, double priority) {
    return highestPriority / (Math.exp(highestPriority - priority));
  }

  /**
   * Registers the {@link PowerManageable} as being used. Blocks power scaling.
   *
   * @param toRegister The {@link PowerManageable} to register.
   * @return true if the PowerManager did not already contain the specified element
   */
  public boolean registerPowerManageable(PowerManageable toRegister) {
    synchronized (powerLock) {
      return powerManaged.add(toRegister);
    }
  }

  /**
   * Registers a group of {@link PowerManageable}s. Calls registerPowerManager().
   *
   * @param toRegister The {@link PowerManageable}s to register.
   * @return A map of PowerManageables with the key true if the PowerManager did not already contain
   * the specified element
   */
  public Map<PowerManageable, Boolean> registerPowerManageables(
      PowerManageable... toRegister) {
    HashMap<PowerManageable, Boolean> success = new HashMap<>();
    for (PowerManageable register : toRegister) {
      success.put(register, registerPowerManageable(register));
    }
    return success;
  }

  /**
   * Unregisters the {@link PowerManageable} as being used. Blocks power scaling.
   *
   * @param toUnregister The {@link PowerManageable} to unregister.
   *
   * @return true if the PowerManager contained the specified element
   */
  public boolean unregisterPowerManageable(PowerManageable toUnregister) {
    synchronized (powerLock) {
      return powerManaged.remove(toUnregister);
    }
  }

  /**
   * Unregisters a group of {@link PowerManageable}s. Calls unregisterPowerManager().
   *
   * @param toUnregister The {@link PowerManageable}s to unregister.
   * @return A map of PowerManageables with the key true if the PowerManager contained the specified
   * element
   */
  public Map<PowerManageable, Boolean> unregisterPowerManageables(
      PowerManageable... toUnregister) {
    HashMap<PowerManageable, Boolean> success = new HashMap<>();
    for (PowerManageable unregister : toUnregister) {
      success.put(unregister, unregisterPowerManageable(unregister));
    }
    return success;
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

  /**
   * Gets the margin below which, if power limiting has engaged, power management will remain
   * engaged.
   *
   * @return Margin in amps (default 5)
   */
  public double getMargin() {
    return margin;
  }

  /**
   * Set the margin below which, if power limiting has engaged, power management will remain
   * engaged.
   */
  public void setMargin(double margin) {
    this.margin = margin;
  }
}
