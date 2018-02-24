package org.team1540.base;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.command.Subsystem;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.team1540.base.power.PowerManageable;
import org.team1540.base.power.PowerManager;
import org.team1540.base.power.PowerTelemetry;
import org.team1540.base.wrappers.ChickenController;
import org.team1540.base.wrappers.ChickenTalon;
import org.team1540.base.wrappers.ChickenVictor;


/**
 * Simple implementation of core {@link Subsystem} related interfaces. Makes it quick and easy to
 * build a basic zuko.
 */
@SuppressWarnings("unused")
public class ChickenSubsystem extends Subsystem implements PowerManageable {

  private double priority = 0.0;

  private PowerTelemetry powerTelemetry = new PowerTelemetry() {
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
    return motors.containsKey(o);
  }

  public double add(ChickenController o) {
    return motors.put(o, 1d);
  }

  public void add(ChickenController... os) {
    addAll(Arrays.asList(os));
  }

  public double remove(ChickenController o) {
    return motors.remove(o);
  }

  public void remove(ChickenController... os) {
    removeAll(Arrays.asList(os));
  }

  public boolean containsAll(Collection<ChickenController> controllers) {
    for (ChickenController c : controllers) {
      if (!motors.containsKey(c)) {
        return false;
      }
    }
    return true;
  }

  public void addAll(Collection<? extends ChickenController> controllers) {
    for (ChickenController c : controllers) {
      motors.put(c, 1d);
    }
  }

  public void removeAll(Collection<ChickenController> controllers) {
    for (ChickenController c : controllers) {
      motors.remove(c);
    }
  }

  public void clear() {
    motors.clear();
  }

  private final Object powerLock = new Object();

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

  /**
   * Returns an object that gives the aggregate data from {@link ChickenTalon}s if all motors are
   * either {@link ChickenTalon}s or slaved {@link ChickenVictor}s.
   * Else, returns null.
   * Basically, either the entire subsystem has telemetry or none of it does.
   *
   * @return The according {@link PowerTelemetry} object.
   */
  @Override
  public PowerTelemetry getPowerTelemetry() {
    // This unforunately needs to be checked at runtime, as if a Victor is slaved can change
    // really at any time
    for (ChickenController currentMotor : motors.keySet()) {
      if (currentMotor instanceof ChickenVictor && !currentMotor.getControlMode().equals
          (ControlMode.Follower)) {
        return null;
      }
    }
    return powerTelemetry;
  }
}
