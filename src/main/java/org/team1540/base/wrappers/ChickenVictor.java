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

public class ChickenVictor extends VictorSPX implements ChickenController {
  ControlMode controlMode = ControlMode.PercentOutput;
  int defaultPidIdx = 0;
  int defaultTimeoutMs = 0;

  public ChickenVictor(int deviceNumber) {
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

  @Override
  public ErrorCode configAllowableClosedloopError(int slotIdx, int allowableClosedLoopError) {
    return super
        .configAllowableClosedloopError(slotIdx, allowableClosedLoopError, defaultTimeoutMs);
  }

  @Override
  public ErrorCode configClosedloopRamp(double secondsFromNeutralToFull) {
    return super.configClosedloopRamp(secondsFromNeutralToFull, defaultTimeoutMs);
  }

  @Override
  public ErrorCode configForwardLimitSwitchSource(LimitSwitchSource type,
      LimitSwitchNormal normalOpenOrClose) {
    return super.configForwardLimitSwitchSource(type, normalOpenOrClose, defaultTimeoutMs);
  }

  @Override
  public ErrorCode configForwardLimitSwitchSource(RemoteLimitSwitchSource type,
      LimitSwitchNormal normalOpenOrClose, int deviceID) {
    return super
        .configForwardLimitSwitchSource(type, normalOpenOrClose, deviceID, defaultTimeoutMs);
  }

  @Override
  public ErrorCode configForwardSoftLimitEnable(boolean enable) {
    return super.configForwardSoftLimitEnable(enable, defaultTimeoutMs);
  }

  @Override
  public ErrorCode configForwardSoftLimitThreshold(int forwardSensorLimit) {
    return super.configForwardSoftLimitThreshold(forwardSensorLimit, defaultTimeoutMs);
  }

  @Override
  public int configGetCustomParam(int paramIndex) {
    return super.configGetCustomParam(paramIndex, defaultTimeoutMs);
  }

  @Override
  public double configGetParameter(ParamEnum param, int ordinal) {
    return super.configGetParameter(param, ordinal, defaultTimeoutMs);
  }

  @Override
  public double configGetParameter(int param, int ordinal) {
    return super.configGetParameter(param, ordinal, defaultTimeoutMs);
  }

  @Override
  public ErrorCode configMaxIntegralAccumulator(int slotIdx, double iaccum) {
    return super.configMaxIntegralAccumulator(slotIdx, iaccum, defaultTimeoutMs);
  }

  @Override
  public ErrorCode configMotionAcceleration(int sensorUnitsPer100msPerSec) {
    return super.configMotionAcceleration(sensorUnitsPer100msPerSec, defaultTimeoutMs);
  }

  @Override
  public ErrorCode configMotionCruiseVelocity(int sensorUnitsPer100ms) {
    return super.configMotionCruiseVelocity(sensorUnitsPer100ms, defaultTimeoutMs);
  }

  @Override
  public ErrorCode configNeutralDeadband(double percentDeadband) {
    return super.configNeutralDeadband(percentDeadband, defaultTimeoutMs);
  }

  @Override
  public ErrorCode configNominalOutputForward(double percentOut) {
    return super.configNominalOutputForward(percentOut, defaultTimeoutMs);
  }

  @Override
  public ErrorCode configNominalOutputReverse(double percentOut) {
    return super.configNominalOutputReverse(percentOut, defaultTimeoutMs);
  }

  @Override
  public ErrorCode configOpenloopRamp(double secondsFromNeutralToFull) {
    return super.configOpenloopRamp(secondsFromNeutralToFull, defaultTimeoutMs);
  }

  @Override
  public ErrorCode configPeakOutputForward(double percentOut) {
    return super.configPeakOutputForward(percentOut, defaultTimeoutMs);
  }

  @Override
  public ErrorCode configPeakOutputReverse(double percentOut) {
    return super.configPeakOutputReverse(percentOut, defaultTimeoutMs);
  }

  @Override
  public ErrorCode configRemoteFeedbackFilter(int deviceID, RemoteSensorSource remoteSensorSource,
      int remoteOrdinal) {
    return super
        .configRemoteFeedbackFilter(deviceID, remoteSensorSource, remoteOrdinal, defaultTimeoutMs);
  }

  @Override
  public ErrorCode configReverseLimitSwitchSource(RemoteLimitSwitchSource type,
      LimitSwitchNormal normalOpenOrClose, int deviceID) {
    return super
        .configReverseLimitSwitchSource(type, normalOpenOrClose, deviceID, defaultTimeoutMs);
  }

  @Override
  public ErrorCode configReverseSoftLimitEnable(boolean enable) {
    return super.configReverseSoftLimitEnable(enable, defaultTimeoutMs);
  }

  @Override
  public ErrorCode configReverseSoftLimitThreshold(int reverseSensorLimit) {
    return super.configReverseSoftLimitThreshold(reverseSensorLimit, defaultTimeoutMs);
  }

  @Override
  public ErrorCode configSelectedFeedbackSensor(RemoteFeedbackDevice feedbackDevice, int pidIdx) {
    return super.configSelectedFeedbackSensor(feedbackDevice, pidIdx, defaultTimeoutMs);
  }

  @Override
  public ErrorCode configSelectedFeedbackSensor(RemoteFeedbackDevice feedbackDevice) {
    return super.configSelectedFeedbackSensor(feedbackDevice, defaultPidIdx, defaultTimeoutMs);
  }

  @Override
  public ErrorCode configSelectedFeedbackSensor(FeedbackDevice feedbackDevice, int pidIdx) {
    return super.configSelectedFeedbackSensor(feedbackDevice, pidIdx, defaultTimeoutMs);
  }

  @Override
  public ErrorCode configSelectedFeedbackSensor(FeedbackDevice feedbackDevice) {
    return super.configSelectedFeedbackSensor(feedbackDevice, defaultPidIdx, defaultTimeoutMs);
  }

  @Override
  public ErrorCode configSensorTerm(SensorTerm sensorTerm, FeedbackDevice feedbackDevice) {
    return super.configSensorTerm(sensorTerm, feedbackDevice, defaultTimeoutMs);
  }

  @Override
  public ErrorCode configSetCustomParam(int newValue, int paramIndex) {
    return super.configSetCustomParam(newValue, paramIndex, defaultTimeoutMs);
  }

  @Override
  public ErrorCode configSetParameter(ParamEnum param, double value, int subValue, int ordinal) {
    return super.configSetParameter(param, value, subValue, ordinal, defaultTimeoutMs);
  }

  @Override
  public ErrorCode configSetParameter(int param, double value, int subValue, int ordinal) {
    return super.configSetParameter(param, value, subValue, ordinal, defaultTimeoutMs);
  }

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

  @Override
  public ErrorCode configVoltageCompSaturation(double voltage) {
    return super.configVoltageCompSaturation(voltage, defaultTimeoutMs);
  }

  @Override
  public ErrorCode configVoltageMeasurementFilter(int filterWindowSamples) {
    return super.configVoltageMeasurementFilter(filterWindowSamples, defaultTimeoutMs);
  }

  @Override
  public ErrorCode config_IntegralZone(int slotIdx, int izone) {
    return super.config_IntegralZone(slotIdx, izone, defaultTimeoutMs);
  }

  @Override
  public ErrorCode config_kD(int slotIdx, double value) {
    return super.config_kD(slotIdx, value, defaultTimeoutMs);
  }

  @Override
  public ErrorCode config_kF(int slotIdx, double value) {
    return super.config_kF(slotIdx, value, defaultTimeoutMs);
  }

  @Override
  public ErrorCode config_kI(int slotIdx, double value) {
    return super.config_kI(slotIdx, value, defaultTimeoutMs);
  }

  @Override
  public ErrorCode config_kP(int slotIdx, double value) {
    return super.config_kP(slotIdx, value, defaultTimeoutMs);
  }

  @Override
  public int getAnalogIn() {
    return getSensorCollection().getAnalogIn();
  }

  @Override
  public int getAnalogInRaw() {
    return getSensorCollection().getAnalogInRaw();
  }

  @Override
  public int getAnalogInVel() {
    return getSensorCollection().getAnalogInVel();
  }

  @Override
  public int getClosedLoopError() {
    return super.getClosedLoopError(defaultPidIdx);
  }

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

  @Override
  @Deprecated
  public double getEncoderCodesPerRev() {
    return 0;
  }

  @Override
  @Deprecated
  public void setEncoderCodesPerRev(double encoderCodesPerRev) {
  }

  @Override
  public double getErrorDerivative() {
    return super.getErrorDerivative(defaultPidIdx);
  }

  @Override
  public double getIntegralAccumulator() {
    return super.getIntegralAccumulator(defaultPidIdx);
  }

  @Override
  public boolean getPinStateQuadA() {
    return getSensorCollection().getPinStateQuadA();
  }

  @Override
  public boolean getPinStateQuadB() {
    return getSensorCollection().getPinStateQuadB();
  }

  @Override
  public boolean getPinStateQuadIdx() {
    return getSensorCollection().getPinStateQuadIdx();
  }

  @Override
  public int getPulseWidthPosition() {
    return getSensorCollection().getPulseWidthPosition();
  }

  @Override
  public int getPulseWidthRiseToFallUs() {
    return getSensorCollection().getPulseWidthRiseToFallUs();
  }

  @Override
  public int getPulseWidthRiseToRiseUs() {
    return getSensorCollection().getPulseWidthRiseToRiseUs();
  }

  @Override
  public int getPulseWidthVelocity() {
    return getSensorCollection().getPulseWidthVelocity();
  }

  @Override
  public int getQuadraturePosition() {
    return getSensorCollection().getQuadraturePosition();
  }

  @Override
  public int getQuadratureVelocity() {
    return getSensorCollection().getQuadratureVelocity();
  }

  @Override
  public double getSelectedSensorPosition() {
    return super.getSelectedSensorPosition(defaultPidIdx);
  }

  @Override
  public double getSelectedSensorVelocity() {
    return super.getSelectedSensorVelocity(defaultPidIdx) * 600;
  }

  @Override
  public int getStatusFramePeriod(StatusFrameEnhanced frame) {
    return super.getStatusFramePeriod(frame, defaultTimeoutMs);
  }

  @Override
  public int getStatusFramePeriod(int frame) {
    return super.getStatusFramePeriod(frame, defaultTimeoutMs);
  }

  @Override
  public int getStatusFramePeriod(StatusFrame frame) {
    return super.getStatusFramePeriod(frame, defaultTimeoutMs);
  }

  @Override
  public boolean isFwdLimitSwitchClosed() {
    return getSensorCollection().isFwdLimitSwitchClosed();
  }

  @Override
  public boolean isRevLimitSwitchClosed() {
    return getSensorCollection().isRevLimitSwitchClosed();
  }

  @Override
  public void selectProfileSlot(int slotIdx) {
    super.selectProfileSlot(slotIdx, defaultPidIdx);
  }

  @Override
  public void set(double outputValue) {
    super.set(controlMode, outputValue);
  }

  @Override
  public ErrorCode setAnalogPosition(int newPosition) {
    return getSensorCollection().setAnalogPosition(newPosition, defaultTimeoutMs);
  }

  @Override
  public void setBrake(boolean brake) {
    super.setNeutralMode(brake ? NeutralMode.Brake : NeutralMode.Coast);
  }

  @Override
  public ErrorCode setIntegralAccumulator(double iaccum, int pidIdx) {
    return super.setIntegralAccumulator(iaccum, pidIdx, defaultTimeoutMs);
  }

  @Override
  public ErrorCode setIntegralAccumulator(double iaccum) {
    return super.setIntegralAccumulator(iaccum, defaultPidIdx, defaultTimeoutMs);
  }

  @Override
  public ErrorCode setPulseWidthPosition(int newPosition) {
    return getSensorCollection().setPulseWidthPosition(newPosition, defaultTimeoutMs);
  }

  @Override
  public ErrorCode setQuadraturePosition(int newPosition) {
    return getSensorCollection().setQuadraturePosition(newPosition, defaultTimeoutMs);
  }

  @Override
  public ErrorCode setSelectedSensorPosition(int sensorPos, int pidIdx) {
    return super.setSelectedSensorPosition(sensorPos, pidIdx, defaultTimeoutMs);
  }

  @Override
  public ErrorCode setSelectedSensorPosition(int sensorPos) {
    return super.setSelectedSensorPosition(sensorPos, defaultPidIdx, defaultTimeoutMs);
  }

  @Override
  public ErrorCode setStatusFramePeriod(int frameValue, int periodMs) {
    return super.setStatusFramePeriod(frameValue, periodMs, defaultTimeoutMs);
  }

  @Override
  public ErrorCode setStatusFramePeriod(StatusFrame frame, int periodMs) {
    return super.setStatusFramePeriod(frame, periodMs, defaultTimeoutMs);
  }
}
