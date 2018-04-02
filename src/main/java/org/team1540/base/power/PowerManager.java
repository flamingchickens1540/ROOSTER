package org.team1540.base.power;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.DoubleSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

  @NotNull
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
  private final Map<PowerManageable, CachedPowerProperties> powerManageables = new
      ConcurrentHashMap<>();

  @NotNull
  private DoubleSupplier getTotalCurrent = new GetPowerFromControllersDoubleSupplier();
  //  private DoubleSupplier getTotalCurrent = (new PowerDistributionPanel())::getTotalCurrent;
  private int updateDelay = 10;
  /**
   * Default to be a little higher than brownouts.
   */
  private double voltageDipLow = 7.5;
  private double voltageMargin = 0.5;
  private double voltageDipLength = 0.25;
  private double voltageTarget = 8.0;
  private boolean running = true;
  @NotNull
  private BiFunction<Double, Double, Double> priorityScalingFunction = this::scaleExponential;

  private double priorityUnscaledTotal, priorityScaledTotal, priorityScaledNoTelemetryTotal,
      currentUnscaledTotal, currentScaledTotal, noTelemetryCount, highestPriority;

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
   * Returns d if it is finite, else returns the safeValue.
   * @param d A double of any sort
   * @param safeValue A safe value to return.
   * @return A finite double.
   */
  private static double finiteMath(double d, double safeValue) {
    return Double.isFinite(d) ? d : safeValue;
  }

  /**
   * Separate method to block PowerManageable registration/deregistration while stopping scaling.
   */
  private synchronized void stopScaling() {
    powerManageables.keySet().forEach(PowerManageable::stopLimitingPower);
  }

  /**
   * Determines if the voltage is currently dipping. If power limiting is not engaged,
   * returns {@link RobotController#getBatteryVoltage()} &lt; voltageDipLow ||
   * {@link RobotController#isBrownedOut()};
   * If power limiting is engaged, returns {@link PowerDistributionPanel#getVoltage()} &lt;
   * voltageDipLow + voltageMargin || {@link RobotController#isBrownedOut()};
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
   * Scale the priority of a given {@link PowerManageable} using an inverse natural
   * exponential. \(\frac{h}{e^{\left(h-x\right)}}\) where h is the
   * highest priority and x is the priority.
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
  public boolean registerPowerManageable(@NotNull PowerManageable toRegister) {
    return (powerManageables.put(toRegister, new CachedPowerProperties(toRegister)) == null);
  }

  /**
   * Registers a group of {@link PowerManageable}s. Calls registerPowerManager().
   *
   * @param toRegister The {@link PowerManageable}s to register.
   * @return A map of PowerManageables with the key true if the PowerManager did not already contain
   * the specified element
   */
  public synchronized Map<PowerManageable, Boolean> registerPowerManageables(
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
    return powerManageables.remove(toUnregister) != null;
  }

  /**
   * Unregisters a group of {@link PowerManageable}s. Calls
   * {@link PowerManager#unregisterPowerManageable(PowerManageable)}}.
   *
   * @param toUnregister The {@link PowerManageable}s to unregister.
   * @return A map of PowerManageables with the key true if the PowerManager contained the specified
   * element
   */
  public synchronized Map<PowerManageable, Boolean> unregisterPowerManageables(
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

  @NotNull
  public BiFunction<Double, Double, Double> getPriorityScalingFunction() {
    return priorityScalingFunction;
  }

  public void setPriorityScalingFunction(
      @NotNull BiFunction<Double, Double, Double> priorityScalingFunction) {
    this.priorityScalingFunction = priorityScalingFunction;
  }

  /**
   * Separate method to block {@link PowerManageable} registration/unregistration while actually
   * scaling the power.
   */
  private synchronized void scalePower() {
    // If the PowerManageable has PowerTelemetry, we'll scale it using the arbitrary function.
    // Otherwise, it'll be scaled using what power remains.

    highestPriority = Collections.max(powerManageables.keySet()).getPriority();
    // The amount of our current output we need to be at
    final double percentToTarget = RobotController.getBatteryVoltage() / voltageTarget;
    final double totalCurrentDraw = getTotalCurrent.getAsDouble();
    final double currentNeedToDecrease = (1 - percentToTarget) * totalCurrentDraw;

    // Reset the totals
    priorityUnscaledTotal = 0;
    priorityScaledTotal = 0;
    priorityScaledNoTelemetryTotal = 0;
    currentUnscaledTotal = 0;
    currentScaledTotal = 0;
    noTelemetryCount = 0;

    powerManageables.values().forEach(CachedPowerProperties::refreshTelemetry);

    // Decide the split for how much we'll use the fancy scaling on and how much we'll use the
    // simple flat scaling on.
    final double percentSimpleScaling = priorityScaledNoTelemetryTotal / priorityScaledTotal;
    final double percentFancyScaling = 1 - percentSimpleScaling;

    // IF THERE IS TELEMETRY
    // Divide each scaled power by the total scaled power to find how much of the total power
    // this individual constitutes. Then multiply that by the percent decrease we actually want
    // to happen.

    // IF THERE IS NOT
    // Do a flat scale since we don't know how to break it up to hit the total

    final double cachedMathHasTelemetry = percentFancyScaling * percentToTarget /
        currentScaledTotal;
    final double cachedMathNoTelemetry = percentSimpleScaling * percentToTarget;

    for (CachedPowerProperties currentProperties : powerManageables.values()) {
      final double percentToDecreaseTo = finiteMath(
          currentProperties.getCurrentUnscaled().isPresent() ?
              cachedMathHasTelemetry * currentProperties.getCurrentScaled().get()
              : cachedMathNoTelemetry, 1);
      // Set the percentToDecreaseTo to the current we want to have out of the present
      // current times amount we want to use with fancy scaling, with divide by zero checking
      currentProperties.manageable.setPercentOutputLimit(percentToDecreaseTo);
    }
  }

  @NotNull
  public DoubleSupplier getGetTotalCurrent() {
    return getTotalCurrent;
  }

  public void setGetTotalCurrent(@NotNull DoubleSupplier getTotalCurrent) {
    this.getTotalCurrent = getTotalCurrent;
  }


  private class CachedPowerProperties {

    @NotNull
    private Double priorityUnscaled;
    @NotNull
    private Double priorityScaled;
    @Nullable
    private Double currentUnscaled;
    @Nullable
    private Double currentScaled;
    @NotNull
    private PowerManageable manageable;

    public CachedPowerProperties(@NotNull PowerManageable manageable) {
      this.manageable = manageable;
      refreshTelemetry();
    }

    public void refreshTelemetry() {
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

    @NotNull
    private Double getPriorityUnscaled() {
      return priorityUnscaled;
    }

    @NotNull
    private Double getPriorityScaled() {
      return priorityScaled;
    }

    @NotNull
    private Optional<Double> getCurrentUnscaled() {
      return Optional.ofNullable(currentUnscaled);
    }

    @NotNull
    private Optional<Double> getCurrentScaled() {
      return Optional.ofNullable(currentScaled);
    }
  }

  /**
   * In case the PDP is being funky, this provides a way to get the total power from the
   * controllers instead.
   */
  public class GetPowerFromControllersDoubleSupplier implements DoubleSupplier {

    @Override
    public synchronized double getAsDouble() {
      double totalCurrent = 0;
      for (PowerManageable currentManageable : powerManageables.keySet()) {
        if (currentManageable.getPowerTelemetry().isPresent()) {
          totalCurrent += currentManageable.getPowerTelemetry().get().getCurrent();
        }
      }
      return totalCurrent;
    }
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    builder.setSmartDashboardType("PowerManager");
    builder.addBooleanProperty("isVoltageDipping", this::isVoltageDipping, null);
    builder.addBooleanProperty("isLimiting", this::isLimiting, null);
    builder.addDoubleProperty("powerTime", this::getPowerTime, null);
    builder.addBooleanProperty("running", this::isRunning, this::setRunning);
    builder.addDoubleProperty("totalPower", this.getGetTotalCurrent(), null);
    builder.addDoubleProperty("updateDelay", this::getUpdateDelay,
        value -> setUpdateDelay(Math.toIntExact(Math.round(value))));
    builder.addDoubleProperty("voltageDipLow", this::getVoltageDipLow, this::setVoltageDipLow);
    builder.addDoubleProperty("voltageMargin", this::getVoltageMargin, this::setVoltageMargin);
    builder.addDoubleProperty("voltageDipLength", this::getVoltageDipLength,
        this::setVoltageDipLength);
    builder.addDoubleProperty("voltageTarget", this::getVoltageTarget, this::setVoltageTarget);
  }

}
