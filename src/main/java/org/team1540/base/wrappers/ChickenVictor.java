package org.team1540.base.wrappers;

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
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

public class ChickenVictor extends VictorSPX {
  ControlMode controlMode = ControlMode.PercentOutput;
  int defaultPidIdx = 0;
  int defaultTimeoutMs = 0;

  public ChickenVictor(int deviceNumber) {
    super(deviceNumber);
  }

  public ErrorCode clearMotionProfileHasUnderrun() {
    return super.clearMotionProfileHasUnderrun(defaultTimeoutMs);
  }

  public ErrorCode clearStickyFaults() {
    return super.clearStickyFaults(defaultTimeoutMs);
  }

  /**
   * Sets the allowable closed-loop error in the given parameter slot.
   *
   * @param slotIdx Parameter slot for the constant.
   * @param allowableClosedLoopError Value of the allowable closed-loop error.
   *
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode configAllowableClosedloopError(int slotIdx, int allowableClosedLoopError) {
    return super
        .configAllowableClosedloopError(slotIdx, allowableClosedLoopError, defaultTimeoutMs);
  }

  /**
   * Configures the closed-loop ramp rate of throttle output.
   *
   * @param secondsFromNeutralToFull Minimum desired time to go from neutral to full throttle. A
   *     value of '0' will disable the ramp.
   *
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode configClosedloopRamp(double secondsFromNeutralToFull) {
    return super.configClosedloopRamp(secondsFromNeutralToFull, defaultTimeoutMs);
  }

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
   *
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode configForwardLimitSwitchSource(RemoteLimitSwitchSource type,
      LimitSwitchNormal normalOpenOrClose, int deviceID) {
    return super
        .configForwardLimitSwitchSource(type, normalOpenOrClose, deviceID, defaultTimeoutMs);
  }

  /**
   * Configures the forward soft limit enable.
   *
   * @param enable Forward Sensor Position Limit Enable.
   *
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode configForwardSoftLimitEnable(boolean enable) {
    return super.configForwardSoftLimitEnable(enable, defaultTimeoutMs);
  }

  /**
   * Configures the forward soft limit threhold.
   *
   * @param forwardSensorLimit Forward Sensor Position Limit (in Raw Sensor Units).
   *
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode configForwardSoftLimitThreshold(int forwardSensorLimit) {
    return super.configForwardSoftLimitThreshold(forwardSensorLimit, defaultTimeoutMs);
  }

  /**
   * Gets the value of a custom parameter.
   *
   * @param paramIndex Index of custom parameter.
   *
   * @return Value of the custom param.
   */
  public int configGetCustomParam(int paramIndex) {
    return super.configGetCustomParam(paramIndex, defaultTimeoutMs);
  }

  /**
   * Gets a parameter.
   *
   * @param param Parameter enumeration.
   * @param ordinal Ordinal of parameter.
   *
   * @return Value of parameter.
   */
  public double configGetParameter(ParamEnum param, int ordinal) {
    return super.configGetParameter(param, ordinal, defaultTimeoutMs);
  }

  /**
   * Gets a parameter.
   *
   * @param param Parameter enumeration.
   * @param ordinal Ordinal of parameter.
   *
   * @return Value of parameter.
   */
  public double configGetParameter(int param, int ordinal) {
    return super.configGetParameter(param, ordinal, defaultTimeoutMs);
  }

  /**
   * Sets the maximum integral accumulator in the given parameter slot.
   *
   * @param slotIdx Parameter slot for the constant.
   * @param iaccum Value of the maximum integral accumulator.
   *
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode configMaxIntegralAccumulator(int slotIdx, double iaccum) {
    return super.configMaxIntegralAccumulator(slotIdx, iaccum, defaultTimeoutMs);
  }

  /**
   * Sets the Motion Magic Acceleration.
   *
   * @param sensorUnitsPer100msPerSec Motion Magic Acceleration (in Raw Sensor Units per 100 ms
   *     per
   *     second).
   *
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode configMotionAcceleration(int sensorUnitsPer100msPerSec) {
    return super.configMotionAcceleration(sensorUnitsPer100msPerSec, defaultTimeoutMs);
  }

  /**
   * Sets the Motion Magic Cruise Velocity.
   *
   * @param sensorUnitsPer100ms Motion Magic Cruise Velocity (in Raw Sensor Units per 100 ms).
   *
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode configMotionCruiseVelocity(int sensorUnitsPer100ms) {
    return super.configMotionCruiseVelocity(sensorUnitsPer100ms, defaultTimeoutMs);
  }

  /**
   * Configures the output deadband percentage.
   *
   * @param percentDeadband Desired deadband percentage. Minimum is 0.1%, Maximum is 25%.
   *     Pass 0.04 for 4%.
   *
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode configNeutralDeadband(double percentDeadband) {
    return super.configNeutralDeadband(percentDeadband, defaultTimeoutMs);
  }

  /**
   * Configures the forward nominal output percentage.
   *
   * @param percentOut Nominal (minimum) percent output.
   *
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode configNominalOutputForward(double percentOut) {
    return super.configNominalOutputForward(percentOut, defaultTimeoutMs);
  }

  /**
   * Configures the reverse nominal output percentage.
   *
   * @param percentOut Nominal (minimum) percent output.
   *
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode configNominalOutputReverse(double percentOut) {
    return super.configNominalOutputReverse(percentOut, defaultTimeoutMs);
  }

  /**
   * Configures the open-loop ramp rate of throttle output.
   *
   * @param secondsFromNeutralToFull Minimum desired time to go from neutral to full throttle. A
   *     value of '0' will disable the ramp.
   *
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode configOpenloopRamp(double secondsFromNeutralToFull) {
    return super.configOpenloopRamp(secondsFromNeutralToFull, defaultTimeoutMs);
  }

  /**
   * Configures the forward peak output percentage.
   *
   * @param percentOut Desired peak output percentage.
   *
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode configPeakOutputForward(double percentOut) {
    return super.configPeakOutputForward(percentOut, defaultTimeoutMs);
  }

  /**
   * Configures the reverse peak output percentage.
   *
   * @param percentOut Desired peak output percentage.
   *
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode configPeakOutputReverse(double percentOut) {
    return super.configPeakOutputReverse(percentOut, defaultTimeoutMs);
  }

  public ErrorCode configRemoteFeedbackFilter(int deviceID, RemoteSensorSource remoteSensorSource,
      int remoteOrdinal) {
    return super
        .configRemoteFeedbackFilter(deviceID, remoteSensorSource, remoteOrdinal, defaultTimeoutMs);
  }

  /**
   * Configures the reverse limit switch for a remote source.
   *
   * @param type Remote limit switch source. @see #LimitSwitchSource
   * @param normalOpenOrClose Setting for normally open or normally closed.
   * @param deviceID Device ID of remote source.
   *
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode configReverseLimitSwitchSource(RemoteLimitSwitchSource type,
      LimitSwitchNormal normalOpenOrClose, int deviceID) {
    return super
        .configReverseLimitSwitchSource(type, normalOpenOrClose, deviceID, defaultTimeoutMs);
  }

  /**
   * Configures the reverse soft limit enable.
   *
   * @param enable Reverse Sensor Position Limit Enable.
   *
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode configReverseSoftLimitEnable(boolean enable) {
    return super.configReverseSoftLimitEnable(enable, defaultTimeoutMs);
  }

  /**
   * Configures the reverse soft limit threshold.
   *
   * @param reverseSensorLimit Reverse Sensor Position Limit (in Raw Sensor Units).
   *
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode configReverseSoftLimitThreshold(int reverseSensorLimit) {
    return super.configReverseSoftLimitThreshold(reverseSensorLimit, defaultTimeoutMs);
  }

  /**
   * Select the remote feedback device for the motor controller.
   *
   * @param feedbackDevice Remote Feedback Device to select.
   * @param pidIdx 0 for Primary closed-loop. 1 for cascaded closed-loop.
   *
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode configSelectedFeedbackSensor(RemoteFeedbackDevice feedbackDevice, int pidIdx) {
    return super.configSelectedFeedbackSensor(feedbackDevice, pidIdx, defaultTimeoutMs);
  }

  /**
   * Select the remote feedback device for the motor controller.
   *
   * @param feedbackDevice Remote Feedback Device to select.
   *
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode configSelectedFeedbackSensor(RemoteFeedbackDevice feedbackDevice) {
    return super.configSelectedFeedbackSensor(feedbackDevice, defaultPidIdx, defaultTimeoutMs);
  }

  /**
   * Select the feedback device for the motor controller.
   *
   * @param feedbackDevice Feedback Device to select.
   * @param pidIdx The PID IDX to use.
   *
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode configSelectedFeedbackSensor(FeedbackDevice feedbackDevice, int pidIdx) {
    return super.configSelectedFeedbackSensor(feedbackDevice, pidIdx, defaultTimeoutMs);
  }

  /**
   * Select the feedback device for the motor controller.
   *
   * @param feedbackDevice Feedback Device to select.
   *
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode configSelectedFeedbackSensor(FeedbackDevice feedbackDevice) {
    return super.configSelectedFeedbackSensor(feedbackDevice, defaultPidIdx, defaultTimeoutMs);
  }

  public ErrorCode configSensorTerm(SensorTerm sensorTerm, FeedbackDevice feedbackDevice) {
    return super.configSensorTerm(sensorTerm, feedbackDevice, defaultTimeoutMs);
  }

  /**
   * Sets the value of a custom parameter.
   *
   * @param newValue Value for custom parameter.
   * @param paramIndex Index of custom parameter.
   *
   * @return Error Code generated by function. 0 indicates no error.
   */
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
   *
   * @return Error Code generated by function. 0 indicates no error.
   */
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
   *
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode configSetParameter(int param, double value, int subValue, int ordinal) {
    return super.configSetParameter(param, value, subValue, ordinal, defaultTimeoutMs);
  }

  /**
   * Sets the period over which velocity measurements are taken.
   *
   * @param period Desired period for the velocity measurement. @see
   *     #VelocityMeasPeriod
   *
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode configVelocityMeasurementPeriod(int period) {
    int retval = MotControllerJNI
        .ConfigVelocityMeasurementPeriod(m_handle, period, defaultTimeoutMs);
    return ErrorCode.valueOf(retval);
  }

  public ErrorCode configVelocityMeasurementWindow(int windowSize) {
    return super.configVelocityMeasurementWindow(windowSize, defaultTimeoutMs);
  }

  /**
   * Configures the Voltage Compensation saturation voltage.
   *
   * @param voltage TO-DO: Comment me!
   *
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode configVoltageCompSaturation(double voltage) {
    return super.configVoltageCompSaturation(voltage, defaultTimeoutMs);
  }

  /**
   * Configures the voltage measurement filter.
   *
   * @param filterWindowSamples Number of samples in the rolling average of voltage
   *     measurement.
   *
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode configVoltageMeasurementFilter(int filterWindowSamples) {
    return super.configVoltageMeasurementFilter(filterWindowSamples, defaultTimeoutMs);
  }

  /**
   * Sets the Integral Zone constant in the given parameter slot.
   *
   * @param slotIdx Parameter slot for the constant.
   * @param izone Value of the Integral Zone constant.
   *
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode config_IntegralZone(int slotIdx, int izone) {
    return super.config_IntegralZone(slotIdx, izone, defaultTimeoutMs);
  }

  /**
   * Sets the 'D' constant in the given parameter slot.
   *
   * @param slotIdx Parameter slot for the constant.
   * @param value Value of the D constant.
   *
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode config_kD(int slotIdx, double value) {
    return super.config_kD(slotIdx, value, defaultTimeoutMs);
  }

  /**
   * Sets the 'F' constant in the given parameter slot.
   *
   * @param slotIdx Parameter slot for the constant.
   * @param value Value of the F constant.
   *
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode config_kF(int slotIdx, double value) {
    return super.config_kF(slotIdx, value, defaultTimeoutMs);
  }

  /**
   * Sets the 'I' constant in the given parameter slot.
   *
   * @param slotIdx Parameter slot for the constant.
   * @param value Value of the I constant.
   *
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode config_kI(int slotIdx, double value) {
    return super.config_kI(slotIdx, value, defaultTimeoutMs);
  }

  /**
   * Sets the 'P' constant in the given parameter slot.
   *
   * @param slotIdx Parameter slot for the constant.
   * @param value Value of the P constant.
   *
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode config_kP(int slotIdx, double value) {
    return super.config_kP(slotIdx, value, defaultTimeoutMs);
  }

  /**
   * Get the position of whatever is in the analog pin of the Talon,
   * regardless of whether it is actually being used for feedback.
   *
   * @return the 24bit analog value. The bottom ten bits is the ADC (0 - 1023)
   *     on the analog pin of the Talon. The upper 14 bits tracks the
   *     overflows and underflows (continuous sensor).
   */
  public int getAnalogIn() {
    return getSensorCollection().getAnalogIn();
  }

  /**
   * Get the position of whatever is in the analog pin of the Talon,
   * regardless of whether it is actually being used for feedback.
   *
   * @return the ADC (0 - 1023) on analog pin of the Talon.
   */
  public int getAnalogInRaw() {
    return getSensorCollection().getAnalogInRaw();
  }

  /**
   * Get the position of whatever is in the analog pin of the Talon,
   * regardless of whether it is actually being used for feedback.
   *
   * @return the value (0 - 1023) on the analog pin of the Talon.
   */
  public int getAnalogInVel() {
    return getSensorCollection().getAnalogInVel();
  }

  /**
   * Gets the closed-loop error.
   *
   * @return Closed-loop error value.
   */
  public int getClosedLoopError() {
    return super.getClosedLoopError(defaultPidIdx);
  }

  /**
   * Note that this does <i>not</i> return the currently set control mode in the talon wrapper and
   * may be different than the control mode set using {@link #setControlMode(ControlMode)}.
   *
   * @return The current control mode in the motor controller.
   */
  public ControlMode getControlMode() {
    return controlMode;
  }

  public void setControlMode(ControlMode controlMode) {
    this.controlMode = controlMode;
  }

  public int getDefaultPidIdx() {
    return defaultPidIdx;
  }

  public void setDefaultPidIdx(int defaultPidIdx) {
    this.defaultPidIdx = defaultPidIdx;
  }

  public int getDefaultTimeoutMs() {
    return defaultTimeoutMs;
  }

  public void setDefaultTimeoutMs(int defaultTimeoutMs) {
    this.defaultTimeoutMs = defaultTimeoutMs;
  }

  /**
   * Gets the encoder codes per revolution.
   *
   * @return {@code 0}.
   * @deprecated This no longer has any effect as natural units functionality has been removed.
   */
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
  @Deprecated
  public void setEncoderCodesPerRev(double encoderCodesPerRev) {
  }

  /**
   * Gets the derivative of the closed-loop error.
   *
   * @return The error derivative value.
   */
  public double getErrorDerivative() {
    return super.getErrorDerivative(defaultPidIdx);
  }

  /**
   * Gets the iaccum value.
   *
   * @return Integral accumulator value.
   */
  public double getIntegralAccumulator() {
    return super.getIntegralAccumulator(defaultPidIdx);
  }

  /**
   * Gets pin state quad a.
   *
   * @return the pin state quad a.
   */
  public boolean getPinStateQuadA() {
    return getSensorCollection().getPinStateQuadA();
  }

  /**
   * Gets pin state quad b.
   *
   * @return Digital level of QUADB pin.
   */
  public boolean getPinStateQuadB() {
    return getSensorCollection().getPinStateQuadB();
  }

  /**
   * Gets pin state quad index.
   *
   * @return Digital level of QUAD Index pin.
   */
  public boolean getPinStateQuadIdx() {
    return getSensorCollection().getPinStateQuadIdx();
  }

  /**
   * Gets pulse width position.
   *
   * @return the pulse width position.
   */
  public int getPulseWidthPosition() {
    return getSensorCollection().getPulseWidthPosition();
  }

  /**
   * Gets pulse width rise to fall us.
   *
   * @return the pulse width rise to fall us.
   */
  public int getPulseWidthRiseToFallUs() {
    return getSensorCollection().getPulseWidthRiseToFallUs();
  }

  /**
   * Gets pulse width rise to rise us.
   *
   * @return the pulse width rise to rise us.
   */
  public int getPulseWidthRiseToRiseUs() {
    return getSensorCollection().getPulseWidthRiseToRiseUs();
  }

  /**
   * Gets pulse width velocity.
   *
   * @return the pulse width velocity.
   */
  public int getPulseWidthVelocity() {
    return getSensorCollection().getPulseWidthVelocity();
  }

  /**
   * Get the position of whatever is in the analog pin of the Talon,
   * regardless of whether it is actually being used for feedback.
   *
   * @return the Error code of the request.
   */
  public int getQuadraturePosition() {
    return getSensorCollection().getQuadraturePosition();
  }

  /**
   * Get the position of whatever is in the analog pin of the Talon,
   * regardless of whether it is actually being used for feedback.
   *
   * @return the value (0 - 1023) on the analog pin of the Talon.
   */
  public int getQuadratureVelocity() {
    return getSensorCollection().getQuadratureVelocity();
  }

  /**
   * Get the selected sensor position.
   *
   * @return Position of selected sensor in raw sensor units per decisecond.
   */
  public double getSelectedSensorPosition() {
    return super.getSelectedSensorPosition(defaultPidIdx);
  }

  /**
   * Get the selected sensor velocity.
   *
   * @return Velocity of selected sensor in raw sensor units per decisecond.
   */
  public double getSelectedSensorVelocity() {
    return super.getSelectedSensorVelocity(defaultPidIdx) * 600;
  }

  public int getStatusFramePeriod(StatusFrameEnhanced frame) {
    return super.getStatusFramePeriod(frame, defaultTimeoutMs);
  }

  /**
   * Gets the period of the given status frame.
   *
   * @param frame Frame to get the period of.
   *
   * @return Period of the given status frame.
   */
  public int getStatusFramePeriod(int frame) {
    return super.getStatusFramePeriod(frame, defaultTimeoutMs);
  }

  /**
   * Gets the period of the given status frame.
   *
   * @param frame Frame to get the period of.
   *
   * @return Period of the given status frame.
   */
  public int getStatusFramePeriod(StatusFrame frame) {
    return super.getStatusFramePeriod(frame, defaultTimeoutMs);
  }

  /**
   * Is forward limit switch closed.
   *
   * @return '1' iff forward limit switch is closed, 0 iff switch is open.
   *     This function works regardless if limit switch feature is
   *     enabled.
   */
  public boolean isFwdLimitSwitchClosed() {
    return getSensorCollection().isFwdLimitSwitchClosed();
  }

  /**
   * Is reverse limit switch closed.
   *
   * @return '1' iff reverse limit switch is closed, 0 iff switch is open.
   *     This function works regardless if limit switch feature is
   *     enabled.
   */
  public boolean isRevLimitSwitchClosed() {
    return getSensorCollection().isRevLimitSwitchClosed();
  }

  /**
   * Selects which profile slot to use for closed-loop control.
   *
   * @param slotIdx Profile slot to select.
   **/
  public void selectProfileSlot(int slotIdx) {
    super.selectProfileSlot(slotIdx, defaultPidIdx);
  }

  /**
   * Sets the appropriate output on the talon, depending on the mode.
   * <p>
   * In PercentOutput, the output is between -1.0 and 1.0, with 0.0 as
   * stopped. In Voltage mode, output value is in volts. In Current mode,
   * output value is in amperes. In Speed mode, output value is in position
   * change / 100ms. In Position mode, output value is in  native sensor units.
   * In Velocity mode, output value is in native units per decisecond. In Follower mode,
   * the output value is the integer device ID of the talon to duplicate.
   *
   * @param outputValue The setpoint value, as described above.
   */
  public void set(double outputValue) {
    super.set(controlMode, outputValue);
  }

  /**
   * Sets analog position.
   *
   * @param newPosition The new position.
   *
   * @return an ErrorCode.
   */
  public ErrorCode setAnalogPosition(int newPosition) {
    return getSensorCollection().setAnalogPosition(newPosition, defaultTimeoutMs);
  }

  /**
   * Sets the mode of operation during neutral throttle output.
   *
   * @param brake Whether or not to brake.
   **/
  public void setBrake(boolean brake) {
    super.setNeutralMode(brake ? NeutralMode.Brake : NeutralMode.Coast);
  }

  /**
   * Sets the integral accumulator.
   *
   * @param iaccum Value to set for the integral accumulator.
   * @param pidIdx The PID IDX to use.
   *
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode setIntegralAccumulator(double iaccum, int pidIdx) {
    return super.setIntegralAccumulator(iaccum, pidIdx, defaultTimeoutMs);
  }

  /**
   * Sets the integral accumulator.
   *
   * @param iaccum Value to set for the integral accumulator.
   *
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode setIntegralAccumulator(double iaccum) {
    return super.setIntegralAccumulator(iaccum, defaultPidIdx, defaultTimeoutMs);
  }

  /**
   * Sets pulse width position.
   *
   * @param newPosition The position value to apply to the sensor.
   *
   * @return an ErrErrorCode
   */
  public ErrorCode setPulseWidthPosition(int newPosition) {
    return getSensorCollection().setPulseWidthPosition(newPosition, defaultTimeoutMs);
  }

  /**
   * Change the quadrature reported position. Typically this is used to "zero"
   * the sensor. This only works with Quadrature sensor. To set the selected
   * sensor position regardless of what type it is, see
   * SetSelectedSensorPosition in the motor controller class.
   *
   * @param newPosition The position value to apply to the sensor.
   *
   * @return error code.
   */
  public ErrorCode setQuadraturePosition(int newPosition) {
    return getSensorCollection().setQuadraturePosition(newPosition, defaultTimeoutMs);
  }

  /**
   * Sets the sensor position to the given value.
   *
   * @param sensorPos Position to set for the selected sensor (in Raw Sensor Units).
   * @param pidIdx The PID IDX to use.
   *
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode setSelectedSensorPosition(int sensorPos, int pidIdx) {
    return super.setSelectedSensorPosition(sensorPos, pidIdx, defaultTimeoutMs);
  }

  /**
   * Sets the sensor position to the given value.
   *
   * @param sensorPos Position to set for the selected sensor (in Raw Sensor Units).
   *
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode setSelectedSensorPosition(int sensorPos) {
    return super.setSelectedSensorPosition(sensorPos, defaultPidIdx, defaultTimeoutMs);
  }

  /**
   * Sets the period of the given status frame.
   *
   * @param periodMs Period in ms for the given frame.
   * @param frameValue Frame whose period is to be changed.
   *
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode setStatusFramePeriod(int frameValue, int periodMs) {
    return super.setStatusFramePeriod(frameValue, periodMs, defaultTimeoutMs);
  }

  /**
   * Sets the period of the given status frame.
   *
   * @param frame Frame whose period is to be changed.
   * @param periodMs Period in ms for the given frame.
   *
   * @return Error Code generated by function. 0 indicates no error.
   */
  public ErrorCode setStatusFramePeriod(StatusFrame frame, int periodMs) {
    return super.setStatusFramePeriod(frame, periodMs, defaultTimeoutMs);
  }
}