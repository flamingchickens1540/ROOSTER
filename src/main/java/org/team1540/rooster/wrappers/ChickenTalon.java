package org.team1540.rooster.wrappers;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.RemoteFeedbackDevice;
import com.ctre.phoenix.motorcontrol.RemoteLimitSwitchSource;
import com.ctre.phoenix.motorcontrol.RemoteSensorSource;
import com.ctre.phoenix.motorcontrol.SensorTerm;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.MotControllerJNI;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

/**
 * Wrapper around a {@link TalonSRX} adding some features (that really should already be there) as
 * well as making functions easier to call.
 */
public class ChickenTalon extends TalonSRX implements ChickenController {

  ControlMode controlMode = ControlMode.PercentOutput;
  int defaultPidIdx = 0;
  int defaultTimeoutMs = 0;
  private double peakOutputForward = 1;
  private double peakOutputReverse = -1;

  public ChickenTalon(int deviceNumber) {
    super(deviceNumber);
  }

  @Override
  public ErrorCode clearMotionProfileHasUnderrun() {
    return super.clearMotionProfileHasUnderrun(defaultTimeoutMs);
  }

  @Override
  public ErrorCode clearStickyFaults() {
    return super.clearStickyFaults(defaultTimeoutMs);
  }

  /**
   * Sets the allowable closed-loop error in the given parameter slot.
   *
   * @param slotIdx Parameter slot for the constant.
   * @param allowableClosedLoopError Value of the allowable closed-loop error.
   * @return Error Code generated by function. 0 indicates no error.
   */
  @Override
  public ErrorCode configAllowableClosedloopError(int slotIdx, int allowableClosedLoopError) {
    return super
        .configAllowableClosedloopError(slotIdx, allowableClosedLoopError, defaultTimeoutMs);
  }

  /**
   * Configures the closed-loop ramp rate of throttle output.
   *
   * @param secondsFromNeutralToFull Minimum desired time to go from neutral to full throttle. A
   * value of '0' will disable the ramp.
   * @return Error Code generated by function. 0 indicates no error.
   */
  @Override
  public ErrorCode configClosedloopRamp(double secondsFromNeutralToFull) {
    return super.configClosedloopRamp(secondsFromNeutralToFull, defaultTimeoutMs);
  }

  public ErrorCode configContinuousCurrentLimit(int amps) {
    return super.configContinuousCurrentLimit(amps, defaultTimeoutMs);
  }

  @Override
  public ErrorCode configForwardLimitSwitchSource(LimitSwitchSource type,
      LimitSwitchNormal normalOpenOrClose) {
    return super.configForwardLimitSwitchSource(type, normalOpenOrClose, defaultTimeoutMs);
  }

  /**
   * Configures the forward limit switch for a remote source.
   *
   * @param type Remote limit switch source. @see #LimitSwitchSource
   * @param normalOpenOrClose Setting for normally open or normally closed.
   * @param deviceID Device ID of remote source.
   * @return Error Code generated by function. 0 indicates no error.
   */
  @Override
  public ErrorCode configForwardLimitSwitchSource(RemoteLimitSwitchSource type,
      LimitSwitchNormal normalOpenOrClose, int deviceID) {
    return super
        .configForwardLimitSwitchSource(type, normalOpenOrClose, deviceID, defaultTimeoutMs);
  }

  /**
   * Configures the forward soft limit enable.
   *
   * @param enable Forward Sensor Position Limit Enable.
   * @return Error Code generated by function. 0 indicates no error.
   */
  @Override
  public ErrorCode configForwardSoftLimitEnable(boolean enable) {
    return super.configForwardSoftLimitEnable(enable, defaultTimeoutMs);
  }

  /**
   * Configures the forward soft limit threhold.
   *
   * @param forwardSensorLimit Forward Sensor Position Limit (in Raw Sensor Units).
   * @return Error Code generated by function. 0 indicates no error.
   */
  @Override
  public ErrorCode configForwardSoftLimitThreshold(int forwardSensorLimit) {
    return super.configForwardSoftLimitThreshold(forwardSensorLimit, defaultTimeoutMs);
  }

  /**
   * Gets the value of a custom parameter.
   *
   * @param paramIndex Index of custom parameter.
   * @return Value of the custom param.
   */
  @Override
  public int configGetCustomParam(int paramIndex) {
    return super.configGetCustomParam(paramIndex, defaultTimeoutMs);
  }

  /**
   * Gets a parameter.
   *
   * @param param Parameter enumeration.
   * @param ordinal Ordinal of parameter.
   * @return Value of parameter.
   */
  @Override
  public double configGetParameter(ParamEnum param, int ordinal) {
    return super.configGetParameter(param, ordinal, defaultTimeoutMs);
  }

  /**
   * Gets a parameter.
   *
   * @param param Parameter enumeration.
   * @param ordinal Ordinal of parameter.
   * @return Value of parameter.
   */
  @Override
  public double configGetParameter(int param, int ordinal) {
    return super.configGetParameter(param, ordinal, defaultTimeoutMs);
  }

  /**
   * Sets the maximum integral accumulator in the given parameter slot.
   *
   * @param slotIdx Parameter slot for the constant.
   * @param iaccum Value of the maximum integral accumulator.
   * @return Error Code generated by function. 0 indicates no error.
   */
  @Override
  public ErrorCode configMaxIntegralAccumulator(int slotIdx, double iaccum) {
    return super.configMaxIntegralAccumulator(slotIdx, iaccum, defaultTimeoutMs);
  }

  /**
   * Sets the Motion Magic Acceleration.
   *
   * @param sensorUnitsPer100msPerSec Motion Magic Acceleration (in Raw Sensor Units per 100 ms per
   * second).
   * @return Error Code generated by function. 0 indicates no error.
   */
  @Override
  public ErrorCode configMotionAcceleration(int sensorUnitsPer100msPerSec) {
    return super.configMotionAcceleration(sensorUnitsPer100msPerSec, defaultTimeoutMs);
  }

  /**
   * Sets the Motion Magic Cruise Velocity.
   *
   * @param sensorUnitsPer100ms Motion Magic Cruise Velocity (in Raw Sensor Units per 100 ms).
   * @return Error Code generated by function. 0 indicates no error.
   */
  @Override
  public ErrorCode configMotionCruiseVelocity(int sensorUnitsPer100ms) {
    return super.configMotionCruiseVelocity(sensorUnitsPer100ms, defaultTimeoutMs);
  }

  /**
   * Configures the output deadband percentage.
   *
   * @param percentDeadband Desired deadband percentage. Minimum is 0.1%, Maximum is 25%. Pass 0.04
   * for 4%.
   * @return Error Code generated by function. 0 indicates no error.
   */
  @Override
  public ErrorCode configNeutralDeadband(double percentDeadband) {
    return super.configNeutralDeadband(percentDeadband, defaultTimeoutMs);
  }

  /**
   * Configures the forward nominal output percentage.
   *
   * @param percentOut Nominal (minimum) percent output.
   * @return Error Code generated by function. 0 indicates no error.
   */
  @Override
  public ErrorCode configNominalOutputForward(double percentOut) {
    return super.configNominalOutputForward(percentOut, defaultTimeoutMs);
  }

  /**
   * Configures the reverse nominal output percentage.
   *
   * @param percentOut Nominal (minimum) percent output.
   * @return Error Code generated by function. 0 indicates no error.
   */
  @Override
  public ErrorCode configNominalOutputReverse(double percentOut) {
    return super.configNominalOutputReverse(percentOut, defaultTimeoutMs);
  }

  /**
   * Configures the open-loop ramp rate of throttle output.
   *
   * @param secondsFromNeutralToFull Minimum desired time to go from neutral to full throttle. A
   * value of '0' will disable the ramp.
   * @return Error Code generated by function. 0 indicates no error.
   */
  @Override
  public ErrorCode configOpenloopRamp(double secondsFromNeutralToFull) {
    return super.configOpenloopRamp(secondsFromNeutralToFull, defaultTimeoutMs);
  }

  public ErrorCode configPeakCurrentDuration(int milliseconds) {
    return super.configPeakCurrentDuration(milliseconds, defaultTimeoutMs);
  }

  public ErrorCode configPeakCurrentLimit(int amps) {
    return super.configPeakCurrentLimit(amps, defaultTimeoutMs);
  }

  @Override
  public ErrorCode configPeakOutputForward(double percentOut) {
    peakOutputForward = percentOut;
    return super.configPeakOutputForward(percentOut, defaultTimeoutMs);
  }

  @Override
  public ErrorCode configPeakOutputReverse(double percentOut) {
    peakOutputReverse = percentOut;
    return super.configPeakOutputReverse(percentOut, defaultTimeoutMs);
  }

  @Override
  public double getPeakOutputForward() {
    return peakOutputForward;
  }

  @Override
  public double getPeakOutputReverse() {
    return peakOutputReverse;
  }

  @Override
  public ErrorCode configRemoteFeedbackFilter(int deviceID, RemoteSensorSource remoteSensorSource,
      int remoteOrdinal) {
    return super
        .configRemoteFeedbackFilter(deviceID, remoteSensorSource, remoteOrdinal, defaultTimeoutMs);
  }

  public ErrorCode configReverseLimitSwitchSource(LimitSwitchSource type,
      LimitSwitchNormal normalOpenOrClose) {
    return super.configReverseLimitSwitchSource(type, normalOpenOrClose, defaultTimeoutMs);
  }

  /**
   * Configures the reverse limit switch for a remote source.
   *
   * @param type Remote limit switch source. @see #LimitSwitchSource
   * @param normalOpenOrClose Setting for normally open or normally closed.
   * @param deviceID Device ID of remote source.
   * @return Error Code generated by function. 0 indicates no error.
   */
  @Override
  public ErrorCode configReverseLimitSwitchSource(RemoteLimitSwitchSource type,
      LimitSwitchNormal normalOpenOrClose, int deviceID) {
    return super
        .configReverseLimitSwitchSource(type, normalOpenOrClose, deviceID, defaultTimeoutMs);
  }

  /**
   * Configures the reverse soft limit enable.
   *
   * @param enable Reverse Sensor Position Limit Enable.
   * @return Error Code generated by function. 0 indicates no error.
   */
  @Override
  public ErrorCode configReverseSoftLimitEnable(boolean enable) {
    return super.configReverseSoftLimitEnable(enable, defaultTimeoutMs);
  }

  /**
   * Configures the reverse soft limit threshold.
   *
   * @param reverseSensorLimit Reverse Sensor Position Limit (in Raw Sensor Units).
   * @return Error Code generated by function. 0 indicates no error.
   */
  @Override
  public ErrorCode configReverseSoftLimitThreshold(int reverseSensorLimit) {
    return super.configReverseSoftLimitThreshold(reverseSensorLimit, defaultTimeoutMs);
  }

  /**
   * Select the remote feedback device for the motor controller.
   *
   * @param feedbackDevice Remote Feedback Device to select.
   * @param pidIdx 0 for Primary closed-loop. 1 for cascaded closed-loop.
   * @return Error Code generated by function. 0 indicates no error.
   */
  @Override
  public ErrorCode configSelectedFeedbackSensor(RemoteFeedbackDevice feedbackDevice, int pidIdx) {
    return super.configSelectedFeedbackSensor(feedbackDevice, pidIdx, defaultTimeoutMs);
  }

  /**
   * Select the remote feedback device for the motor controller.
   *
   * @param feedbackDevice Remote Feedback Device to select.
   * @return Error Code generated by function. 0 indicates no error.
   */
  @Override
  public ErrorCode configSelectedFeedbackSensor(RemoteFeedbackDevice feedbackDevice) {
    return super.configSelectedFeedbackSensor(feedbackDevice, defaultPidIdx, defaultTimeoutMs);
  }

  /**
   * Select the feedback device for the motor controller.
   *
   * @param feedbackDevice Feedback Device to select.
   * @param pidIdx The PID IDX to use.
   * @return Error Code generated by function. 0 indicates no error.
   */
  @Override
  public ErrorCode configSelectedFeedbackSensor(FeedbackDevice feedbackDevice, int pidIdx) {
    return super.configSelectedFeedbackSensor(feedbackDevice, pidIdx, defaultTimeoutMs);
  }

  /**
   * Select the feedback device for the motor controller.
   *
   * @param feedbackDevice Feedback Device to select.
   * @return Error Code generated by function. 0 indicates no error.
   */
  @Override
  public ErrorCode configSelectedFeedbackSensor(FeedbackDevice feedbackDevice) {
    return super.configSelectedFeedbackSensor(feedbackDevice, defaultPidIdx, defaultTimeoutMs);
  }

  @Override
  public ErrorCode configSensorTerm(SensorTerm sensorTerm, FeedbackDevice feedbackDevice) {
    return super.configSensorTerm(sensorTerm, feedbackDevice, defaultTimeoutMs);
  }

  /**
   * Sets the value of a custom parameter.
   *
   * @param newValue Value for custom parameter.
   * @param paramIndex Index of custom parameter.
   * @return Error Code generated by function. 0 indicates no error.
   */
  @Override
  public ErrorCode configSetCustomParam(int newValue, int paramIndex) {
    return super.configSetCustomParam(newValue, paramIndex, defaultTimeoutMs);
  }

  /**
   * Sets a parameter.
   *
   * @param param Parameter enumeration.
   * @param value Value of parameter.
   * @param subValue Subvalue for parameter. Maximum value of 255.
   * @param ordinal Ordinal of parameter.
   * @return Error Code generated by function. 0 indicates no error.
   */
  @Override
  public ErrorCode configSetParameter(ParamEnum param, double value, int subValue, int ordinal) {
    return super.configSetParameter(param, value, subValue, ordinal, defaultTimeoutMs);
  }

  /**
   * Sets a parameter.
   *
   * @param param Parameter enumeration.
   * @param value Value of parameter.
   * @param subValue Subvalue for parameter. Maximum value of 255.
   * @param ordinal Ordinal of parameter.
   * @return Error Code generated by function. 0 indicates no error.
   */
  @Override
  public ErrorCode configSetParameter(int param, double value, int subValue, int ordinal) {
    return super.configSetParameter(param, value, subValue, ordinal, defaultTimeoutMs);
  }

  /**
   * Sets the period over which velocity measurements are taken.
   *
   * @param period Desired period for the velocity measurement. @see #VelocityMeasPeriod
   * @return Error Code generated by function. 0 indicates no error.
   */
  @Override
  public ErrorCode configVelocityMeasurementPeriod(int period) {
    int retval = MotControllerJNI
        .ConfigVelocityMeasurementPeriod(m_handle, period, defaultTimeoutMs);
    return ErrorCode.valueOf(retval);
  }

  @Override
  public ErrorCode configVelocityMeasurementWindow(int windowSize) {
    return super.configVelocityMeasurementWindow(windowSize, defaultTimeoutMs);
  }

  /**
   * Configures the Voltage Compensation saturation voltage.
   *
   * @param voltage TO-DO: Comment me!
   * @return Error Code generated by function. 0 indicates no error.
   */
  @Override
  public ErrorCode configVoltageCompSaturation(double voltage) {
    return super.configVoltageCompSaturation(voltage, defaultTimeoutMs);
  }

  /**
   * Configures the voltage measurement filter.
   *
   * @param filterWindowSamples Number of samples in the rolling average of voltage measurement.
   * @return Error Code generated by function. 0 indicates no error.
   */
  @Override
  public ErrorCode configVoltageMeasurementFilter(int filterWindowSamples) {
    return super.configVoltageMeasurementFilter(filterWindowSamples, defaultTimeoutMs);
  }

  /**
   * Sets the Integral Zone constant in the given parameter slot.
   *
   * @param slotIdx Parameter slot for the constant.
   * @param izone Value of the Integral Zone constant.
   * @return Error Code generated by function. 0 indicates no error.
   */
  @Override
  public ErrorCode config_IntegralZone(int slotIdx, int izone) {
    return super.config_IntegralZone(slotIdx, izone, defaultTimeoutMs);
  }

  /**
   * Sets the 'D' constant in the given parameter slot.
   *
   * @param slotIdx Parameter slot for the constant.
   * @param value Value of the D constant.
   * @return Error Code generated by function. 0 indicates no error.
   */
  @Override
  public ErrorCode config_kD(int slotIdx, double value) {
    return super.config_kD(slotIdx, value, defaultTimeoutMs);
  }

  /**
   * Sets the 'F' constant in the given parameter slot.
   *
   * @param slotIdx Parameter slot for the constant.
   * @param value Value of the F constant.
   * @return Error Code generated by function. 0 indicates no error.
   */
  @Override
  public ErrorCode config_kF(int slotIdx, double value) {
    return super.config_kF(slotIdx, value, defaultTimeoutMs);
  }

  /**
   * Sets the 'I' constant in the given parameter slot.
   *
   * @param slotIdx Parameter slot for the constant.
   * @param value Value of the I constant.
   * @return Error Code generated by function. 0 indicates no error.
   */
  @Override
  public ErrorCode config_kI(int slotIdx, double value) {
    return super.config_kI(slotIdx, value, defaultTimeoutMs);
  }

  /**
   * Sets the 'P' constant in the given parameter slot.
   *
   * @param slotIdx Parameter slot for the constant.
   * @param value Value of the P constant.
   * @return Error Code generated by function. 0 indicates no error.
   */
  @Override
  public ErrorCode config_kP(int slotIdx, double value) {
    return super.config_kP(slotIdx, value, defaultTimeoutMs);
  }

  /**
   * Get the position of whatever is in the analog pin of the Talon, regardless of whether it is
   * actually being used for feedback.
   *
   * @return the 24bit analog value. The bottom ten bits is the ADC (0 - 1023) on the analog pin of
   * the Talon. The upper 14 bits tracks the overflows and underflows (continuous sensor).
   */
  @Override
  public int getAnalogIn() {
    return getSensorCollection().getAnalogIn();
  }

  /**
   * Get the position of whatever is in the analog pin of the Talon, regardless of whether it is
   * actually being used for feedback.
   *
   * @return the ADC (0 - 1023) on analog pin of the Talon.
   */
  @Override
  public int getAnalogInRaw() {
    return getSensorCollection().getAnalogInRaw();
  }

  /**
   * Get the position of whatever is in the analog pin of the Talon, regardless of whether it is
   * actually being used for feedback.
   *
   * @return the value (0 - 1023) on the analog pin of the Talon.
   */
  @Override
  public int getAnalogInVel() {
    return getSensorCollection().getAnalogInVel();
  }

  /**
   * Gets the closed-loop error.
   *
   * @return Closed-loop error value.
   */
  @Override
  public int getClosedLoopError() {
    return super.getClosedLoopError(defaultPidIdx);
  }

  /**
   * Note that this does <i>not</i> return the currently set control mode in the talon wrapper and
   * may be different than the control mode set using {@link #setControlMode(ControlMode)}.
   *
   * @return The current control mode in the motor controller.
   */
  @Override
  public ControlMode getControlMode() {
    return controlMode;
  }

  @Override
  public void setControlMode(ControlMode controlMode) {
    this.controlMode = controlMode;
  }

  @Override
  public int getDefaultPidIdx() {
    return defaultPidIdx;
  }

  @Override
  public void setDefaultPidIdx(int defaultPidIdx) {
    this.defaultPidIdx = defaultPidIdx;
  }

  @Override
  public int getDefaultTimeoutMs() {
    return defaultTimeoutMs;
  }

  @Override
  public void setDefaultTimeoutMs(int defaultTimeoutMs) {
    this.defaultTimeoutMs = defaultTimeoutMs;
  }

  /**
   * Gets the encoder codes per revolution.
   *
   * @return {@code 0}.
   * @deprecated This no longer has any effect as natural units functionality has been removed.
   */
  @Override
  @Deprecated
  public double getEncoderCodesPerRev() {
    return 0;
  }

  /**
   * Sets the encoder codes per revolution for getting and setting velocity and position.
   *
   * @param encoderCodesPerRev The encoder codes per revolution to set.
   * @deprecated This no longer has any effect as natural units functionality has been removed.
   */
  @Override
  @Deprecated
  public void setEncoderCodesPerRev(double encoderCodesPerRev) {
  }

  /**
   * Gets the derivative of the closed-loop error.
   *
   * @return The error derivative value.
   */
  @Override
  public double getErrorDerivative() {
    return super.getErrorDerivative(defaultPidIdx);
  }

  /**
   * Gets the iaccum value.
   *
   * @return Integral accumulator value.
   */
  @Override
  public double getIntegralAccumulator() {
    return super.getIntegralAccumulator(defaultPidIdx);
  }

  /**
   * Gets pin state quad a.
   *
   * @return the pin state quad a.
   */
  @Override
  public boolean getPinStateQuadA() {
    return getSensorCollection().getPinStateQuadA();
  }

  /**
   * Gets pin state quad b.
   *
   * @return Digital level of QUADB pin.
   */
  @Override
  public boolean getPinStateQuadB() {
    return getSensorCollection().getPinStateQuadB();
  }

  /**
   * Gets pin state quad index.
   *
   * @return Digital level of QUAD Index pin.
   */
  @Override
  public boolean getPinStateQuadIdx() {
    return getSensorCollection().getPinStateQuadIdx();
  }

  /**
   * Gets pulse width position.
   *
   * @return the pulse width position.
   */
  @Override
  public int getPulseWidthPosition() {
    return getSensorCollection().getPulseWidthPosition();
  }

  /**
   * Gets pulse width rise to fall us.
   *
   * @return the pulse width rise to fall us.
   */
  @Override
  public int getPulseWidthRiseToFallUs() {
    return getSensorCollection().getPulseWidthRiseToFallUs();
  }

  /**
   * Gets pulse width rise to rise us.
   *
   * @return the pulse width rise to rise us.
   */
  @Override
  public int getPulseWidthRiseToRiseUs() {
    return getSensorCollection().getPulseWidthRiseToRiseUs();
  }

  /**
   * Gets pulse width velocity.
   *
   * @return the pulse width velocity.
   */
  @Override
  public int getPulseWidthVelocity() {
    return getSensorCollection().getPulseWidthVelocity();
  }

  /**
   * Get the position of whatever is in the analog pin of the Talon, regardless of whether it is
   * actually being used for feedback.
   *
   * @return the Error code of the request.
   */
  @Override
  public int getQuadraturePosition() {
    return getSensorCollection().getQuadraturePosition();
  }

  /**
   * Get the position of whatever is in the analog pin of the Talon, regardless of whether it is
   * actually being used for feedback.
   *
   * @return the value (0 - 1023) on the analog pin of the Talon.
   */
  @Override
  public int getQuadratureVelocity() {
    return getSensorCollection().getQuadratureVelocity();
  }

  /**
   * Get the selected sensor position.
   *
   * @return Position of selected sensor in raw sensor units per decisecond.
   */
  @Override
  public int getSelectedSensorPosition() {
    return super.getSelectedSensorPosition(defaultPidIdx);
  }

  /**
   * Get the selected sensor velocity.
   *
   * @return Velocity of selected sensor in raw sensor units per decisecond.
   */
  @Override
  public int getSelectedSensorVelocity() {
    return super.getSelectedSensorVelocity(defaultPidIdx);
  }

  @Override
  public int getStatusFramePeriod(StatusFrameEnhanced frame) {
    return super.getStatusFramePeriod(frame, defaultTimeoutMs);
  }

  /**
   * Gets the period of the given status frame.
   *
   * @param frame Frame to get the period of.
   * @return Period of the given status frame.
   */
  @Override
  public int getStatusFramePeriod(int frame) {
    return super.getStatusFramePeriod(frame, defaultTimeoutMs);
  }

  /**
   * Gets the period of the given status frame.
   *
   * @param frame Frame to get the period of.
   * @return Period of the given status frame.
   */
  @Override
  public int getStatusFramePeriod(StatusFrame frame) {
    return super.getStatusFramePeriod(frame, defaultTimeoutMs);
  }

  /**
   * Is forward limit switch closed.
   *
   * @return '1' iff forward limit switch is closed, 0 iff switch is open. This function works
   * regardless if limit switch feature is enabled.
   */
  @Override
  public boolean isFwdLimitSwitchClosed() {
    return getSensorCollection().isFwdLimitSwitchClosed();
  }

  /**
   * Is reverse limit switch closed.
   *
   * @return '1' iff reverse limit switch is closed, 0 iff switch is open. This function works
   * regardless if limit switch feature is enabled.
   */
  @Override
  public boolean isRevLimitSwitchClosed() {
    return getSensorCollection().isRevLimitSwitchClosed();
  }

  /**
   * Selects which profile slot to use for closed-loop control.
   *
   * @param slotIdx Profile slot to select.
   **/
  @Override
  public void selectProfileSlot(int slotIdx) {
    super.selectProfileSlot(slotIdx, defaultPidIdx);
  }

  /**
   * Sets the appropriate output on the talon, depending on the mode. <p> In PercentOutput, the
   * output is between -1.0 and 1.0, with 0.0 as stopped. In Voltage mode, output value is in volts.
   * In Current mode, output value is in amperes. In Speed mode, output value is in position change
   * / 100ms. In Position mode, output value is in  native sensor units. In Velocity mode, output
   * value is in native units per decisecond. In Follower mode, the output value is the integer
   * device ID of the talon to duplicate.
   *
   * @param outputValue The setpoint value, as described above.
   */
  @Override
  public void set(double outputValue) {
    super.set(controlMode, outputValue);
  }

  /**
   * Sets analog position.
   *
   * @param newPosition The new position.
   * @return an ErrorCode.
   */
  @Override
  public ErrorCode setAnalogPosition(int newPosition) {
    return getSensorCollection().setAnalogPosition(newPosition, defaultTimeoutMs);
  }

  /**
   * Sets the mode of operation during neutral throttle output.
   *
   * @param brake Whether or not to brake.
   **/
  @Override
  public void setBrake(boolean brake) {
    super.setNeutralMode(brake ? NeutralMode.Brake : NeutralMode.Coast);
  }

  /**
   * Sets the integral accumulator.
   *
   * @param iaccum Value to set for the integral accumulator.
   * @param pidIdx The PID IDX to use.
   * @return Error Code generated by function. 0 indicates no error.
   */
  @Override
  public ErrorCode setIntegralAccumulator(double iaccum, int pidIdx) {
    return super.setIntegralAccumulator(iaccum, pidIdx, defaultTimeoutMs);
  }

  /**
   * Sets the integral accumulator.
   *
   * @param iaccum Value to set for the integral accumulator.
   * @return Error Code generated by function. 0 indicates no error.
   */
  @Override
  public ErrorCode setIntegralAccumulator(double iaccum) {
    return super.setIntegralAccumulator(iaccum, defaultPidIdx, defaultTimeoutMs);
  }

  /**
   * Sets pulse width position.
   *
   * @param newPosition The position value to apply to the sensor.
   * @return an ErrErrorCode
   */
  @Override
  public ErrorCode setPulseWidthPosition(int newPosition) {
    return getSensorCollection().setPulseWidthPosition(newPosition, defaultTimeoutMs);
  }

  /**
   * Change the quadrature reported position. Typically this is used to "zero" the sensor. This only
   * works with Quadrature sensor. To set the selected sensor position regardless of what type it
   * is, see SetSelectedSensorPosition in the motor controller class.
   *
   * @param newPosition The position value to apply to the sensor.
   * @return error code.
   */
  @Override
  public ErrorCode setQuadraturePosition(int newPosition) {
    return getSensorCollection().setQuadraturePosition(newPosition, defaultTimeoutMs);
  }

  /**
   * Sets the sensor position to the given value.
   *
   * @param sensorPos Position to set for the selected sensor (in Raw Sensor Units).
   * @param pidIdx The PID IDX to use.
   * @return Error Code generated by function. 0 indicates no error.
   */
  @Override
  public ErrorCode setSelectedSensorPosition(int sensorPos, int pidIdx) {
    return super.setSelectedSensorPosition(sensorPos, pidIdx, defaultTimeoutMs);
  }

  /**
   * Sets the sensor position to the given value.
   *
   * @param sensorPos Position to set for the selected sensor (in Raw Sensor Units).
   * @return Error Code generated by function. 0 indicates no error.
   */
  @Override
  public ErrorCode setSelectedSensorPosition(int sensorPos) {
    return super.setSelectedSensorPosition(sensorPos, defaultPidIdx, defaultTimeoutMs);
  }

  public ErrorCode setStatusFramePeriod(StatusFrameEnhanced frame, int periodMs) {
    return super.setStatusFramePeriod(frame, periodMs, defaultTimeoutMs);
  }

  /**
   * Sets the period of the given status frame.
   *
   * @param periodMs Period in ms for the given frame.
   * @param frameValue Frame whose period is to be changed.
   * @return Error Code generated by function. 0 indicates no error.
   */
  @Override
  public ErrorCode setStatusFramePeriod(int frameValue, int periodMs) {
    return super.setStatusFramePeriod(frameValue, periodMs, defaultTimeoutMs);
  }

  /**
   * Sets the period of the given status frame.
   *
   * @param frame Frame whose period is to be changed.
   * @param periodMs Period in ms for the given frame.
   * @return Error Code generated by function. 0 indicates no error.
   */
  @Override
  public ErrorCode setStatusFramePeriod(StatusFrame frame, int periodMs) {
    return super.setStatusFramePeriod(frame, periodMs, defaultTimeoutMs);
  }

  // HERE ON DOWN IS DEPRECATED STUFF FOR COMPATIBILITY PURPOSES
  // Yes, we're aware that CTRE wrote their own compatbility thing, but it's big and sucky so here's this

  /**
   * @deprecated Use {@link #setControlMode(ControlMode)}
   */
  @Deprecated
  public void setControlMode(int mode) {
    changeControlMode(TalonControlMode.valueOf(mode));
  }

  /**
   * @deprecated Use {@link #setControlMode(ControlMode)}
   */
  @SuppressWarnings("DeprecatedIsStillUsed")
  @Deprecated
  public void changeControlMode(TalonControlMode controlMode) {
    if (controlMode.ctrl != null) {
      setControlMode(controlMode.ctrl);
    }
  }

  /**
   * @deprecated Use {@link #config_kP(int, double)}, {@link #config_kI(int, double)}, and {@link
   * #config_kD(int, double)}
   */
  @Deprecated
  public void setPID(double p, double i, double d) {
    config_kP(defaultPidIdx, p);
    config_kI(defaultPidIdx, i);
    config_kD(defaultPidIdx, d);
  }

  /**
   * Set the proportional value of the currently selected profile.
   *
   * @param p Proportional constant for the currently selected PID profile.
   * @deprecated Use {@link #config_kP(int, double)}
   */
  @Deprecated
  public void setP(double p) {
    config_kP(defaultPidIdx, p);
  }

  /**
   * Set the integration constant of the currently selected profile.
   *
   * @param i Integration constant for the currently selected PID profile.
   * @deprecated Use {@link #config_kI(int, double)}
   */
  @Deprecated
  public void setI(double i) {
    config_kI(defaultPidIdx, i);
  }

  /**
   * Set the derivative constant of the currently selected profile.
   *
   * @param d Derivative constant for the currently selected PID profile.
   * @deprecated Use {@link #config_kD(int, double)}
   */
  @Deprecated
  public void setD(double d) {
    config_kD(defaultPidIdx, d);
  }

  /**
   * Set the feedforward value of the currently selected profile.
   *
   * @param f Feedforward constant for the currently selected PID profile.
   * @deprecated Use {@link #config_kF(int, double, int)}
   */
  @Deprecated
  public void setF(double f) {
    config_kF(defaultPidIdx, f);
  }

  /**
   * @deprecated Use {@link #setBrake(boolean)}
   */
  @Deprecated
  public void enableBrakeMode(boolean brake) {
    setBrake(brake);
  }

  /**
   * Configure how many codes per revolution are generated by your encoder.
   *
   * @param codesPerRev The number of counts per revolution.
   * @deprecated Functionality removed.
   */
  @SuppressWarnings("DeprecatedIsStillUsed")
  @Deprecated
  public void configEncoderCodesPerRev(int codesPerRev) {
    this.setEncoderCodesPerRev(codesPerRev);
  }

  /**
   * When using analog sensors, 0 units corresponds to 0V, 1023 units corresponds to 3.3V. When
   * using an analog encoder (wrapping around 1023 to 0 is possible) the units are still 3.3V per
   * 1023 units. When using quadrature, each unit is a quadrature edge (4X) mode.
   *
   * @return The position of the sensor currently providing feedback.
   * @deprecated Use {@link #getSelectedSensorPosition()}
   */
  @Deprecated
  public double getPosition() {
    return getSelectedSensorPosition();
  }

  /**
   * @deprecated Use {@link #setSelectedSensorPosition(int)}
   */
  @Deprecated
  public void setPosition(double pos) {
    setSelectedSensorPosition(Math.toIntExact(Math.round(pos)));
  }

  /**
   * @deprecated Use {@link #configSelectedFeedbackSensor(FeedbackDevice)}
   */
  @SuppressWarnings("DeprecatedIsStillUsed")
  @Deprecated
  public void setFeedbackDevice(FeedbackDevice device) {
    configSelectedFeedbackSensor(device);
  }

  /**
   * Set the voltage ramp rate for the current profile. Limits the rate at which the throttle will
   * change. Affects all modes.
   *
   * @deprecated Use {@link #configOpenloopRamp(double)}
   */
  @Deprecated
  public void setVoltageRampRate(double rampRate) {
    configOpenloopRamp(rampRate);
  }

  /**
   * Select which closed loop profile to use, and uses whatever PIDF gains and the such that are
   * already there.
   *
   * @deprecated Use {@link #selectProfileSlot(int)}
   */
  @Deprecated
  public void setProfile(int profile) {
    selectProfileSlot(profile);
  }

  /**
   * Get the current encoder position, regardless of whether it is the current feedback device.
   *
   * @deprecated Use {@link #getQuadraturePosition()}
   */
  @Deprecated
  public int getEncPosition() {
    return getQuadraturePosition();
  }

  /**
   * Get the current encoder velocity, regardless of whether it is the current feedback device.
   *
   * @deprecated Use {@link #getQuadratureVelocity()}
   */
  @SuppressWarnings("DeprecatedIsStillUsed")
  @Deprecated
  public int getEncVelocity() {
    return getQuadratureVelocity();
  }

  /**
   * Returns the difference between the setpoint and the current position.
   *
   * @return The error in units corresponding to whichever mode we are in.
   * @deprecated Use {@link #getClosedLoopError()}
   */
  @Deprecated
  public double getError() {
    return getClosedLoopError();
  }

  /**
   * Flips the sign (multiplies by negative one) the throttle values going into the motor on the
   * talon in closed loop modes.
   *
   * @param flip True if motor output should be flipped; False if not.
   * @deprecated Use {@link #setInverted(boolean)}
   */
  @SuppressWarnings("DeprecatedIsStillUsed")
  @Deprecated
  public void reverseOutput(boolean flip) {
    setInverted(flip);
  }

  /**
   * Flips the sign (multiplies by negative one) the sensor values going into the talon. This only
   * affects position and velocity closed loop control. Allows for situations where you may have a
   * sensor flipped and going in the wrong direction.
   *
   * @param flip True if sensor input should be flipped; False if not.
   * @deprecated Use {@link #setSensorPhase(boolean)}
   */
  @SuppressWarnings("DeprecatedIsStillUsed")
  public void reverseSensor(boolean flip) {
    setSensorPhase(flip);
  }

  /**
   * @deprecated Use {@link #configNominalOutputForward(double)} and
   * {@link #configNominalOutputReverse(double)}
   */
  @SuppressWarnings("DeprecatedIsStillUsed")
  @Deprecated
  public void configNominalOutputVoltage(double forwardVoltage, double reverseVoltage) {
    configNominalOutputForward(forwardVoltage / 12);
    configNominalOutputReverse(reverseVoltage / 12);
  }

  /**
   * @return The latest value set using set().
   * @deprecated Use {@link #getClosedLoopTarget(int)}
   */
  @Deprecated
  public double getSetpoint() {
    return getClosedLoopTarget(defaultPidIdx);
  }

  /**
   * Calls set(double).
   *
   * @deprecated Use {@link #set(double)}
   */
  @Deprecated
  public void setSetpoint(double setpoint) {
    set(setpoint);
  }

  /**
   * @deprecated Use {@link #configPeakOutputForward(double) and
   * {@link #configPeakOutputReverse(double)}}
   */
  @SuppressWarnings("DeprecatedIsStillUsed")
  @Deprecated
  public void configPeakOutputVoltage(double forwardVoltage, double reverseVoltage) {
    configPeakOutputForward(forwardVoltage / 12);
    configPeakOutputReverse(reverseVoltage / 12);
  }

  /**
   * @return The voltage being output by the Talon, in Volts.
   * @deprecated Use {@link #getMotorOutputVoltage()}
   */
  @SuppressWarnings("DeprecatedIsStillUsed")
  @Deprecated
  public double getOutputVoltage() {
    return getMotorOutputVoltage();
  }

  /**
   * The speed units will be in the sensor's native ticks per 100ms.
   * For analog sensors, 3.3V corresponds to 1023 units. So a speed of 200 equates to ~0.645 dV per
   * 100ms or 6.451 dV per second. If this is an analog encoder, that likely means 1.9548 rotations
   * per sec. For quadrature encoders, each unit corresponds a quadrature edge (4X). So a 250 count
   * encoder will produce 1000 edge events per rotation. An example speed of 200 would then equate
   * to 20% of a rotation per 100ms, or 10 rotations per second.
   *
   * @return The speed of the sensor currently providing feedback.
   * @deprecated Use {@link #getSelectedSensorVelocity()}
   */
  @SuppressWarnings("DeprecatedIsStillUsed")
  @Deprecated
  public double getSpeed() {
    return getSelectedSensorVelocity();
  }

  /**
   * @deprecated Use {@link ControlMode}
   */
  @SuppressWarnings("DeprecatedIsStillUsed")
  @Deprecated
  public enum TalonControlMode {
    PercentVbus(0, ControlMode.PercentOutput),
    Position(1, ControlMode.Position),
    Speed(2, ControlMode.Velocity),
    Current(3, ControlMode.Current),
    Voltage(4, null),
    Follower(5, ControlMode.Follower),
    MotionProfile(6, ControlMode.MotionProfile),
    MotionMagic(7, ControlMode.MotionMagic),
    Disabled(15, ControlMode.Disabled);

    public final int value;
    private final ControlMode ctrl;

    TalonControlMode(int value, ControlMode ctrl) {
      this.value = value;
      this.ctrl = ctrl;
    }

    public static TalonControlMode valueOf(int value) {
      TalonControlMode[] var1 = values();
      int var2 = var1.length;

      for (TalonControlMode mode : var1) {
        if (mode.value == value) {
          return mode;
        }
      }

      return null;
    }

    public boolean isPID() {
      return this == Current || this == Speed || this == Position;
    }

    public int getValue() {
      return this.value;
    }

  }

}