package org.team1540.rooster.motionprofiling;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;

/**
 * Functional interface to pass commanded values from {@link FollowProfile} to the motors.
 *
 * @see FollowProfile
 */
@FunctionalInterface
public interface SetpointConsumer {

  /**
   * Sets the setpoint of the motors to be profiled. Usually implemented using the {@link
   * com.ctre.phoenix.motorcontrol.can.TalonSRX#set(ControlMode, double, DemandType, double)
   * set(ControlMode, double, DemandType, double)} method of CTRE's {@link
   * com.ctre.phoenix.motorcontrol.can.TalonSRX TalonSRX}, with the {@code ControlMode} set to
   * {@link ControlMode#Position} and the {@code DemandType} set to {@link
   * DemandType#ArbitraryFeedForward}.
   * <p>
   * See the {@linkplain org.team1540.rooster.motionprofiling package documentation} for an
   * explanation of profile units, bump units, etc..
   *
   * @param setpoint The position PID setpoint, in profile units. (Unit conversion to Talon SRX
   * native units etc. should be performed within this method.)
   * @param bump The throttle bump to apply, in bump units.
   */
  void set(double setpoint, double bump);
}
