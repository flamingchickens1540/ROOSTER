package org.team1540.base;

import edu.wpi.first.wpilibj.command.Subsystem;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.team1540.base.power.PowerManageable;
import org.team1540.base.wrappers.ChickenTalon;


/**
 * Simple implementation of core {@link Subsystem} related interfaces. Makes it quick and easy to
 * build a basic robot.
 */
@SuppressWarnings("unused")
public class ChickenSubsystem extends Subsystem implements PowerManageable {

  private double priority = 0.0;

  private final Set<ChickenTalon> motors = new HashSet<>();

  public int size() {
    return motors.size();
  }

  public boolean isEmpty() {
    return motors.isEmpty();
  }

  public boolean contains(ChickenTalon o) {
    return motors.contains(o);
  }

  public boolean add(ChickenTalon motor) {
    return motors.add(motor);
  }

  public boolean remove(ChickenTalon o) {
    return motors.remove(o);
  }

  public boolean containsAll(Collection<ChickenTalon> c) {
    return motors.containsAll(c);
  }

  public boolean addAll(Collection<? extends ChickenTalon> c) {
    return motors.addAll(c);
  }

  public boolean removeAll(Collection<ChickenTalon> c) {
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
  public double getCurrent() {
    double sum = 0;
    for (ChickenTalon currentMotor : motors) {
      sum += currentMotor.getOutputCurrent();
    }
    return sum;
  }

  @Override
  public void limitPower(double limit) {
    synchronized (powerLock) {
      for (ChickenTalon currentMotor : motors) {
        currentMotor.enableCurrentLimit(true);
        currentMotor.configContinuousCurrentLimit(Math.toIntExact(Math.round(limit / motors.size())), 0);
      }
    }
  }

  @Override
  public void stopLimitingPower() {
    synchronized (powerLock) {
      for (ChickenTalon currentMotor : motors) {
        currentMotor.enableCurrentLimit(false);
      }
    }
  }
}
