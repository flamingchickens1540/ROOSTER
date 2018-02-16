package org.team1540.base;

import edu.wpi.first.wpilibj.command.Subsystem;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.team1540.base.power.PowerManageable;
import org.team1540.base.wrappers.ChickenController;


/**
 * Simple implementation of core {@link Subsystem} related interfaces. Makes it quick and easy to
 * build a basic robot.
 */
@SuppressWarnings("unused")
public class ChickenSubsystem extends Subsystem implements PowerManageable {

  private double priority = 0.0;

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
  }

  public ChickenSubsystem() {
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
  public double getPowerConsumption() {
    double sum = 0;
    for (ChickenController currentMotor : motors.keySet()) {
      sum += currentMotor.getMotorOutputVoltage() * currentMotor.getOutputCurrent();
    }
    return sum;
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
}
