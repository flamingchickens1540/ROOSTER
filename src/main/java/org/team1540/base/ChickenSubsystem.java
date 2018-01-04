package org.team1540.base;

import com.ctre.CANTalon;
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

  private final Set<CANTalon> motors = new HashSet<>();

  public int size() {
    return motors.size();
  }

  public boolean isEmpty() {
    return motors.isEmpty();
  }

  public boolean contains(CANTalon o) {
    return motors.contains(o);
  }

  public boolean add(CANTalon canTalon) {
    return motors.add(canTalon);
  }

  public boolean remove(CANTalon o) {
    return motors.remove(o);
  }

  public boolean containsAll(Collection<CANTalon> c) {
    return motors.containsAll(c);
  }

  public boolean addAll(Collection<? extends CANTalon> c) {
    return motors.addAll(c);
  }

  public boolean removeAll(Collection<CANTalon> c) {
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

  protected void initDefaultCommand() {

  }

  public double getPriority() {
    return priority;
  }

  public void setPriority(double priority) {
    this.priority = priority;
  }

  public double getCurrent() {
    double sum = 0;
    for (CANTalon currentMotor : motors) {
      sum += currentMotor.getOutputCurrent();
    }
    return sum;
  }

  public void limitPower(double limit) {
    synchronized (powerLock) {
      for (CANTalon currentMotor : motors) {
        currentMotor.EnableCurrentLimit(true);
        currentMotor.setCurrentLimit(Math.toIntExact(Math.round(limit / motors.size())));
      }
    }
  }

  public void stopLimitingPower() {
    synchronized (powerLock) {
      for (CANTalon currentMotor : motors) {
        currentMotor.EnableCurrentLimit(false);
      }
    }
  }
}
