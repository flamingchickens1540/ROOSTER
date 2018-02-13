package org.team1540.base;

import edu.wpi.first.wpilibj.command.Subsystem;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
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
   * A set of all master motors to be power managed. Slaves (including ChickenVictors) will be power managed through that
   */
  private final Set<ChickenController> motors = new HashSet<>();

  public int size() {
    return motors.size();
  }

  public boolean isEmpty() {
    return motors.isEmpty();
  }

  public boolean contains(ChickenController o) {
    return motors.contains(o);
  }

  public boolean add(ChickenController o) {
    return motors.add(o);
  }

  public boolean add(ChickenController... os) {
    return addAll(Arrays.asList(os));
  }

  public boolean remove(ChickenController o) {
    return motors.remove(o);
  }

  public boolean remove(ChickenController... os) {
    return removeAll(Arrays.asList(os));
  }

  public boolean containsAll(Collection<ChickenController> c) {
    return motors.containsAll(c);
  }

  public boolean addAll(Collection<? extends ChickenController> c) {
    return motors.addAll(c);
  }

  public boolean removeAll(Collection<ChickenController> c) {
    return motors.removeAll(c);
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
  public double getVoltage() {
    double sum = 0;
    for (ChickenController currentMotor : motors) {
      sum += currentMotor.getMotorOutputVoltage();
    }
    return sum;
  }

  @Override
  public void setLimit(double limit) {
    synchronized (powerLock) {
      for (ChickenController currentMotor : motors) {
        currentMotor
            .configPeakOutputForward(Math.toIntExact(Math.round(limit / motors.size())));
        currentMotor
            .configPeakOutputReverse(-Math.toIntExact(Math.round(limit / motors.size())));
        currentMotor.configForwardSoftLimitEnable(true);
        currentMotor.configReverseSoftLimitEnable(true);
      }
    }
  }

  @Override
  public void stopLimitingPower() {
    synchronized (powerLock) {
      for (ChickenController currentMotor : motors) {
        currentMotor.configForwardSoftLimitEnable(false);
        currentMotor.configReverseSoftLimitEnable(false);
      }
    }
  }
}
