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
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

public class ChickenVictor extends VictorSPX {

  ControlMode controlMode = ControlMode.PercentOutput;
  int defaultPidIdx = 0;
  int defaultTimeoutMs = 0;
  private double peakOutputForward = 1;
  private double peakOutputReverse = -1;

  public ChickenVictor(int deviceNumber) {
    super(deviceNumber);
  }

  public ErrorCode clearMotionProfileHasUnderrun() {
    return super.clearMotionProfileHasUnderrun(defaultTimeoutMs);
  }

  public ErrorCode clearStickyFaults() {
    return super.clearStickyFaults(defaultTimeoutMs);
  }

  public ErrorCode configAllowableClosedloopError(int slotIdx, int allowableClosedLoopError) {
    return super
        .configAllowableClosedloopError(slotIdx, allowableClosedLoopError, defaultTimeoutMs);
  }

  public ErrorCode configClosedloopRamp(double secondsFromNeutralToFull) {
    return super.configClosedloopRamp(secondsFromNeutralToFull, defaultTimeoutMs);
  }

  public ErrorCode configOpenloopRamp(double secondsFromNeutralToFull) {
    return super.configOpenloopRamp(secondsFromNeutralToFull, defaultTimeoutMs);
  }

  public ErrorCode configForwardLimitSwitchSource(LimitSwitchSource type,
      LimitSwitchNormal normalOpenOrClose) {
    return super.configForwardLimitSwitchSource(type, normalOpenOrClose, defaultTimeoutMs);
  }

  public ErrorCode configForwardLimitSwitchSource(RemoteLimitSwitchSource type,
      LimitSwitchNormal normalOpenOrClose, int deviceID) {
    return super
        .configForwardLimitSwitchSource(type, normalOpenOrClose, deviceID, defaultTimeoutMs);
  }

  public ErrorCode configForwardSoftLimitEnable(boolean enable) {
    return super.configForwardSoftLimitEnable(enable, defaultTimeoutMs);
  }

  public ErrorCode configForwardSoftLimitThreshold(int forwardSensorLimit) {
    return super.configForwardSoftLimitThreshold(forwardSensorLimit, defaultTimeoutMs);
  }

  public int configGetCustomParam(int paramIndex) {
    return super.configGetCustomParam(paramIndex, defaultTimeoutMs);
  }

  public double configGetParameter(ParamEnum param, int ordinal) {
    return super.configGetParameter(param, ordinal, defaultTimeoutMs);
  }

  public double configGetParameter(int param, int ordinal) {
    return super.configGetParameter(param, ordinal, defaultTimeoutMs);
  }

  public ErrorCode configMaxIntegralAccumulator(int slotIdx, double iaccum) {
    return super.configMaxIntegralAccumulator(slotIdx, iaccum, defaultTimeoutMs);
  }

  public ErrorCode configMotionAcceleration(int sensorUnitsPer100msPerSec) {
    return super.configMotionAcceleration(sensorUnitsPer100msPerSec, defaultTimeoutMs);
  }

  public ErrorCode configMotionCruiseVelocity(int sensorUnitsPer100ms) {
    return super.configMotionCruiseVelocity(sensorUnitsPer100ms, defaultTimeoutMs);
  }

  public ErrorCode configNeutralDeadband(double percentDeadband) {
    return super.configNeutralDeadband(percentDeadband, defaultTimeoutMs);
  }

  public ErrorCode configNominalOutputForward(double percentOut) {
    return super.configNominalOutputForward(percentOut, defaultTimeoutMs);
  }

  public ErrorCode configNominalOutputReverse(double percentOut) {
    return super.configNominalOutputReverse(percentOut, defaultTimeoutMs);
  }

  public ErrorCode configPeakOutputForward(double percentOut) {
    peakOutputForward = percentOut;
    return super.configPeakOutputForward(percentOut, defaultTimeoutMs);
  }

  public ErrorCode configPeakOutputReverse(double percentOut) {
    peakOutputReverse = percentOut;
    return super.configPeakOutputReverse(percentOut, defaultTimeoutMs);
  }

  public double getPeakOutputForward() {
    return peakOutputForward;
  }

  public double getPeakOutputReverse() {
    return peakOutputReverse;
  }

  public ErrorCode configRemoteFeedbackFilter(int deviceID, RemoteSensorSource remoteSensorSource,
      int remoteOrdinal) {
    return super
        .configRemoteFeedbackFilter(deviceID, remoteSensorSource, remoteOrdinal, defaultTimeoutMs);
  }

  public ErrorCode configReverseLimitSwitchSource(RemoteLimitSwitchSource type,
      LimitSwitchNormal normalOpenOrClose, int deviceID) {
    return super
        .configReverseLimitSwitchSource(type, normalOpenOrClose, deviceID, defaultTimeoutMs);
  }

  public ErrorCode configReverseSoftLimitEnable(boolean enable) {
    return super.configReverseSoftLimitEnable(enable, defaultTimeoutMs);
  }

  public ErrorCode configReverseSoftLimitThreshold(int reverseSensorLimit) {
    return super.configReverseSoftLimitThreshold(reverseSensorLimit, defaultTimeoutMs);
  }

  public ErrorCode configSelectedFeedbackSensor(RemoteFeedbackDevice feedbackDevice, int pidIdx) {
    return super.configSelectedFeedbackSensor(feedbackDevice, pidIdx, defaultTimeoutMs);
  }

  public ErrorCode configSelectedFeedbackSensor(RemoteFeedbackDevice feedbackDevice) {
    return super.configSelectedFeedbackSensor(feedbackDevice, defaultPidIdx, defaultTimeoutMs);
  }

  public ErrorCode configSelectedFeedbackSensor(FeedbackDevice feedbackDevice, int pidIdx) {
    return super.configSelectedFeedbackSensor(feedbackDevice, pidIdx, defaultTimeoutMs);
  }

  public ErrorCode configSelectedFeedbackSensor(FeedbackDevice feedbackDevice) {
    return super.configSelectedFeedbackSensor(feedbackDevice, defaultPidIdx, defaultTimeoutMs);
  }

  public ErrorCode configSensorTerm(SensorTerm sensorTerm, FeedbackDevice feedbackDevice) {
    return super.configSensorTerm(sensorTerm, feedbackDevice, defaultTimeoutMs);
  }

  public ErrorCode configSetCustomParam(int newValue, int paramIndex) {
    return super.configSetCustomParam(newValue, paramIndex, defaultTimeoutMs);
  }

  public ErrorCode configSetParameter(ParamEnum param, double value, int subValue, int ordinal) {
    return super.configSetParameter(param, value, subValue, ordinal, defaultTimeoutMs);
  }

  public ErrorCode configSetParameter(int param, double value, int subValue, int ordinal) {
    return super.configSetParameter(param, value, subValue, ordinal, defaultTimeoutMs);
  }

  public ErrorCode configVelocityMeasurementPeriod(int period) {
    int retval = MotControllerJNI
        .ConfigVelocityMeasurementPeriod(m_handle, period, defaultTimeoutMs);
    return ErrorCode.valueOf(retval);
  }

  public ErrorCode configVelocityMeasurementWindow(int windowSize) {
    return super.configVelocityMeasurementWindow(windowSize, defaultTimeoutMs);
  }

  public ErrorCode configVoltageCompSaturation(double voltage) {
    return super.configVoltageCompSaturation(voltage, defaultTimeoutMs);
  }

  public ErrorCode configVoltageMeasurementFilter(int filterWindowSamples) {
    return super.configVoltageMeasurementFilter(filterWindowSamples, defaultTimeoutMs);
  }

  public ErrorCode config_IntegralZone(int slotIdx, int izone) {
    return super.config_IntegralZone(slotIdx, izone, defaultTimeoutMs);
  }

  public ErrorCode config_kD(int slotIdx, double value) {
    return super.config_kD(slotIdx, value, defaultTimeoutMs);
  }

  public ErrorCode config_kF(int slotIdx, double value) {
    return super.config_kF(slotIdx, value, defaultTimeoutMs);
  }

  public ErrorCode config_kI(int slotIdx, double value) {
    return super.config_kI(slotIdx, value, defaultTimeoutMs);
  }

  public ErrorCode config_kP(int slotIdx, double value) {
    return super.config_kP(slotIdx, value, defaultTimeoutMs);
  }

  public int getClosedLoopError() {
    return super.getClosedLoopError(defaultPidIdx);
  }

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

  @Deprecated
  public double getEncoderCodesPerRev() {
    return 0;
  }

  @Deprecated
  public void setEncoderCodesPerRev(double encoderCodesPerRev) {
  }

  public double getErrorDerivative() {
    return super.getErrorDerivative(defaultPidIdx);
  }

  public double getIntegralAccumulator() {
    return super.getIntegralAccumulator(defaultPidIdx);
  }

  public int getSelectedSensorPosition() {
    return super.getSelectedSensorPosition(defaultPidIdx);
  }

  public int getSelectedSensorVelocity() {
    return super.getSelectedSensorVelocity(defaultPidIdx);
  }

  public int getStatusFramePeriod(StatusFrameEnhanced frame) {
    return super.getStatusFramePeriod(frame, defaultTimeoutMs);
  }

  public int getStatusFramePeriod(int frame) {
    return super.getStatusFramePeriod(frame, defaultTimeoutMs);
  }

  public int getStatusFramePeriod(StatusFrame frame) {
    return super.getStatusFramePeriod(frame, defaultTimeoutMs);
  }

  public void selectProfileSlot(int slotIdx) {
    super.selectProfileSlot(slotIdx, defaultPidIdx);
  }

  public void set(double outputValue) {
    super.set(controlMode, outputValue);
  }

  public void setBrake(boolean brake) {
    super.setNeutralMode(brake ? NeutralMode.Brake : NeutralMode.Coast);
  }

  public ErrorCode setIntegralAccumulator(double iaccum, int pidIdx) {
    return super.setIntegralAccumulator(iaccum, pidIdx, defaultTimeoutMs);
  }

  public ErrorCode setIntegralAccumulator(double iaccum) {
    return super.setIntegralAccumulator(iaccum, defaultPidIdx, defaultTimeoutMs);
  }

  public ErrorCode setSelectedSensorPosition(int sensorPos, int pidIdx) {
    return super.setSelectedSensorPosition(sensorPos, pidIdx, defaultTimeoutMs);
  }

  public ErrorCode setSelectedSensorPosition(int sensorPos) {
    return super.setSelectedSensorPosition(sensorPos, defaultPidIdx, defaultTimeoutMs);
  }

  public ErrorCode setStatusFramePeriod(int frameValue, int periodMs) {
    return super.setStatusFramePeriod(frameValue, periodMs, defaultTimeoutMs);
  }

  public ErrorCode setStatusFramePeriod(StatusFrame frame, int periodMs) {
    return super.setStatusFramePeriod(frame, periodMs, defaultTimeoutMs);
  }
}
