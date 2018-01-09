package org.team1540.base;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.command.Subsystem;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.team1540.base.power.PowerManageable;


/**
 * Simple implementation of core {@link Subsystem} related interfaces. Makes it quick and easy to
 * build a basic robot.
 */
@SuppressWarnings("unused")
public class ChickenSubsystem extends Subsystem implements PowerManageable {

  private double priority = 0.0;

  private final Set<TalonSRX> motors = new HashSet<TalonSRX>();

  public int size() {
    return motors.size();
  }

  public boolean isEmpty() {
    return motors.isEmpty();
  }

  public boolean contains(TalonSRX o) {
    return motors.contains(o);
  }

  public boolean add(TalonSRX TalonSRX) {
    return motors.add(TalonSRX);
  }

  public boolean remove(TalonSRX o) {
    return motors.remove(o);
  }

  public boolean containsAll(Collection<TalonSRX> c) {
    return motors.containsAll(c);
  }

  public boolean addAll(Collection<? extends TalonSRX> c) {
    return motors.addAll(c);
  }

  public boolean removeAll(Collection<TalonSRX> c) {
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
    for (TalonSRX currentMotor : motors) {
      sum += currentMotor.getOutputCurrent();
    }
    return sum;
  }

  @Override
  public void limitPower(double limit) {
    synchronized (powerLock) {
      for (TalonSRX currentMotor : motors) {
        currentMotor.enableCurrentLimit(true);
        currentMotor.configContinuousCurrentLimit(Math.toIntExact(Math.round(limit / motors.size())), 0);
      }
    }
  }

  @Override
  public void stopLimitingPower() {
    synchronized (powerLock) {
      for (TalonSRX currentMotor : motors) {
        currentMotor.enableCurrentLimit(false);
      }
    }
  }
}
