package org.team1540.base;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.team1540.base.power.PowerManageable;
import org.team1540.base.power.PowerManager;
import org.team1540.base.power.PowerTelemetry;
import org.team1540.base.wrappers.ChickenController;
import org.team1540.base.wrappers.ChickenTalon;
import org.team1540.base.wrappers.ChickenVictor;


/**
 * Simple implementation of core {@link Subsystem} related interfaces. Makes it quick and easy to
 * build a basic robot.
 */
@SuppressWarnings("unused")
public class ChickenSubsystem extends Subsystem implements PowerManageable {

  private double noiseThreshold = 0.02;
  private double priority = 0.0;
  private final PowerTelemetry allMotorTelemetry = new PowerTelemetry() {
    @Override
    public double getCurrent() {
      double sum = 0;
      for (ChickenController motor : motors.keySet()) {
        if (motor instanceof ChickenTalon) {
          sum += motor.getOutputCurrent();
        }
      }
      return sum;
    }

    @Override
    public double getVoltage() {
      double sum = 0;
      for (ChickenController motor : motors.keySet()) {
        sum += motor.getMotorOutputVoltage();
      }
      return sum / motors.size();
    }
  };
  private boolean telemetryCacheValid = false;
  private PowerTelemetry telemetry = allMotorTelemetry;
  private final Object powerLock = new Object();
  /**
   * Map of motors in this subsystem to be power managed, with the key being the motor and the value
   * being the current percentOutput the motor is at.
   */
  private final Map<ChickenController, Double> motors = new HashMap<>();

  public int size() {
    return motors.size();
  }

  public boolean isEmpty() {
    return motors.isEmpty();
  }

  public boolean contains(ChickenController o) {
    synchronized (powerLock) {
      return motors.containsKey(o);
    }
  }

  public double add(ChickenController o) {
    invalidateTelemetryCache();
    synchronized (powerLock) {
      return motors.put(o, 1d);
    }
  }

  public void add(ChickenController... os) {
    addAll(Arrays.asList(os));
  }

  public double remove(ChickenController o) {
    invalidateTelemetryCache();
    synchronized (powerLock) {
      return motors.remove(o);
    }
  }

  public void remove(ChickenController... os) {
    removeAll(Arrays.asList(os));
  }

  public boolean containsAll(Collection<ChickenController> controllers) {
    for (ChickenController c : controllers) {
      if (!contains(c)) {
        return false;
      }
    }
    return true;
  }

  public void addAll(Collection<? extends ChickenController> controllers) {
    for (ChickenController c : controllers) {
      add(c);
    }
  }

  public void removeAll(Collection<ChickenController> controllers) {
    for (ChickenController c : controllers) {
      remove(c);
    }
  }

  public void clear() {
    invalidateTelemetryCache();
    synchronized (powerLock) {
      motors.clear();
    }
  }

  public ChickenSubsystem(String name) {
    super(name);
    realConstructor();
  }

  public ChickenSubsystem() {
    super();
    realConstructor();
  }

  private void realConstructor() {
    PowerManager.getInstance().registerPowerManageable(this);
  }

  @Override
  protected void initDefaultCommand() {

  }

  @Override
  public double getPriority() {
    return priority;
  }

  @Override
  public void setPriority(double priority) {
    this.priority = priority;
  }

  @Override
  public double getPercentOutputLimit() {
    double sum = 0;
    for (ChickenController currentMotors : motors.keySet()) {
      sum += motors.get(currentMotors);
    }
    return sum / motors.size();
  }

  @Override
  public void setPercentOutputLimit(double limit) {
    synchronized (powerLock) {
      for (ChickenController currentMotor : motors.keySet()) {
        double newLimit = motors.get(currentMotor) * limit;
        if (newLimit > 1) {
          // If the limit is above 1, set it to 1 to keep it from increasing forver
          newLimit = 1;
        } else if (newLimit < noiseThreshold) {
          // If the new limit is below the threshold, introduce some noise to keep it from being
          // stuck at 0
          newLimit = Math.random() * noiseThreshold;
        }
        currentMotor.configPeakOutputForward(newLimit);
        currentMotor.configPeakOutputReverse(-newLimit);
        motors.put(currentMotor, newLimit);
      }
    }
  }

  @Override
  public void stopLimitingPower() {
    synchronized (powerLock) {
      for (ChickenController currentMotor : motors.keySet()) {
        currentMotor.configPeakOutputForward(1);
        currentMotor.configPeakOutputReverse(-1);
        motors.put(currentMotor, 1d);
      }
    }
  }

  public boolean isTelemetryCacheValid() {
    return telemetryCacheValid;
  }

  public void invalidateTelemetryCache() {
    telemetryCacheValid = false;
  }

  private void setTelemetry(PowerTelemetry t) {
    telemetry = t;
    telemetryCacheValid = true;
  }

  /**
   * Gets the threshold below which the output will be randomized to prevent the output from
   * being stuck at 0.
   *
   * @return A float between 0 and 1 inclusive.
   */
  public double getNoiseThreshold() {
    return noiseThreshold;
  }

  /**
   * Gets the threshold below which the output will be randomized to prevent the output from
   * being stuck at 0.
   *
   * @param noiseThreshold A float between 0 and 1 inclusive.
   */
  public void setNoiseThreshold(double noiseThreshold) {
    if (noiseThreshold < 0 || noiseThreshold > 1) {
      throw new IllegalArgumentException("noiseThreshold must be between 0 and 1 inclusive, got "
          + noiseThreshold);
    }
    this.noiseThreshold = noiseThreshold;
  }


  /**
   * Returns an object that gives the aggregate data from {@link ChickenTalon}s if all motors are
   * either {@link ChickenTalon}s or slaved {@link ChickenVictor}s.
   * Else, returns null.
   * Basically, either the entire subsystem has telemetry or none of it does.
   * Note that to improve performance, this status is cached and only updated when either a motor
   * is added, removed, or the cache is externally invalidaded using
   * {@link #invalidateTelemetryCache()}.
   *
   * Override me as necessary (e.g. for all-Victor subsystems where you'd be getting the
   * telemetry from the PDP.)
   *
   * @return The according {@link PowerTelemetry} object.
   */
  @Override
  public Optional<PowerTelemetry> getPowerTelemetry() {
    if (telemetryCacheValid) {
      return Optional.ofNullable(telemetry);
    } else {
      // This unforunately needs to be checked at runtime, as if a Victor is slaved can change
      // really at any time
      for (ChickenController currentMotor : motors.keySet()) {
        if (currentMotor instanceof ChickenVictor && !currentMotor.getControlMode().equals
            (ControlMode.Follower)) {
          setTelemetry(null);
          return Optional.empty();
        }
      }
      setTelemetry(allMotorTelemetry);
      return Optional.of(allMotorTelemetry);
    }
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    super.initSendable(builder);
    sendablePowerInfo(builder);
    builder.addBooleanProperty("telemetryCacheValid", this::isTelemetryCacheValid, null);
    builder.addDoubleProperty("noiseThreshold", this::getNoiseThreshold, this::setNoiseThreshold);
  }
}
