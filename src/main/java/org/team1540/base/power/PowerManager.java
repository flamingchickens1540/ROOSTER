package org.team1540.base.power;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

/*
A word on language: Management is if this is running, scaling is if the power is actually being set
to be something different.
 */

// Reminder that everything will need to be thread safe

/**
 * A class for power managing PowerManageables (both with and without telemetry.) Set a target
 * voltage to stay above, register PowerManageables, and you're good to go!
 */
@SuppressWarnings("unused")
public class PowerManager extends Thread implements Sendable {

  private static String name = "PowerManager";

  // Singleton
  private static PowerManager theManager = new PowerManager();

  static {
    theManager.start();
  }

  private final Timer voltageTimer = new Timer();
  // Store the currently running PowerManageables
  // For the love of everything, so there are no race conditions, do not access this except though
  // synchronized blocks
  private final Set<PowerManageable> powerManageables = Collections
      .synchronizedSet(new HashSet<>());
  private final Object powerLock = new Object();
  // Because we gotta grab the power info off of it
  private final PowerDistributionPanel pdp = new PowerDistributionPanel();
  private int updateDelay = 5;
  /**
   * Default to be a little higher than brownouts.
   */
  private double voltageDipLow = 7.5;
  private double voltageMargin = 0.5;
  private double voltageDipLength = 0.25;
  private double voltageTarget = 8.0;
  private boolean running = true;
  private BiFunction<Double, Double, Double> priorityScalingFunction = this::scaleExponential;

  private double priorityUnscaledTotal, priorityScaledTotal, priorityScaledNoTelemetryTotal,
      currentUnscaledTotal,
      currentScaledTotal, noTelemetryCount;

  private PowerManager() {
  }

  /**
   * Gets the PowerManager.
   *
   * @return The singleton PowerManager instance.
   */
  public static PowerManager getInstance() {
    return theManager;
  }

  @Override
  public void run() {
    while (true) {
      // No whiles in here as that'd stop the last block from executing
      if (running) {
        if (isVoltageDipping()) {
          if (voltageTimer.get() <= 0) {
            // Calling the timer when it's already started seems to reset it.
            voltageTimer.start();
          }
        } else {
          voltageTimer.stop();
          voltageTimer.reset();
          stopScaling();
        }

        if (hasTimePassedVoltage()) {
          scalePower();
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
      // TODO clean this up cuz it's messy; some way of maintaining what's what kind of unit?
      // TODO v is wrong
      // If the PowerManageable has PowerTelemetry, we'll scale it using the arbitrary function.
      // Otherwise, it'll be scaled flatly using the remaining bit.

      final double highestPriority = Collections.max(powerManageables).getPriority();
      // The amount of our current output we need to be at
      final double percentToTarget = RobotController.getBatteryVoltage() / voltageTarget;
      final double totalCurrentDraw = pdp.getTotalCurrent();
      final double currentNeedToDecrease = (1 - percentToTarget) * totalCurrentDraw;

      // Reset the totals
      priorityUnscaledTotal = 0;
      priorityScaledTotal = 0;
      priorityScaledNoTelemetryTotal = 0;
      currentUnscaledTotal = 0;
      currentScaledTotal = 0;
      noTelemetryCount = 0;

      // TODO Could still use some cleanup. Still, could be worse.
      // TODO Stop passing null around
      Set<PowerProperties> manageableProperties = new LinkedHashSet<>();
      for (PowerManageable thisManageable : powerManageables) {
        PowerProperties thisPowerProperty = new PowerProperties(thisManageable, highestPriority);
        manageableProperties.add(thisPowerProperty);
      }

      // Find a factor such that the totalCurrentDrawScaled * that factor =
      // totalCurrentDrawUnscaled * percentNeededToDecrease * the priority ratio between subsystems
      // with telemetry and those without it
      // Also including checking for divide by zeros
      final double fancyScalingCurrentTarget = priorityUnscaledTotal == 0 ?
          currentUnscaledTotal * percentToTarget * (priorityScaledTotal / priorityUnscaledTotal)
          : 0;
      double scaledToUnscaledFactor = currentScaledTotal == 0 ? (fancyScalingCurrentTarget) /
          currentScaledTotal : 0;

      // This leaves some remaining amount of current that's unnacounted for by this fancy scaling.
      // We'll do a dumber scale the rest of the powerManageables to account for that

      // Get the remaining percent needed to decrease
      double unnacountedCurrentPercent = 1 - (fancyScalingCurrentTarget / totalCurrentDraw);
      // Find the percent to decrease per unit priority
      double percentNeededToDecreasePerPriority = unnacountedCurrentPercent /
          (priorityScaledNoTelemetryTotal / noTelemetryCount);

      // IF THERE IS TELEMETRY
      // Multiply each scaled power by the factor, which gets us our real target current. Then,
      // divide that by the original current draw to get the percent output we want.

      // IF THERE IS NOT
      // Do the dumber scale
      for (PowerProperties currentProperties : manageableProperties) {
        double percentToDecreaseTo = currentProperties.getCurrentScaled()
            .orElse(percentNeededToDecreasePerPriority *
                currentProperties.priorityScaled);
        currentProperties.manageable.setPercentOutputLimit(percentToDecreaseTo);
      }
    }
  }

  /**
   * Separate method to block PowerManageable registration/deregistration while stopping scaling.
   */
  private void stopScaling() {
    synchronized (powerLock) {
      for (PowerManageable currentManageable : powerManageables) {
        currentManageable.stopLimitingPower();
      }
    }
  }

  /**
   * Determines if the voltage is currently dipping. If power limiting is not engaged,
   * returns RobotController.getBatteryVoltage() &lt; voltageDipLow || RobotController.
   * isBrownedOut();
   * If power limiting is engaged, returns pdp.getVoltage() &lt; voltageDipLow + voltageMargin ||
   * RobotController.isBrownedOut();.
   *
   * @return Boolean representing if the voltage is dipping.
   */
  public boolean isVoltageDipping() {
    if (!hasTimePassedVoltage()) {
      return RobotController.getBatteryVoltage() < voltageDipLow || RobotController.isBrownedOut();
    } else {
      return RobotController.getBatteryVoltage() < voltageDipLow + voltageMargin || RobotController
          .isBrownedOut();
    }
  }

  private boolean hasTimePassedVoltage() {
    return (voltageTimer.get() > voltageDipLength);
  }

  /**
   * Determine if power limiting has kicked in.
   *
   * @return True if power limiting has kicked in, false otherwise
   */
  public boolean isLimiting() {
    return hasTimePassedVoltage() && isVoltageDipping();
  }

  /**
   * Run an arbitrary function to scale the priority of a given {@link PowerManageable}. <p>
   * Currently uses
   * inverse natural exponential For those who like LaTeX, here's the function, where h is the
   * highest priority and x is the priority \frac{h}{e^{\left(h-x\right)}}
   *
   * @param highestPriority The priority of the highest priority {@link PowerManageable}
   * currently running.
   * @param priority The priority of this {@link PowerManageable}.
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
      return powerManageables.add(toRegister);
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
   * @return true if the PowerManager contained the specified element
   */
  public boolean unregisterPowerManageable(PowerManageable toUnregister) {
    synchronized (powerLock) {
      return powerManageables.remove(toUnregister);
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
   * Gets the highest current time on any of the internal timers representing time from the most
   * recent spike or dip.
   *
   * @return Double representing time.
   */
  public double getPowerTime() {
    return voltageTimer.get();
  }

  /**
   * Gets the required voltage value for the robot to be considered dipping. Defaults to 7.2V.
   *
   * @return voltageDipLow The minimum dip value, in volts.
   */
  public double getVoltageDipLow() {
    return voltageDipLow;
  }

  /**
   * Sets the required voltage value for the robot to be considered dipping. Defaults to 7.2V.
   *
   * @param voltageDipLow The minimum dip value, in volts.
   */
  public void setVoltageDipLow(double voltageDipLow) {
    this.voltageDipLow = voltageDipLow;
  }

  /**
   * Gets how long the voltage must dip for before doing anything. Defaults to 0.25 seconds, must be
   * {@literal >=}0.
   *
   * @return voltageDipLength The minimum actionable spike length, in seconds.
   */
  public double getVoltageDipLength() {
    return voltageDipLength;
  }

  /**
   * Sets how long the voltage must dip for before doing anything. Defaults to 0.25 seconds, must
   * be {@literal >=}0.
   *
   * @param voltageDipLength The minimum actionable spike length, in seconds.
   */
  public void setVoltageDipLength(double voltageDipLength) {
    if (voltageDipLength < 0) {
      throw new IllegalArgumentException("voltageDipLength must be >=0, got " + voltageDipLength);
    }
    this.voltageDipLength = voltageDipLength;
  }

  /**
   * Gets the voltageMargin within which, if power limiting has engaged, power management will
   * remain engaged. Defaults to 0.5V.
   *
   * @return voltageMargin in volts.
   */
  public double getVoltageMargin() {
    return voltageMargin;
  }

  /**
   * Sets the voltageMargin within which, if power limiting has engaged, power management will
   * remain engaged. Defaults to 0.5V, must be {@literal >=}0.
   *
   * @param voltageMargin in volts.
   */
  public void setVoltageMargin(double voltageMargin) {
    if (voltageMargin < 0) {
      throw new IllegalArgumentException("voltageMargin must be >=0, got " + voltageMargin);
    }
    this.voltageMargin = voltageMargin;
  }

  /**
   * Gets the voltageTarget. Defaults to 8.0V.
   *
   * @return voltageTarget in volts.
   */
  public double getVoltageTarget() {
    return voltageTarget;
  }

  /**
   * Sets the voltageTarget. Defaults to 8.0V, must be {@literal >=}0.
   */
  public void setVoltageTarget(double voltageTarget) {
    if (voltageTarget < 0) {
      throw new IllegalArgumentException("voltageTarget must be >=0, got " + voltageTarget);
    }
    this.voltageTarget = voltageTarget;
  }

  @Override
  public String getSubsystem() {
    return name;
  }

  @Override
  public void setSubsystem(String subsystem) {
    name = subsystem;
  }

  public BiFunction<Double, Double, Double> getPriorityScalingFunction() {
    return priorityScalingFunction;
  }

  public void setPriorityScalingFunction(
      BiFunction<Double, Double, Double> priorityScalingFunction) {
    this.priorityScalingFunction = priorityScalingFunction;
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    builder.setSmartDashboardType("PowerManager");
    builder.addBooleanProperty("isVoltageDipping", this::isVoltageDipping, null);
    builder.addBooleanProperty("isLimiting", this::isLimiting, null);
    builder.addDoubleProperty("powerTime", this::getPowerTime, null);
    builder.addBooleanProperty("running", this::isRunning, this::setRunning);
    builder.addDoubleProperty("updateDelay", this::getUpdateDelay,
        value -> setUpdateDelay(Math.toIntExact(Math.round(value))));
    builder.addDoubleProperty("voltageDipLow", this::getVoltageDipLow, this::setVoltageDipLow);
    builder.addDoubleProperty("voltageMargin", this::getVoltageMargin, this::setVoltageMargin);
    builder.addDoubleProperty("voltageDipLength", this::getVoltageDipLength,
        this::setVoltageDipLength);
    builder.addDoubleProperty("voltageTarget", this::getVoltageTarget, this::setVoltageTarget);
  }


  private class PowerProperties {

    private final Double priorityUnscaled;
    private final Double priorityScaled;
    private final Double currentUnscaled;
    private final Double currentScaled;
    private PowerManageable manageable;

    public PowerProperties(PowerManageable manageable, final double highestPriority) {
      this.manageable = manageable;
      this.priorityUnscaled = manageable.getPriority();
      this.priorityScaled = priorityScalingFunction.apply(highestPriority, priorityUnscaled);
      priorityUnscaledTotal += priorityUnscaled;
      priorityScaledTotal += priorityScaled;
      if (manageable.getPowerTelemetry().isPresent()) {
        this.currentUnscaled = manageable.getPowerTelemetry().get().getCurrent();
        this.currentScaled = this.priorityScaled * currentUnscaled;
        currentUnscaledTotal += currentUnscaled;
        currentScaledTotal += currentScaled;
      } else {
        this.currentUnscaled = null;
        this.currentScaled = null;
        priorityScaledNoTelemetryTotal += priorityScaled;
        noTelemetryCount++;
      }
    }

    public Double getPriorityUnscaled() {
      return priorityUnscaled;
    }

    public Double getPriorityScaled() {
      return priorityScaled;
    }

    public Optional<Double> getCurrentUnscaled() {
      return Optional.ofNullable(currentUnscaled);
    }

    public Optional<Double> getCurrentScaled() {
      return Optional.ofNullable(currentScaled);
    }

  }


}
